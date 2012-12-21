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
package com.jdom.mediadownloader.api;

import java.util.Collection;
import java.util.List;

import com.jdom.mediadownloader.domain.AbstractEntity;
import com.jdom.mediadownloader.domain.EntityDownload;
import com.jdom.mediadownloader.download.queue.EntityDownloadQueueManager;
import com.jdom.util.time.Duration;

/**
 * @author djohnson
 * 
 */
public interface MediaProcessor<T extends AbstractEntity<T>, U extends EntityDownload<U, T>> {

	/**
	 * Return the name of the media processor.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Get the list of entities this processor monitors.
	 * 
	 * @return the list of entities
	 */
	List<T> getEntities();

	/**
	 * Find any new downloads for the entities.
	 * 
	 * @param entities
	 *            the entities
	 * @return the downloads to be processed
	 */
	Collection<U> findDownloads(List<T> entities);

	/**
	 * Process the download.
	 * 
	 * @param download
	 *            the download
	 */
	void download(U download);

	/**
	 * Process successful downloads.
	 * 
	 * @return the list of successfully downloaded entities
	 */
	List<T> processSuccessfulDownloads();

	/**
	 * Get the entity download queue manager.
	 * 
	 * @return the queue manager
	 */
	EntityDownloadQueueManager<T, U> getDownloadQueueManager();

	/**
	 * Get the sleep time that should be spent between downloads.
	 * 
	 * @return the sleep time between downloads
	 */
	Duration getSleepTimeBetweenDownloads();

	/**
	 * Get the allowed time for a download to live before it should be
	 * considered failed.
	 * 
	 * @return the allowed time for a download to live
	 */
	Duration getAllowedTimeForDownloadToLive();
}