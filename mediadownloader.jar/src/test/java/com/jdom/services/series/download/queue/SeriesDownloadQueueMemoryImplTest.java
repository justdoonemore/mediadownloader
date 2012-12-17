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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jdom.tvshowdownloader.domain.Series;
import com.jdom.util.time.TimeUtil;
import com.jdom.util.time.TimeUtilTest;

/**
 * Tests the SeriesDownloadDatabaseQueue class.
 * 
 * @author djohnson
 */
@Ignore
public class SeriesDownloadQueueMemoryImplTest {

	private static final SeriesDownloadDatabaseQueue seriesDownloadQueueMemoryImpl = new SeriesDownloadDatabaseQueue();

	private static final Series inQueue = new Series("test", 2, 4);

	private static final Series notInQueue = new Series("NotInQueue", 3, 7);

	@Before
	public void setUp() {
		assertTrue(seriesDownloadQueueMemoryImpl.addSeries(inQueue));
	}

	@Test
	public void removedSeriesReturnsTrue() {
		assertTrue(seriesDownloadQueueMemoryImpl.removeSeries(inQueue));
	}

	@Test
	public void addedSeriesAlreadyInQueueReturnsFalse() {
		assertFalse(seriesDownloadQueueMemoryImpl.addSeries(inQueue));
	}

	@Test
	public void removedSeriesNotInQueueReturnsFalse() {
		assertFalse(seriesDownloadQueueMemoryImpl.removeSeries(notInQueue));
	}

	@Test
	public void purgeRemovesOldSeriesQueueEntries() throws InterruptedException {
		TimeUtilTest.freezeTime();

		long timeToLive = 1500;

		TimeUtilTest.freezeTime(TimeUtil.currentTimeMillis() + timeToLive);

		// Add a new series
		seriesDownloadQueueMemoryImpl.addSeries(notInQueue);

		// Purge old entries
		seriesDownloadQueueMemoryImpl.purgeExpiredSeries(timeToLive);

		// Should not have inQueue
		assertTrue(seriesDownloadQueueMemoryImpl.addSeries(inQueue));
		// Should have notInQueue
		assertFalse(seriesDownloadQueueMemoryImpl.addSeries(notInQueue));
	}
}