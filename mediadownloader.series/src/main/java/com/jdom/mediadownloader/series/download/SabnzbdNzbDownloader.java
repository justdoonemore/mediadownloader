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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.mediadownloader.series.util.SeriesUtil;
import com.jdom.mediadownloader.services.ConfigurationManagerService;
import com.jdom.mediadownloader.services.UrlDownloadService;
import com.jdom.util.file.FileUtils;
import com.jdom.util.file.FileWrapper;
import com.jdom.util.file.filter.ExcludeStartsWith;
import com.jdom.util.time.Duration;

public class SabnzbdNzbDownloader implements NzbDownloader {

	private static final Logger LOG = Logger
			.getLogger(SabnzbdNzbDownloader.class);

	public static final String UNPACK_PREFIX = "_UNPACK";

	/**
	 * The duration at which if the file last modified time is within, it won't
	 * be picked up.
	 */
	private static final Duration TIME_AGO_LAST_MODIFIED = Duration
			.getDuration("file.pickup.last.modified", new Duration(2,
					TimeUnit.MINUTES));

	protected final ConfigurationManagerService configurationManager;

	protected final UrlDownloadService urlDownloadService;

	private final FileFilter downloadedNzbsFilter;

	private final List<SeriesDownloadListener> seriesDownloadListeners = new CopyOnWriteArrayList<SeriesDownloadListener>();

	private final DownloadedNzbMover downloadedNzbMover;

	private final NzbAdder nzbAdder;

	/**
	 * Sets up the downloadedNzbsFilter to exclude directories with specific
	 * prefixes. Currently this is used to ignore the directories whose contents
	 * are still being unpacked.
	 */
	public SabnzbdNzbDownloader(final SeriesDasFactory dasFactory,
			final ConfigurationManagerService configurationManager,
			final UrlDownloadService urlDownloadService,
			DownloadedNzbMover downloadedNzbMover, NzbAdder nzbAdder) {
		this.configurationManager = configurationManager;
		this.urlDownloadService = urlDownloadService;
		this.downloadedNzbMover = downloadedNzbMover;
		this.nzbAdder = nzbAdder;

		// Prepare the exclusions filter
		Collection<String> exclusionPrefixes = new HashSet<String>();
		exclusionPrefixes.add(UNPACK_PREFIX);

		downloadedNzbsFilter = new ExcludeStartsWith(exclusionPrefixes);
	}

	@Override
	public void downloadNzb(SeriesDownload download) {
		if (addNzbForDownload(download)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Added %s to the download queue.",
						download));
			}
		} else {
			LOG.warn("Unable to add the NZB to the download queue!");
		}
	}

	/**
	 * Add an NZB for processing
	 * 
	 * @param seriesDownload
	 *            the series download
	 */
	private boolean addNzbForDownload(SeriesDownload seriesDownload) {
		URL url = seriesDownload.getLink();

		if (url == null) {
			return false;
		}

		// Get the contents of the URL
		String contents = urlDownloadService.downloadUrlContents(url
				.toExternalForm());

		return nzbAdder.addNzb(seriesDownload, contents.getBytes());
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

		notifySeriesDownloadListeners(seriesList);

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
				if (downloadedNzbMover.moveSeries(tvDirectory,
						timeAgoLastModified, directoryWithDownload,
						downloadedEpisodeName, series)) {
					seriesList.add(series);
				}
			} else {
				boolean movedMovie = downloadedNzbMover.moveMovie(
						timeAgoLastModified, directoryWithDownload, new File(
								moviesDirectory, downloadedEpisodeName));

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
	 * 
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.series.download.NzbDownloader#addSeriesDownloadListener(com.jdom.mediadownloader.series.download.SeriesDownloadListener)
	 */
	@Override
	public SeriesDownloadListener addSeriesDownloadListener(
			SeriesDownloadListener listener) {
		seriesDownloadListeners.add(listener);
		return listener;
	}

	/**
	 * Sends series updates.
	 * 
	 * @param seriesList
	 *            the list of series to update
	 */
	private void notifySeriesDownloadListeners(List<Series> seriesList) {
		for (Series series : seriesList) {
			for (SeriesDownloadListener listener : seriesDownloadListeners) {
				listener.downloadComplete(series);
			}
		}
	}
}
