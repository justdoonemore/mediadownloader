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
package com.jdom.mediadownloader;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdom.mediadownloader.ApplicationLock.LockException;
import com.jdom.mediadownloader.api.MediaProcessor;
import com.jdom.mediadownloader.api.MediaProcessorRegistry;
import com.jdom.util.properties.PropertiesUtil;

public class MediaDownloader {

	private static final Logger LOG = Logger.getLogger(MediaDownloader.class);

	private static ClassPathXmlApplicationContext ctx;

	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException(
					"You must pass the location to the properties file to the application!");
		}

		File file = new File(args[0]);

		System.getProperties().putAll(PropertiesUtil.readPropertiesFile(file));

		initializeContext();

		MediaDownloader mediaDownloader = ctx.getBean(MediaDownloader.class);
		mediaDownloader.processDownloads();
	}

	public static void initializeContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext(new String[] {
					"/mediadownloader-core.xml",
					"/mediadownloader-core-db.xml",
					"/mediadownloader-series.xml",
					"/mediadownloader-series-db.xml" });
		}
	}

	public static void closeContext() {
		if (ctx != null) {
			ctx.close();
			ctx = null;
		}
	}

	private final MediaProcessorRegistry mediaProcessorRegistry;

	private final ApplicationLock applicationLock;

	private MediaDownloader(ApplicationLock applicationLock,
			MediaProcessorRegistry mediaProcessorRegistry) {
		this.applicationLock = applicationLock;
		this.mediaProcessorRegistry = mediaProcessorRegistry;
	}

	private void processDownloads() {
		try {
			if (applicationLock.tryLock()) {
				for (MediaProcessor processor : mediaProcessorRegistry
						.getRegistered()) {
					try {
						processor.process();
					} catch (Exception e) {
						LOG.error("Exception while processing.", e);
					}
				}
			} else {
				LOG.warn("Unable to acquire the file lock, does the cron timer need to be lengthened?");
			}
		} catch (LockException e) {
			LOG.error("Unable to acquire the lock", e);
		} finally {
			applicationLock.unlock();
		}
	}
}
