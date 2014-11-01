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

import java.util.Date;

public class ImmutableDate extends Date {

	private static final long serialVersionUID = 1L;

	public ImmutableDate() {
		super();
	}

	public ImmutableDate(long date) {
		super(date);
	}

	@Override
	public void setYear(int year) {
		throwException();
	}

	@Override
	public void setMonth(int month) {
		throwException();
	}

	@Override
	public void setDate(int date) {
		throwException();
	}

	@Override
	public void setHours(int hours) {
		throwException();
	}

	@Override
	public void setMinutes(int minutes) {
		throwException();
	}

	@Override
	public void setSeconds(int seconds) {
		throwException();
	}

	@Override
	public void setTime(long time) {
		throwException();
	}

	private void throwException() {
		throw new UnsupportedOperationException(
				"This date it an immutable object!");
	}
}
