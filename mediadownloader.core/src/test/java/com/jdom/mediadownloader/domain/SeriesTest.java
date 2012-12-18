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
 */package com.jdom.mediadownloader.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jdom.mediadownloader.domain.Series;

public final class SeriesTest extends AbstractEntityTest<Series> {

	private static final String NAME = "seriesName";

	private static final int SEASON = 3;

	private static final int EPISODE = 17;

	private final Series seriesWithNonDefaultValues = new Series(NAME, SEASON,
			EPISODE);

	/**
	 * Tests the no argument constructor.
	 */
	@Override
	public void testNoArgConstructor() {
		Series series = new Series();
		assertEntityValues(series, null, 0, 0);
	}

	/**
	 * Test the constructor accepting all arguments.
	 */
	@Override
	public void testAllArgConstructor() {
		assertEntityValues(seriesWithNonDefaultValues, NAME, SEASON, EPISODE);
	}

	/**
	 * Test incrementing the season.
	 */
	@Test
	public void testIncrementSeason() {
		seriesWithNonDefaultValues.incrementSeason();
		assertEquals(SEASON + 1, seriesWithNonDefaultValues.getSeason());
	}

	/**
	 * Test incrementing the episode.
	 */
	@Test
	public void testIncrementEpisode() {
		seriesWithNonDefaultValues.incrementEpisode();
		assertEquals(EPISODE + 1, seriesWithNonDefaultValues.getEpisode());
	}

	@Test
	public void testEqualsMethodReturnsFalseForDifferentName() {
		Series anotherSeries = seriesWithNonDefaultValues.clone();
		anotherSeries.setName("anotherSeries");
		assertFalse(seriesWithNonDefaultValues.equals(anotherSeries));
	}

	@Test
	public void testEqualsMethodReturnsFalseForDifferentSeason() {
		Series anotherSeries = seriesWithNonDefaultValues.clone();
		anotherSeries.incrementSeason();
		assertFalse(seriesWithNonDefaultValues.equals(anotherSeries));
	}

	@Test
	public void testEqualsMethodReturnsFalseForDifferentEpisode() {
		Series anotherSeries = seriesWithNonDefaultValues.clone();
		anotherSeries.incrementEpisode();
		assertFalse(seriesWithNonDefaultValues.equals(anotherSeries));
	}

	@Test
	public void testCompareToReturnsPositiveForLaterInAlphabet() {
		Series series = new Series("zzzz", 1, 1);
		assertTrue(series.compareTo(seriesWithNonDefaultValues) > 0);
	}

	@Test
	public void testCompareToReturnsNegativeForEarlierInAlphabet() {
		Series series = new Series("aaaa", 1, 1);
		assertTrue(series.compareTo(seriesWithNonDefaultValues) < 0);
	}

	@Test
	public void assertNormalizedNamesResolveCorrectly() {
		assertTrue(Series.normalizedNameEquals("Law and Order SVU",
				"Law & Order: SVU"));
	}

	@Test
	public void testToDownloadedEpisodeNamingStringReplacesSpaces() {
		Series series = new Series("The Ultimate Fighter", 10, 12);
		assertEquals("The_Ultimate_Fighter_S10E12",
				series.toDownloadedEpisodeNamingString());
	}

	@Test
	public void testToDownloadedEpisodeNamingStringPadsSingleDigits() {
		Series series = new Series("House", 2, 4);
		assertEquals("House_S02E04", series.toDownloadedEpisodeNamingString());
	}

	@Override
	protected Series getNonDefaultValueInstance() {
		return seriesWithNonDefaultValues;
	}

	@Override
	protected void assertSameEntityValues(Series instance, Series cloned) {
		assertEquals(instance.getName(), cloned.getName());
		assertEquals(instance.getSeason(), cloned.getSeason());
		assertEquals(instance.getEpisode(), cloned.getEpisode());
	}

	/**
	 * Asserts correct values on the series object.
	 * 
	 * @param seriesToCheck
	 * @param expectedName
	 * @param expectedSeason
	 * @param expectedEpisode
	 */
	private void assertEntityValues(Series seriesToCheck, String expectedName,
			int expectedSeason, int expectedEpisode) {
		assertEquals(expectedName, seriesToCheck.getName());
		assertEquals(expectedSeason, seriesToCheck.getSeason());
		assertEquals(expectedEpisode, seriesToCheck.getEpisode());
	}
}
