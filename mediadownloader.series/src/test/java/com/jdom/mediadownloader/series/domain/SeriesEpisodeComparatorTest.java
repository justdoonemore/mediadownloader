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
package com.jdom.mediadownloader.series.domain;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

public class SeriesEpisodeComparatorTest {
	private static final String NAME = "ASeriesName";

	private static final int SEASON = 1;

	private static final int EPISODE = 1;

	private static final Comparator<Series> SERIES_EPISODE_COMPARATOR = new SeriesEpisodeComparator();

	private final Series TEST_SERIES = new Series(NAME, SEASON, EPISODE);

	private final Series RETURN_EQUAL = new Series(NAME, SEASON, EPISODE);

	private final Series GREATER_THAN_BECAUSE_OF_SEASON = new Series(NAME, 0, 2);

	private final Series LESS_THAN_BECAUSE_OF_SEASON = new Series(NAME, 2, 1);

	private final Series GREATER_THAN_BECAUSE_OF_EPISODE = new Series(NAME, 1,
			0);

	private final Series LESS_THAN_BECAUSE_OF_EPISODE = new Series(NAME, 1, 2);

	@Test
	public void equalSeasonAndEpisodeReturnsEqual() {
		assertEquals(0,
				SERIES_EPISODE_COMPARATOR.compare(TEST_SERIES, RETURN_EQUAL));
	}

	@Test
	public void greaterSeasonReturnsGreater() {
		assertEquals(1, SERIES_EPISODE_COMPARATOR.compare(TEST_SERIES,
				GREATER_THAN_BECAUSE_OF_SEASON));
	}

	@Test
	public void greaterEpisodeReturnsGreater() {
		assertEquals(1, SERIES_EPISODE_COMPARATOR.compare(TEST_SERIES,
				GREATER_THAN_BECAUSE_OF_EPISODE));
	}

	@Test
	public void lesserSeasonReturnsLesser() {
		assertEquals(-1, SERIES_EPISODE_COMPARATOR.compare(TEST_SERIES,
				LESS_THAN_BECAUSE_OF_SEASON));
	}

	@Test
	public void lesserEpisodeReturnsLesser() {
		assertEquals(-1, SERIES_EPISODE_COMPARATOR.compare(TEST_SERIES,
				LESS_THAN_BECAUSE_OF_EPISODE));
	}

}
