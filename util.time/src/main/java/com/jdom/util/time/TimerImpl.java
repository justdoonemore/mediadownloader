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
 * A default {@link Clock} implementation that will use
 * {@link TimeUtil#currentTimeMillis()} to keep track of time. It is good for
 * use inside both production code and tests.
 * 
 * @author djohnson
 * 
 */
class TimerImpl extends AbstractTimer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getCurrentTime() {
		return TimeUtil.currentTimeMillis();
	}
}
