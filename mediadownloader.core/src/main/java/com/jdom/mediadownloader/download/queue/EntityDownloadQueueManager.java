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
package com.jdom.mediadownloader.download.queue;

import com.jdom.mediadownloader.domain.AbstractEntity;
import com.jdom.mediadownloader.domain.EntityDownload;

/**
 * @author djohnson
 * 
 */
public interface EntityDownloadQueueManager<T extends AbstractEntity<T>, U extends EntityDownload<U, T>> {
	/**
	 * Adds an entity to the queue.
	 * 
	 * @param entity
	 *            the entity
	 * @return true if the entity download was added
	 */
	boolean addEntity(T entity);

	/**
	 * Removes the entity from the queue.
	 * 
	 * @return true if the entity was removed
	 */
	boolean removeEntity(T entity);

	/**
	 * Determine if the entity is in the download queue.
	 * 
	 * @param entity
	 *            the entity
	 * @return true if the entity is in the download queue
	 */
	boolean containsEntity(T entity);

	/**
	 * Removes all entity that have passed the allowed purge interval.
	 * 
	 * @param olderThanMillis
	 *            the milliseconds of life allowed for an entity download
	 */
	void purgeExpiredDownloads(long olderThanMillis);
}
