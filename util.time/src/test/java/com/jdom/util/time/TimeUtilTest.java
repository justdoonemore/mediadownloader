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
package com.jdom.util.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 * Test {@link TimeUtil}.
 * 
 * @author djohnson
 * 
 */
public class TimeUtilTest {

	/**
	 * Allows time to be manipulated so that tests can be run assuming specific
	 * time frames.
	 * 
	 * @author djohnson
	 * 
	 */
	private static class MockTimeStrategy implements TimeStrategy {

		private final long[] times;
		private int index;

		public MockTimeStrategy(long[] times) {
			if (times == null || times.length == 0) {
				throw new IllegalArgumentException(
						"Must specify at least one time!");
			}
			this.times = times;
		}

		@Override
		public long currentTimeMillis() {
			long value = times[index];
			if (index < (times.length - 1)) {
				index++;
			}
			return value;
		}
	}

	/**
	 * Mocks the times returned from {@link TimeUtil#currentTimeMillis()} such
	 * that each successive call will return the next element in the array.
	 * 
	 * @param times
	 *            the times to return
	 */
	public static void installMockTimes(long[] times) {
		TimeUtil.timeStrategy = new MockTimeStrategy(times);
	}

	/**
	 * Freezes time so that all successive calls to
	 * {@link TimeUtil#currentTimeMillis()} will return the current time.
	 */
	public static void freezeTime() {
		freezeTime(System.currentTimeMillis());
	}

	/**
	 * Freezes time so that all successive calls to
	 * {@link TimeUtil#currentTimeMillis()} will return the specified time.
	 */
	public static void freezeTime(long time) {
		installMockTimes(new long[] { time });
	}

	/**
	 * Resumes using the system time, this can be called after time has been
	 * frozen to resume noticing time differences when
	 * {@link TimeUtil#currentTimeMillis()} is called.
	 */
	public static void resumeTime() {
		TimeUtil.timeStrategy = TimeUtil.SYSTEM_TIME_STRATEGY;
	}

	@Test
	public void testFreezeTimeStopsTime() throws InterruptedException {
		TimeUtilTest.freezeTime();

		long firstTime = TimeUtil.currentTimeMillis();
		Thread.sleep(10L);
		long secondTime = TimeUtil.currentTimeMillis();

		assertEquals("Time should have been frozen!", firstTime, secondTime);
	}

	@Test
	public void testResumeTimeWillResumeTime() throws InterruptedException {
		TimeUtilTest.freezeTime();

		long firstTime = TimeUtil.currentTimeMillis();
		TimeUtilTest.resumeTime();
		Thread.sleep(10L);
		long secondTime = TimeUtil.currentTimeMillis();

		assertFalse("Time should have resumed!", firstTime == secondTime);
	}

}
