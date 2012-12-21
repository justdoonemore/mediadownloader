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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdom.mediadownloader.ApplicationLock.LockException;
import com.jdom.mediadownloader.api.MediaProcessor;
import com.jdom.mediadownloader.api.MediaProcessorRegistry;
import com.jdom.mediadownloader.domain.AbstractEntity;
import com.jdom.mediadownloader.domain.EntityDownload;
import com.jdom.mediadownloader.download.queue.EntityDownloadQueueManager;
import com.jdom.util.properties.PropertiesUtil;
import com.jdom.util.time.Duration;

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
				for (MediaProcessor<?, ?> processor : mediaProcessorRegistry
						.getRegistered()) {
					try {
						invokeMediaProcessor(processor);
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

	private <T extends AbstractEntity<T>, U extends EntityDownload<U, T>> void invokeMediaProcessor(
			MediaProcessor<T, U> processor) {
		EntityDownloadQueueManager<T, U> queueManager = processor
				.getDownloadQueueManager();
		queueManager.purgeExpiredDownloads(processor
				.getAllowedTimeForDownloadToLive().toMillis().value);

		List<T> entities = processor.getEntities();

		Collection<U> downloads = processor.findDownloads(entities);

		if (!downloads.isEmpty()) {

			// Remove anything currently downloading, and any remaining
			// downloads to the queue
			for (Iterator<U> iter = downloads.iterator(); iter.hasNext();) {

				// Get the entity in question
				U download = iter.next();
				T entity = download.getEntity();

				// First things first, make sure the series is not already in
				// the download queue
				if (queueManager.containsEntity(entity)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String
								.format("%s is already in the download queue, skipping...",
										download));
					}

					iter.remove();
				} else {
					LOG.info(String.format("Added %s to the download queue",
							download));
					queueManager.addEntity(entity);
				}
			}

			Duration sleepTimeBetweenDownloads = processor
					.getSleepTimeBetweenDownloads();

			// Perform the actual downloads
			for (U download : downloads) {
				processor.download(download);

				sleepBetweenDownloads(sleepTimeBetweenDownloads);
			}

			List<T> successfulDownloads = processor
					.processSuccessfulDownloads();

			for (T download : successfulDownloads) {
				// Remove the download from the queue
				if (!(queueManager.removeEntity(download))) {
					LOG.warn(String.format(
							"Unable to remove download %s from the queue",
							download));
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format(
					"Found [%s] downloads for media processor [%s]",
					downloads.size(), processor.getName()));
		}
	}

	private void sleepBetweenDownloads(Duration sleepTimeBetweenDownloads) {
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Sleeping for "
						+ sleepTimeBetweenDownloads.toMillis().value
						+ " ms until next download.");
			}
			sleepTimeBetweenDownloads.sleep();
		} catch (InterruptedException e) {
			LOG.error("Exception while sleeping", e);
		}
	}
}
