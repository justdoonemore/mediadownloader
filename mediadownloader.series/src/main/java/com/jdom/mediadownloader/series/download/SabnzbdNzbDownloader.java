/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.mediadownloader.series.download;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.domain.SeriesEpisodeComparator;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.mediadownloader.series.util.SeriesUtil;
import com.jdom.mediadownloader.services.ConfigurationManagerService;
import com.jdom.mediadownloader.services.SeriesDASService;
import com.jdom.mediadownloader.services.SeriesNotifierService;
import com.jdom.mediadownloader.services.UrlDownloadService;
import com.jdom.util.compare.CompareUtil;
import com.jdom.util.file.FileUtils;
import com.jdom.util.file.FileWrapper;
import com.jdom.util.file.filter.ExcludeStartsWith;
import com.jdom.util.time.Duration;

public class SabnzbdNzbDownloader implements NzbDownloader {

	private static final Logger LOG = Logger
			.getLogger(SabnzbdNzbDownloader.class);

	public static final String UNPACK_PREFIX = "_UNPACK";

	private static final SeriesEpisodeComparator SERIES_EPISODE_COMPARATOR = new SeriesEpisodeComparator();

	/**
	 * The duration at which if the file last modified time is within, it won't
	 * be picked up.
	 */
	private static final Duration TIME_AGO_LAST_MODIFIED = Duration
			.getDuration("file.pickup.last.modified", new Duration(2,
					TimeUnit.MINUTES));

	private final SeriesDasFactory dasFactory;

	protected final ConfigurationManagerService configurationManager;

	protected final UrlDownloadService urlDownloadService;

	private final FileFilter downloadedNzbsFilter;

	private final SeriesNotifierService seriesNotifier;

	protected final File nzbQueueDirectory;

	/**
	 * Sets up the downloadedNzbsFilter to exclude directories with specific
	 * prefixes. Currently this is used to ignore the directories whose contents
	 * are still being unpacked.
	 */
	public SabnzbdNzbDownloader(final SeriesDasFactory dasFactory,
			final ConfigurationManagerService configurationManager,
			final UrlDownloadService urlDownloadService,
			final SeriesNotifierService seriesNotifier) {
		this.dasFactory = dasFactory;
		this.configurationManager = configurationManager;
		this.urlDownloadService = urlDownloadService;
		this.seriesNotifier = seriesNotifier;

		// Prepare the exclusions filter
		Collection<String> exclusionPrefixes = new HashSet<String>();
		exclusionPrefixes.add(UNPACK_PREFIX);

		downloadedNzbsFilter = new ExcludeStartsWith(exclusionPrefixes);

		// The destination directory to write .nzb files to
		nzbQueueDirectory = configurationManager.getNzbDestinationDirectory();
	}

	@Override
	public void downloadNzb(SeriesDownload download) {
		if (writeNzbToDisk(download, nzbQueueDirectory)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Wrote %s to the nzb downloder queue.",
						download));
			}
		} else {
			LOG.warn("Unable to write the NZB to ["
					+ nzbQueueDirectory.getAbsolutePath() + "]");
		}
	}

	/**
	 * Write an NZB to disk
	 * 
	 * @param seriesDownload
	 *            the series download
	 * @param nzbQueueDirectory
	 *            the destination directory
	 */
	private boolean writeNzbToDisk(SeriesDownload seriesDownload,
			File destinationDirectory) {
		URL url = seriesDownload.getLink();

		if (url == null) {
			return false;
		}

		// Get the contents of the URL
		String contents = urlDownloadService.downloadUrlContents(url
				.toExternalForm());

		// Create a file and temp file which we'll write first then rename
		File finalFile = new File(destinationDirectory,
				seriesDownload.getNzbTitle());

		// Write the temp file to disk
		return FileUtils.writeFileToDisk(finalFile, contents, true);
	}

	@Override
	public List<Series> processDownloadedItems() {
		final File downloadedDirectory = configurationManager
				.getNzbDownloadedDirectory();
		final File tvDirectory = configurationManager.getArchivedTvDirectory();
		final File moviesDirectory = configurationManager
				.getArchivedMoviesDirectory();

		List<Series> seriesList = handleRetrievedNzbs(downloadedDirectory,
				tvDirectory, moviesDirectory, TIME_AGO_LAST_MODIFIED);

		performPostDownloadActions(seriesList);

		return seriesList;
	}

	/**
	 * Handles the retrieved nzbs.
	 * 
	 * @param downloadedDirectory
	 *            the directory where nzb contents were downloaded to
	 * @param tvDirectory
	 *            the directory to place tv shows in
	 * @param moviesDirectory
	 *            the directory to place movies in
	 * @param timeAgoLastModified
	 *            how long ago the last modified time must be before, for the
	 *            file to be picked up
	 * @return A list of series to be updated
	 */
	protected List<Series> handleRetrievedNzbs(File downloadedDirectory,
			File tvDirectory, File moviesDirectory, Duration timeAgoLastModified) {
		// Get a list of all contents of the downloaded directory,
		// and exclude the ones being unpacked
		Collection<File> downloads = FileUtils.getDirectoriesFromDirectory(
				downloadedDirectory, false, downloadedNzbsFilter);
		List<Series> seriesList = new ArrayList<Series>();

		// Look for any series
		for (File candidate : downloads) {
			FileWrapper directoryWithDownload = new FileWrapper(candidate);

			final String downloadedEpisodeName = directoryWithDownload
					.getName();

			Series series = SeriesUtil.parseSeries(downloadedEpisodeName);

			// If a series was found
			if (series != null) {

				String show = series.getName();

				File destinationSeriesFolder = new File(tvDirectory, show);

				boolean movedSeries = moveContents(timeAgoLastModified,
						directoryWithDownload, destinationSeriesFolder);

				if (movedSeries) {
					seriesList.add(series);

					// Now rename the files moved in that match the folder name
					Collection<File> filesToRename = FileUtils
							.getFilesAndDirectories(destinationSeriesFolder,
									true, false, false,
									new SeriesEpisodeFileFilter(
											downloadedEpisodeName));

					for (File fileToRename : filesToRename) {
						FileWrapper file = new FileWrapper(fileToRename);

						fileToRename.renameTo(new File(destinationSeriesFolder,
								series.toDownloadedEpisodeNamingString()
										+ file.getExtension()));
					}
				}
			} else {
				boolean movedMovie = moveContents(timeAgoLastModified,
						directoryWithDownload, new File(moviesDirectory,
								downloadedEpisodeName));

				if (!movedMovie && LOG.isDebugEnabled()) {
					LOG.debug("Skipping moving movie [" + downloadedEpisodeName
							+ "]");
				}
			}
		}

		// Sort the list in ascending order by season/episode
		Collections.sort(seriesList);

		return seriesList;
	}

	/**
	 * Moves the contents to the appropriate directory.
	 * 
	 * @param timeAgoLastModified
	 * 
	 * @param sourceDir
	 *            the source directory
	 * @param destination
	 *            the target directory
	 * @return true if the contents were moved
	 */
	private boolean moveContents(Duration timeAgoLastModified,
			FileWrapper sourceDir, File destination) {

		boolean movedContents = false;

		// If we should move the directory contents
		boolean contentsReadyToBeMoved = !sourceDir
				.hasBeenModifiedSince(timeAgoLastModified.toMillis().value);

		if (contentsReadyToBeMoved) {

			if (!destination.exists() && !destination.mkdirs()) {
				throw new IllegalArgumentException(
						"Unable to create new directory [" + destination + "]");
			}

			sourceDir.moveTo(destination, true);

			// If we reach here then we successfully moved the contents
			movedContents = true;
		}

		return movedContents;
	}

	/**
	 * Sends series updates.
	 * 
	 * @param seriesList
	 *            the list of series to update
	 */
	private void performPostDownloadActions(List<Series> seriesList) {

		// If the list of series is not empty, send the list to update to the
		// service
		if (!seriesList.isEmpty()) {

			// Create the list of series update objects
			List<Series> seriesUpdateObjects = new ArrayList<Series>(
					seriesList.size());

			// For each series create the update object
			for (Series series : seriesList) {

				String name = series.getName();

				// Look up the actual series object based on the show for an
				// update
				SeriesDASService seriesDas = dasFactory.getSeriesDAS();

				Series seriesObj = seriesDas.getSeriesByName(name);

				if (seriesObj != null) {

					seriesObj = prepareSeriesUpdate(seriesObj, series);

					if (LOG.isDebugEnabled()) {
						LOG.debug("Updating Series to " + seriesObj);
					}

					seriesUpdateObjects.add(seriesObj);
				} else {
					LOG.warn("Unable to find a series by name [" + name + "]");
				}
			}

			sendSeriesUpdates(seriesUpdateObjects);
		}
	}

	private void sendSeriesUpdates(List<Series> seriesUpdateObjects) {

		SeriesDASService service = dasFactory.getSeriesDAS();

		for (Series series : seriesUpdateObjects) {
			service.updateObject(series);
		}

		seriesNotifier.sendEmails(seriesUpdateObjects);
	}

	/**
	 * Prepares the series update to occur.
	 * 
	 * @param seriesObj
	 *            the current db object
	 * @param series
	 *            the downloaded series instance
	 */
	protected Series prepareSeriesUpdate(Series seriesObj, Series series) {
		if (SERIES_EPISODE_COMPARATOR.compare(series, seriesObj) > CompareUtil.LESS_THAN) {
			seriesObj.setSeason(series.getSeason());
			seriesObj.setEpisode(series.getEpisode() + 1);
		}

		return seriesObj;
	}

	static final class SeriesEpisodeFileFilter implements FileFilter {

		private final String filename;

		/**
		 * Default Constructor.
		 * 
		 * 
		 * @param filename
		 *            the filename
		 */
		SeriesEpisodeFileFilter(String filename) {
			this.filename = filename;
		}

		@Override
		public boolean accept(File arg0) {
			return arg0.getName().startsWith(this.filename);
		}
	}
}
