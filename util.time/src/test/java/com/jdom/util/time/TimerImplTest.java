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
 */package com.jdom.util.time;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jdom.util.time.TimerImpl;

/**
 * Test {@link TimerImpl}.
 * 
 * @author djohnson
 * 
 */
public class TimerImplTest {

	@Before
	public void setUp() {
		TimeUtilTest.installMockTimes(new long[] { 100L, 200L });
	}

	@Test(expected = IllegalStateException.class)
	public void testClockImplCantBeStoppedIfHasntBeenStarted() {
		TimerImpl timer = new TimerImpl();
		timer.stop();
	}

	@Test(expected = IllegalStateException.class)
	public void testClockImplCantBeStartedAfterItHasBeenStopped() {
		TimerImpl timer = new TimerImpl();
		timer.start();
		timer.stop();
		timer.start();
	}

	@Test
	public void testClockImplReturnsElapsedTime() throws InterruptedException {
		TimerImpl timer = new TimerImpl();
		timer.start();
		timer.stop();

		assertEquals("Invalid elapsed time returned!", 100L,
				timer.getElapsedTime());
	}

	@Test
	public void testResetWillAllowClockToBeReused() {
		// The first difference will be 75-50 = 25L
		// The second difference will be 250-150 = 100L
		TimeUtilTest.installMockTimes(new long[] { 50L, 75L, 150L, 250L });

		TimerImpl timer = new TimerImpl();
		timer.start();
		timer.stop();
		assertEquals("Incorrect elapsed time returned!", 25L,
				timer.getElapsedTime());

		timer.reset();

		timer.start();
		timer.stop();
		assertEquals("Incorrect elapsed time returned!", 100L,
				timer.getElapsedTime());
	}

	@Test(expected = IllegalStateException.class)
	public void testStartBeingCalledTwiceThrowsException() {
		TimerImpl timer = new TimerImpl();
		timer.start();
		timer.start();
	}

	@Test
	public void testStoppingAClockTwiceDoesNotChangeStopTime() {
		TimeUtilTest.installMockTimes(new long[] { 100L, 200L, 300L });

		TimerImpl timer = new TimerImpl();
		timer.start();
		timer.stop();
		// Elapsed time should still be 100L since the stop time should be stuck
		// at 200L
		timer.stop();

		assertEquals(
				"Expected the stop time to not have changed after the second invocation!",
				100L, timer.getElapsedTime());
	}
}
