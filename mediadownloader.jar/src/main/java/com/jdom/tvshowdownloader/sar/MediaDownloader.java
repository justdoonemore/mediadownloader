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
 */package com.jdom.tvshowdownloader.sar;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdom.util.properties.PropertiesUtil;
import com.jdom.services.series.download.SeriesDownloadProcessor;
import com.jdom.services.series.download.SeriesDownloadUpdater;

public class MediaDownloader {

	private static ApplicationContext ctx;

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
			ctx = new ClassPathXmlApplicationContext(
					new String[] { "/applicationContext.xml" });
		}
	}

	private final SeriesDownloadProcessor downloadProcessor;

	private MediaDownloader(SeriesDownloadProcessor downloadProcessor) {
		this.downloadProcessor = downloadProcessor;
	}

	private void processDownloads() {
		this.downloadProcessor.process();
		SeriesDownloadUpdater.update();
	}
}
