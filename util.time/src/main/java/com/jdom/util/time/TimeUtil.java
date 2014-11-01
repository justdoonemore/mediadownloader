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

import org.slf4j.Logger;

import java.util.Date;

public final class TimeUtil {

	/**
	 * A timer that does not really return the current time. Useful when you
	 * only want to keep track of times in a conditional sense, such as if a
	 * logging priority is enabled.
	 * 
	 * @author djohnson
	 * 
	 */
	private static class NullTimer extends AbstractTimer {
		@Override
		protected long getCurrentTime() {
			return 1;
		}
	}

	/**
	 * Delegates the retrieval of the current time to the system clock.
	 * 
	 * @author djohnson
	 * 
	 */
	private static class SystemTimeStrategy implements TimeStrategy {
		@Override
		public long currentTimeMillis() {
			return System.currentTimeMillis();
		}
	}

	static final TimeStrategy SYSTEM_TIME_STRATEGY = new SystemTimeStrategy();

	static final Timer NULL_TIMER = new NullTimer();

	/**
	 * The strategy to retrieve the "current time" value from.
	 */
	static TimeStrategy timeStrategy = SYSTEM_TIME_STRATEGY;

	/**
	 * Retrieve a {@link Timer} that allows the demarcation of arbitrary start
	 * and stop times.
	 * 
	 * @return a {@link Timer}
	 */
	public static Timer getTimer() {
		return new TimerImpl();
	}

	/**
	 * Retrieve the current time in milliseconds. This method should be used
	 * instead of {@link System#currentTimeMillis()}.
	 * 
	 * @return the current time in milliseconds
	 */
	public static long currentTimeMillis() {
		return timeStrategy.currentTimeMillis();
	}

	/**
	 * Return a new {@link java.util.Date} instance.
	 * 
	 * @return the current {@link java.util.Date}
	 */
	public static Date newDate() {
		return new Date(currentTimeMillis());
	}

	/**
	 * Return a new ImmutableDate.
	 * 
	 * @return an immutable date for the current time
	 */
	public static Date newImmutableDate() {
		return new ImmutableDate(currentTimeMillis());
	}
}
