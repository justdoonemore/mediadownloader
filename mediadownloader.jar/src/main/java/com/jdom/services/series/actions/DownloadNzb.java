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
package com.jdom.services.series.actions;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.jdom.mediadownloader.domain.Series;
import com.jdom.mediadownloader.services.ConfigurationManagerService;
import com.jdom.mediadownloader.services.UrlDownloadService;
import com.jdom.services.series.download.SeriesDownload;
import com.jdom.services.series.download.util.SeriesDownloadUtil;
import com.jdom.services.util.ServiceLocator;
import com.jdom.util.file.FileUtils;

public class DownloadNzb {

	private static final long SLEEP_TIME_BETWEEN_NZB_DOWNLOADS = Long.getLong(
			"sleep.time.between.nzb.downloads", 2000L);
	private static final Logger LOG = Logger.getLogger(DownloadNzb.class);

	private final UrlDownloadService urlDownloadService;

	public DownloadNzb(UrlDownloadService urlDownloadService) {
		this.urlDownloadService = urlDownloadService;
	}

	/**
	 * Accepts an array of SeriesDownload objects as the payload, and downloads
	 * each of the series. It will then update the database entity to look for
	 * the next episode.
	 * 
	 * @param message
	 *            The message
	 */
	public void downloadNzbs(Collection<SeriesDownload> downloads) {

		ConfigurationManagerService configurationManager = ServiceLocator
				.getConfigurationManager();

		File destinationDirectory = configurationManager
				.getNzbDestinationDirectory();

		writeNzbsToDisk(downloads, destinationDirectory);
	}

	/**
	 * Write the NZBs to disk.
	 * 
	 * @param downloads
	 *            the message
	 * @param destinationDirectory
	 *            the destination directory
	 */
	private void writeNzbsToDisk(Collection<SeriesDownload> seriesDownloads,
			File destinationDirectory) {

		// Download each nzb and update the series object
		for (SeriesDownload seriesDownload : seriesDownloads) {
			// Get the series in question
			Series series = seriesDownload.getSeries();

			// First things first, make sure the series is not already in the
			// download queue
			if (SeriesDownloadUtil.containsSeries(series)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("The series " + series
							+ " is already in the download queue, skipping...");
				}
				continue;
			} else {
				addSeriesToDownloadQueue(destinationDirectory, seriesDownload,
						series);
			}

			if (SLEEP_TIME_BETWEEN_NZB_DOWNLOADS > 0) {
				try {

					if (LOG.isDebugEnabled()) {
						LOG.debug("Sleeping for "
								+ SLEEP_TIME_BETWEEN_NZB_DOWNLOADS
								+ " ms until next download.");
					}
					Thread.sleep(SLEEP_TIME_BETWEEN_NZB_DOWNLOADS);
				} catch (InterruptedException e) {
					LOG.error("Exception while sleeping", e);
				}
			}
		}
	}

	private void addSeriesToDownloadQueue(File destinationDirectory,
			SeriesDownload seriesDownload, Series series) {
		// Place the series download into the queue
		if (SeriesDownloadUtil.addSeries(series)) {
			if (writeNzbToDisk(seriesDownload, destinationDirectory)) {
				LOG.info(String.format(
						"Added series %s to the download queue.", series));
			} else {
				LOG.warn("Unable to write the NZB to ["
						+ destinationDirectory.getAbsolutePath() + "]");
			}
		} else {
			LOG.warn("Unable to add Series " + series
					+ " to the series download queue");
		}
	}

	/**
	 * Write an NZB to disk
	 * 
	 * @param seriesDownload
	 *            the series download
	 * @param destinationDirectory
	 *            the destination directory
	 */
	protected boolean writeNzbToDisk(SeriesDownload seriesDownload,
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
}
