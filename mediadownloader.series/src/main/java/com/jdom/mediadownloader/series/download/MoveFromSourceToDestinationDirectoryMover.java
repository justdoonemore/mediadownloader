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
import java.util.Collection;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.util.file.FileUtils;
import com.jdom.util.file.FileWrapper;
import com.jdom.util.time.Duration;

/**
 * @author djohnson
 * 
 */
public class MoveFromSourceToDestinationDirectoryMover implements
		DownloadedNzbMover {

	/**
	 * @param tvDirectory
	 * @param timeAgoLastModified
	 * @param directoryWithDownload
	 * @param downloadedEpisodeName
	 * @param series
	 */
	@Override
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
	@Override
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
