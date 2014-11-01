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

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author djohnson
 * 
 */
public class Duration {
	private static final Pattern DURATION_PATTERN = Pattern
			.compile("(\\d+)\\s(.*)");

	public final TimeUnit unit;
	public final long value;

	public Duration(long value, TimeUnit unit) {
		this.unit = unit;
		this.value = value;
	}

	public Duration toNanos() {
		return convert(TimeUnit.NANOSECONDS);
	}

	public Duration toMicros() {
		return convert(TimeUnit.MICROSECONDS);
	}

	public Duration toMillis() {
		return convert(TimeUnit.MILLISECONDS);
	}

	public Duration toSeconds() {
		return convert(TimeUnit.SECONDS);
	}

	public Duration toMinutes() {
		return convert(TimeUnit.MINUTES);
	}

	public Duration toHours() {
		return convert(TimeUnit.HOURS);
	}

	public Duration toDays() {
		return convert(TimeUnit.DAYS);
	}

	public Duration convert(TimeUnit u) {
		return new Duration(u.convert(value, unit), u);
	}

	public void sleep() throws InterruptedException {
		unit.sleep(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Duration) {
			Duration that = (Duration) obj;

			return toNanos().value == that.toNanos().value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) toNanos().value;
	}

	/**
	 * Determines the <code>Duration</code> value of the system property with
	 * the specified name.
	 * <p>
	 * The first argument is treated as the name of a system property. System
	 * properties are accessible through the
	 * {@link System#getProperty(String)} method. The string
	 * value of this property is then interpreted as a <code>Duration</code>
	 * value and a <code>Duration</code> object representing this value is
	 * returned.
	 * <p>
	 * If there is no property with the specified name, if the specified name is
	 * empty or <code>null</code>, or if the property does not have the correct
	 * format, then <code>null</code> is returned.
	 * <p>
	 * In other words, this method returns a <code>Duration</code> object equal
	 * to the value of: <blockquote><code>
	 * getDuration(propertyKey, null)
	 * </code></blockquote>
	 * 
	 * @param propertyKey
	 *            property name.
	 * @return the <code>Duration</code> value of the property.
	 * @see System#getProperty(String)
	 * @see System#getProperty(String, String)
	 */
	public static Duration getDuration(String propertyKey)
			throws IllegalArgumentException {
		return Duration.parseDuration(System.getProperty(propertyKey));
	}

	/**
	 * Determines the <code>Duration</code> value of the system property with
	 * the specified name.
	 * <p>
	 * The first argument is treated as the name of a system property. System
	 * properties are accessible through the
	 * {@link System#getProperty(String)} method. The string
	 * value of this property is then interpreted as a <code>Duration</code>
	 * value and a <code>Duration</code> object representing this value is
	 * returned. Details of possible numeric formats can be found with the
	 * definition of <code>getProperty</code>.
	 * <p>
	 * The second argument is the default value. A <code>Duration</code> object
	 * that represents the value of the second argument is returned if there is
	 * no property of the specified name, if the property does not have the
	 * correct format, or if the specified name is empty or null.
	 * 
	 * @param propertyKey
	 *            property name.
	 * @param defaultValue
	 *            default value.
	 * @return the <code>Duartion</code> value of the property.
	 * @see System#getProperty(String)
	 * @see System#getProperty(String, String)
	 */
	public static Duration getDuration(String propertyKey, Duration defaultValue) {
		try {
			return getDuration(propertyKey);
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * @param string
	 * @return
	 * @throws IllegalArgumentException
	 *             if the provided string cannot be parsed into a
	 *             {@link com.jdom.util.time.Duration}
	 */
	public static Duration parseDuration(String string)
			throws IllegalArgumentException {
		if (string == null) {
			throw new IllegalArgumentException("Provided string was null!");
		}
		Matcher m = DURATION_PATTERN.matcher(string);
		if (m.matches()) {
			return new Duration(Long.parseLong(m.group(1)), TimeUnit.valueOf(m
					.group(2)));
		}
		throw new IllegalArgumentException(string
				+ " is not parseable into a duration!");
	}
}
