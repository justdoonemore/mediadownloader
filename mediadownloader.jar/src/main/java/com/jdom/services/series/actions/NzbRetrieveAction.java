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
 */package com.jdom.services.series.actions;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.jdom.services.series.download.util.SeriesDownloadUtil;
import com.jdom.services.series.util.SeriesUtil;
import com.jdom.services.util.ServiceLocator;
import com.jdom.tvshowdownloader.domain.Series;
import com.jdom.tvshowdownloader.domain.SeriesEpisodeComparator;
import com.jdom.tvshowdownloader.ejb.ConfigurationManagerService;
import com.jdom.tvshowdownloader.ejb.SeriesDASService;
import com.jdom.util.compare.CompareUtil;
import com.jdom.util.file.FileUtils;
import com.jdom.util.file.FileWrapper;
import com.jdom.util.file.filter.ExcludeStartsWith;
import com.jdom.util.time.TimeConstants;

/**
 * Organizes downloaded NZBs.
 */
public class NzbRetrieveAction {

	private static final Logger LOG = Logger.getLogger(NzbRetrieveAction.class);

	public static final String UNPACK_PREFIX = "_UNPACK";

	private static final SeriesEpisodeComparator SERIES_EPISODE_COMPARATOR = new SeriesEpisodeComparator();

	private final FileFilter downloadedNzbsFilter;

	/**
	 * Sets up the downloadedNzbsFilter to exclude directories with specific
	 * prefixes. Currently this is used to ignore the directories whose contents
	 * are still being unpacked.
	 */
	public NzbRetrieveAction() {
		// Prepare the exclusions filter
		Collection<String> exclusionPrefixes = new HashSet<String>();
		exclusionPrefixes.add(UNPACK_PREFIX);

		downloadedNzbsFilter = new ExcludeStartsWith(exclusionPrefixes);
	}

	public void moveDownloadedVideos() {
		ConfigurationManagerService cm = ServiceLocator
				.getConfigurationManager();

		final File downloadedDirectory = cm.getNzbDownloadedDirectory();
		final File tvDirectory = cm.getArchivedTvDirectory();
		final File moviesDirectory = cm.getArchivedMoviesDirectory();
		final int lastModifiedInMinutes = cm.getLastModifiedTime();

		long lastModifiedInMillis = lastModifiedInMinutes
				* TimeConstants.MILLIS_PER_MINUTE;

		List<Series> seriesList = handleRetrievedNzbs(downloadedDirectory,
				tvDirectory, moviesDirectory, lastModifiedInMillis);

		performPostDownloadActions(seriesList);
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
	 * @param lastModifiedInMillis
	 *            how long ago the last modified time should be in milliseconds
	 * @return A list of series to be updated
	 */
	protected List<Series> handleRetrievedNzbs(File downloadedDirectory,
			File tvDirectory, File moviesDirectory, long lastModifiedInMillis) {
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

				boolean movedSeries = moveContents(lastModifiedInMillis,
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
				boolean movedMovie = moveContents(lastModifiedInMillis,
						directoryWithDownload, new File(moviesDirectory,
								downloadedEpisodeName));

				if (!movedMovie && LOG.isDebugEnabled()) {
					LOG.debug("Skipping moving movie [" + downloadedEpisodeName
							+ "]");
				}
			}
		}

		return seriesList;
	}

	/**
	 * Moves the contents to the appropriate directory.
	 * 
	 * @param cm
	 * 
	 * @param sourceDir
	 *            the source directory
	 * @param destination
	 *            the target directory
	 * @return true if the contents were moved
	 */
	private boolean moveContents(long lastModifiedInMillis,
			FileWrapper sourceDir, File destination) {

		boolean movedContents = false;

		// If we should move the directory contents
		boolean contentsReadyToBeMoved = !sourceDir
				.hasBeenModifiedSince(lastModifiedInMillis);

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
				SeriesDASService seriesDas = ServiceLocator.getSeriesDAS();

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

			// After delivering the series updates and we received a
			// response we can remove the series from the download queue
			removeFromDownloadQueue(seriesList);
		}
	}

	private void sendSeriesUpdates(List<Series> seriesUpdateObjects) {

		SeriesDASService service = ServiceLocator.getSeriesDAS();

		for (Series series : seriesUpdateObjects) {
			service.updateObject(series);
		}

		new SendEmailNotifications().sendEmails(seriesUpdateObjects);
	}

	private void removeFromDownloadQueue(List<Series> seriesList) {
		for (Series series : seriesList) {
			// Remove the series from the queue
			if (!(SeriesDownloadUtil.removeSeries(series))) {
				LOG.warn("Unable to remove the series [" + series
						+ "] from the queue");
			}
		}
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
