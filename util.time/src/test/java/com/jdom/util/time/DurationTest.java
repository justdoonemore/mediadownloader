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

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author djohnson
 * 
 */
public class DurationTest {

	@Test
	public void testGetDurationReturnsValueWhenParseable()
			throws ParseException {
		System.getProperties().setProperty("duration.property", "3 SECONDS");
		Duration parsed = Duration.getDuration("duration.property");
		assertEquals("Incorrectly parsed duration!", new Duration(3,
				TimeUnit.SECONDS), parsed);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetDurationThrowsExceptionWhenNotParseable()
			throws ParseException {
		System.getProperties().setProperty("duration.property", "bad format");
		Duration.getDuration("duration.property");
	}

	@Test
	public void testGetDurationReturnsDefaultWhenNotParseable() {
		System.getProperties().setProperty("duration.property", "bad format");
		Duration parsed = Duration.getDuration("duration.property",
				new Duration(5, TimeUnit.SECONDS));
		assertEquals("Expected the default provided duration!", new Duration(5,
				TimeUnit.SECONDS), parsed);
	}

	@Test
	public void testGetDurationReturnsDefaultWhenNull() {
		System.clearProperty("duration.property");
		Duration parsed = Duration.getDuration("duration.property",
				new Duration(5, TimeUnit.SECONDS));
		assertEquals("Expected the default provided duration!", new Duration(5,
				TimeUnit.SECONDS), parsed);
	}
}
