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

import com.google.common.io.Files;
import com.jdom.mediadownloader.series.domain.SeriesDownload;

import java.io.File;
import java.io.IOException;

/**
 * @author djohnson
 * 
 */
public class FileQueueNzbAdder implements NzbAdder {

	private final File destinationDirectory;

	public FileQueueNzbAdder(File destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.series.download.NzbAdder#addNzb(com.jdom.mediadownloader.series.domain.SeriesDownload,
	 *      byte[])
	 */
	@Override
	public boolean addNzb(SeriesDownload download, byte[] bytes) {

		// Create a file and temp file which we'll write first then rename
		File finalFile = new File(destinationDirectory, download.getNzbTitle());

		// Write the temp file to disk
		try {
			Files.write(bytes, finalFile);
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
