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
package com.jdom.mediadownloader.series.download.queue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesBuilder;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.util.time.TimeUtil;
import com.jdom.util.time.TimeUtilTest;

/**
 * Tests the {@link SeriesDownloadDatabaseQueue} class.
 * 
 * @author djohnson
 */
@Ignore
public class SeriesDownloadDatabaseQueueTest {

	private static final SeriesDasFactory NULL_DAS_FACTORY = null;

	private static final SeriesDownloadDatabaseQueue queue = new SeriesDownloadDatabaseQueue(
			NULL_DAS_FACTORY);

	private static final Series inQueue = new SeriesBuilder()
			.withName("in queue").withSeason(2).withEpisode(4).build();

	private static final Series notInQueue = new SeriesBuilder()
			.withName("not in queue").withSeason(3).withEpisode(7).build();

	@Before
	public void setUp() {
		assertTrue(queue.addEntity(inQueue));
	}

	@Test
	public void removedSeriesReturnsTrue() {
		assertTrue(queue.removeEntity(inQueue));
	}

	@Test
	public void addedSeriesAlreadyInQueueReturnsFalse() {
		assertFalse(queue.addEntity(inQueue));
	}

	@Test
	public void removedSeriesNotInQueueReturnsFalse() {
		assertFalse(queue.removeEntity(notInQueue));
	}

	@Test
	public void purgeRemovesOldSeriesQueueEntries() throws InterruptedException {
		TimeUtilTest.freezeTime();

		long timeToLive = 1500;

		TimeUtilTest.freezeTime(TimeUtil.currentTimeMillis() + timeToLive);

		// Add a new series
		queue.addEntity(notInQueue);

		// Purge old entries
		queue.purgeExpiredDownloads(timeToLive);

		// Should not have inQueue
		assertTrue(queue.addEntity(inQueue));
		// Should have notInQueue
		assertFalse(queue.addEntity(notInQueue));
	}
}
