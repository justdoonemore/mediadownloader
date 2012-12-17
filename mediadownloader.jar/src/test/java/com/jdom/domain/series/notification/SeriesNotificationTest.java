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
 */package com.jdom.domain.series.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.jdom.domain.common.AbstractEntityTest;
import com.jdom.tvshowdownloader.domain.Series;
import com.jdom.tvshowdownloader.domain.SeriesNotification;
import com.jdom.tvshowdownloader.domain.User;

public final class SeriesNotificationTest extends
		AbstractEntityTest<SeriesNotification> {

	private static final User USER = new User("SomeGuy", "password", "Admin",
			"random@random.com");

	private static final Series SERIES = new Series("series", 1, 1);

	private final SeriesNotification SERIES_NOTIFICATION = new SeriesNotification(
			USER, SERIES);

	@Override
	public void testNoArgConstructor() {
		SeriesNotification seriesNotification = new SeriesNotification();
		assertEntityValues(seriesNotification, null, null);
	}

	@Override
	public void testAllArgConstructor() {
		assertEntityValues(SERIES_NOTIFICATION, USER, SERIES);
	}

	@Test
	public void testEqualsReturnsFalseForDifferentUser() {
		SeriesNotification cloned = SERIES_NOTIFICATION.clone();
		cloned.setUser(new User());
		assertFalse(SERIES_NOTIFICATION.equals(cloned));
	}

	@Test
	public void testEqualsReturnsFalseForDifferentSeries() {
		SeriesNotification cloned = SERIES_NOTIFICATION.clone();
		cloned.setSeries(new Series());
		assertFalse(SERIES_NOTIFICATION.equals(cloned));
	}

	@Test
	public void testCompareToReturnsSeriesResultsWhenSeriesAreDifferent() {
		Series anotherSeries = new Series("anotherSeries", 3, 3);
		SeriesNotification cloned = SERIES_NOTIFICATION.clone();
		cloned.setSeries(anotherSeries);

		assertEquals(SERIES_NOTIFICATION.compareTo(cloned), SERIES_NOTIFICATION
				.getSeries().compareTo(cloned.getSeries()));

		assertEquals(cloned.compareTo(SERIES_NOTIFICATION), cloned.getSeries()
				.compareTo(SERIES_NOTIFICATION.getSeries()));
	}

	@Test
	public void testCompareToReturnsUserResultsWhenSeriesAreSame() {
		User anotherUser = new User("anotherUser", "password", "Admin",
				"blah@blah.com");
		SeriesNotification cloned = SERIES_NOTIFICATION.clone();
		cloned.setUser(anotherUser);

		assertEquals(SERIES_NOTIFICATION.compareTo(cloned), SERIES_NOTIFICATION
				.getUser().compareTo(cloned.getUser()));

		assertEquals(cloned.compareTo(SERIES_NOTIFICATION), cloned.getUser()
				.compareTo(SERIES_NOTIFICATION.getUser()));
	}

	@Override
	protected SeriesNotification getNonDefaultValueInstance() {
		return SERIES_NOTIFICATION;
	}

	@Override
	protected void assertSameEntityValues(SeriesNotification instance,
			SeriesNotification cloned) {
		assertEquals(instance.getUser(), cloned.getUser());
		assertEquals(instance.getSeries(), cloned.getSeries());
	}

	/**
	 * Asserts correct values on the entity object.
	 * 
	 * @param entityToCheck
	 * @param expectedUser
	 * @param expectedSeries
	 */
	private void assertEntityValues(SeriesNotification entityToCheck,
			User expectedUser, Series expectedSeries) {
		assertEquals(expectedUser, entityToCheck.getUser());
		assertEquals(expectedSeries, entityToCheck.getSeries());
	}
}
