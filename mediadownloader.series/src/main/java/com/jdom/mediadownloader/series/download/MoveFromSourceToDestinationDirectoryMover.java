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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.jdom.logging.api.LogFactory;
import com.jdom.logging.api.Logger;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.util.SeriesUtil;
import com.jdom.util.file.FileUtils;
import com.jdom.util.file.FileWrapper;
import com.jdom.util.file.filter.ExcludeStartsWith;
import com.jdom.util.time.Duration;

/**
 * @author djohnson
 * 
 */
public class MoveFromSourceToDestinationDirectoryMover implements
		DownloadedNzbMover {

	private static final Logger LOG = LogFactory
			.getLogger(MoveFromSourceToDestinationDirectoryMover.class);

	public static final String UNPACK_PREFIX = "_UNPACK";

	private final FileFilter downloadedNzbsFilter;

	private final File moviesDirectory;

	private final Duration timeAgoLastModified;

	private final File tvDirectory;

	private final File downloadedDirectory;

	/**
	 * @param downloadedDirectory
	 *            the directory where nzb contents were downloaded to
	 * @param tvDirectory
	 *            the directory to place tv shows in
	 * @param moviesDirectory
	 *            the directory to place movies in
	 * @param timeAgoLastModified
	 *            how long ago the last modified time must be before, for the
	 *            file to be picked up
	 */
	public MoveFromSourceToDestinationDirectoryMover(File downloadedDirectory,
			File tvDirectory, File moviesDirectory, Duration timeAgoLastModified) {
		this.downloadedDirectory = downloadedDirectory;
		this.tvDirectory = tvDirectory;
		this.moviesDirectory = moviesDirectory;
		this.timeAgoLastModified = timeAgoLastModified;

		// Prepare the exclusions filter
		Collection<String> exclusionPrefixes = new HashSet<String>();
		exclusionPrefixes.add(UNPACK_PREFIX);

		downloadedNzbsFilter = new ExcludeStartsWith(exclusionPrefixes);
	}

	/**
	 * Handles the retrieved nzbs.
	 * 
	 * 
	 * @return A list of series to be updated
	 */
	@Override
	public List<Series> handleRetrievedNzbs() {
		// Get a list of all contents of the downloaded directory,
		// and exclude the ones being unpacked
		if (LOG.isDebugEnabled()) {
			LOG.debug("Looking for downloads in directory ["
					+ downloadedDirectory.getAbsolutePath() + "]");
		}

		Collection<File> downloads = FileUtils.getDirectoriesFromDirectory(
				downloadedDirectory, false, downloadedNzbsFilter);
		List<Series> seriesList = new ArrayList<Series>();

		// Look for any series
		for (File candidate : downloads) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Found file [" + candidate.getAbsolutePath() + "]");
			}

			FileWrapper directoryWithDownload = new FileWrapper(candidate);

			final String downloadedEpisodeName = directoryWithDownload
					.getName();

			Series series = SeriesUtil.parseSeries(downloadedEpisodeName);

			// If a series was found
			if (series != null) {
				if (moveSeries(tvDirectory, timeAgoLastModified,
						directoryWithDownload, downloadedEpisodeName, series)) {
					seriesList.add(series);
				}
			} else {
				boolean movedMovie = moveMovie(timeAgoLastModified,
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
	 * @param tvDirectory
	 * @param timeAgoLastModified
	 * @param directoryWithDownload
	 * @param downloadedEpisodeName
	 * @param series
	 */
	public boolean moveSeries(File tvDirectory, Duration timeAgoLastModified,
			FileWrapper directoryWithDownload, String downloadedEpisodeName,
			Series series) {
		String show = series.getName();

		File destinationSeriesFolder = new File(tvDirectory, show);

		boolean movedSeries = moveContents(timeAgoLastModified,
				directoryWithDownload, destinationSeriesFolder);

		if (movedSeries) {
			// Now rename the files moved in that match the folder name
			Collection<File> filesToRename = FileUtils.getFilesAndDirectories(
					destinationSeriesFolder, true, false, false,
					new SeriesEpisodeFileFilter(downloadedEpisodeName));

			for (File fileToRename : filesToRename) {
				FileWrapper file = new FileWrapper(fileToRename);

				fileToRename.renameTo(new File(destinationSeriesFolder, series
						.toDownloadedEpisodeNamingString()
						+ file.getExtension()));
			}
		}

		return movedSeries;
	}

	/**
	 * @param timeAgoLastModified
	 * @param directoryWithDownload
	 * @param destination
	 * @return
	 */
	public boolean moveMovie(Duration timeAgoLastModified,
			FileWrapper directoryWithDownload, File destination) {
		return moveContents(timeAgoLastModified, directoryWithDownload,
				destination);
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

	private static final class SeriesEpisodeFileFilter implements FileFilter {

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
