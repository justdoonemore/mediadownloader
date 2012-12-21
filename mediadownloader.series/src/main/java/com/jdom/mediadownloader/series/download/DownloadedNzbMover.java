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

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.util.file.FileWrapper;
import com.jdom.util.time.Duration;

/**
 * @author djohnson
 * 
 */
public interface DownloadedNzbMover {

	/**
	 * @param tvDirectory
	 * @param timeAgoLastModified
	 * @param directoryWithDownload
	 * @param downloadedEpisodeName
	 * @param series
	 * @return true if moved
	 */
	boolean moveSeries(File tvDirectory, Duration timeAgoLastModified,
			FileWrapper directoryWithDownload, String downloadedEpisodeName,
			Series series);

	/**
	 * @param timeAgoLastModified
	 * @param directoryWithDownload
	 * @param destination
	 * @return
	 */
	boolean moveMovie(Duration timeAgoLastModified,
			FileWrapper directoryWithDownload, File destination);

}
