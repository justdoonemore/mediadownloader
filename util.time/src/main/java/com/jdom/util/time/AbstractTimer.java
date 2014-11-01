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

/**
 * Provides the basic {@link Clock} implementation, such as the state machine
 * functionality.
 * 
 * @author djohnson
 * 
 */
abstract class AbstractTimer implements Timer {
	private long start;

	private long stop;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		if (stop != 0) {
			throw new IllegalStateException(
					"A stopped clock must be reset before start() is called again!");
		} else if (start != 0) {
			throw new IllegalStateException(
					"A clock that is running must be reset before start() is called again!");
		}
		this.start = getCurrentTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		if (start == 0) {
			throw new IllegalStateException(
					"Clock must be started before it can be stopped!");
		}
		// If the clock has already been stopped, don't change the time
		if (stop == 0) {
			this.stop = getCurrentTime();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getElapsedTime() {
		return stop - start;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		start = 0;
		stop = 0;
	}

	protected abstract long getCurrentTime();
}
