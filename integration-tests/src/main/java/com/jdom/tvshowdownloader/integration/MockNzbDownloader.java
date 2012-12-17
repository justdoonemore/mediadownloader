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
 */package com.jdom.tvshowdownloader.integration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.jdom.tvshowdownloader.ejb.ConfigurationManager;

public class MockNzbDownloader extends Thread {

	private final ConfigurationManager configurationManager;

	private final File scanDir;
	private final File destinationDir;

	public volatile boolean stop;

	public MockNzbDownloader(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
		scanDir = configurationManager.getNzbDestinationDirectory();
		destinationDir = configurationManager.getNzbDownloadedDirectory();
	}

	@Override
	public void run() {
		while (!stop) {
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
						org.apache.commons.io.FileUtils.moveFileToDirectory(
								file, destinationFolder, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
