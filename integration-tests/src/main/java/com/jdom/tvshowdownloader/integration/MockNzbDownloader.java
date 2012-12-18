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
package com.jdom.tvshowdownloader.integration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.jdom.mediadownloader.services.ConfigurationManager;
import com.jdom.services.series.download.NzbDownloader;

public class MockNzbDownloader implements NzbDownloader {

	private final ConfigurationManager configurationManager;

	public MockNzbDownloader(final ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	@Override
	public void downloadNzbs() {
		final File scanDir = configurationManager.getNzbDestinationDirectory();
		final File destinationDir = configurationManager
				.getNzbDownloadedDirectory();

		String[] nzbs = scanDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".nzb");
			}
		});
		if (nzbs != null) {
			for (String string : nzbs) {
				File file = new File(scanDir, string);
				File destinationFolder = new File(destinationDir, file
						.getName().replaceAll(".nzb", ""));
				try {
					org.apache.commons.io.FileUtils.moveFileToDirectory(file,
							destinationFolder, true);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

}
