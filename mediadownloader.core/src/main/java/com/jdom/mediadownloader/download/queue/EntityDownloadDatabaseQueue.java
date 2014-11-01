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
import com.jdom.persist.persistence.AbstractDASService;
import com.jdom.util.time.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author djohnson
 * 
 */
public abstract class EntityDownloadDatabaseQueue<T extends AbstractEntity<T>, U extends EntityDownload<U, T>>
		implements EntityDownloadQueueManager<T, U> {

	private static final Logger LOG = LoggerFactory
			.getLogger(EntityDownloadDatabaseQueue.class);

	private final AbstractDASService<T> entityDas;

	private final AbstractDASService<U> entityDownloadDas;

	public EntityDownloadDatabaseQueue(AbstractDASService<T> entityDas,
			AbstractDASService<U> entityDownloadDas) {
		this.entityDas = entityDas;
		this.entityDownloadDas = entityDownloadDas;
	}

	@Override
	public boolean addEntity(T series) {
		U download = getDownload(series);
		download.setTime(TimeUtil.newImmutableDate());

		T actualEntity = entityDas.getById(series.getId());
		if (actualEntity != null) {
			download.setEntity(actualEntity);

			return entityDownloadDas.addObject(download);
		} else {
			return false;
		}
	}

	@Override
	public boolean containsEntity(T series) {
		U download = getDownload(series);

		U actualDownload = checkForAlreadyDownloading(download);

		return actualDownload != null;
	}

	@Override
	public boolean removeEntity(T series) {
		U download = getDownload(series);

		U actualDownload = checkForAlreadyDownloading(download);
		if (actualDownload != null) {
			return entityDownloadDas.deleteObject(actualDownload);
		} else {
			LOG.warn("Unable to find entity download " + download);
		}

		return false;
	}

	@Override
	public void purgeExpiredDownloads(long olderThanMillis) {
		long currentTime = TimeUtil.currentTimeMillis();

		List<U> all = entityDownloadDas.getAll();

		for (U entity : all) {
			long timePutInQueue = entity.getTime().getTime();

			long timeSinceAdded = currentTime - timePutInQueue;
			if (timeSinceAdded > olderThanMillis) {
				LOG.info("Purging " + entity + ", ["
						+ (timeSinceAdded - olderThanMillis)
						+ "] ms past purge time");
				if (!entityDownloadDas.deleteObject(entity)) {
					LOG.error("Unable to delete series download " + entity);
				}
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Not purging " + entity + " still has ["
							+ (olderThanMillis - timeSinceAdded) + "] ms");
				}
			}
		}
	}

	protected abstract U getDownload(T entity);

	protected abstract U checkForAlreadyDownloading(U download);
}
