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
 */package com.jdom.services.series.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jdom.services.series.util.SeriesUtil;
import com.jdom.tvshowdownloader.domain.Series;

public class SeriesUtilTest {

	public static final int SHOW_URL_REGEX_GROUP = 2;

	public static final int SHOW_NAME_REGEX_GROUP = 6;

	public static final String AVAILABLE_SERIES_REGEX = "<a href=(\"|')(.*)?(\"|').*?class=(\"|')result_blue_bold(\"|')>(.*)?</a>";

	private static final String SAVED_BY_THE_BELL = "Saved.By.the.Bell.S03E18.DVDRip.XviD-FQM";

	private static final String TWO_AND_A_HALF_MEN = "Two and a Half Men";

	private static final String TWO_AND_A_HALF_MEN_WITH_UNDERSCORES = TWO_AND_A_HALF_MEN
			.replaceAll(" ", "_");

	private static final String TWO_AND_A_HALF_MEN_WITH_PERIODS = TWO_AND_A_HALF_MEN
			.replaceAll(" ", ".");

	private static final String SO_YOU = "So.You.Think.You.Can.Dance.S06E17.Top.12.Perform.WS.PDTV.XviD-FQM";

	private static final String HOUSE = "House";

	private static final int SEASON = 13;

	private static final int EPISODE = 27;

	private static final int EXPECTED_SHOWS = 622;

	private static final String TV_SHOW_LIST_FILE = "tv_show_list.html";

	private static final String SHOW_LIST_FILE_PATH = "/html/"
			+ TV_SHOW_LIST_FILE;

	@Test
	public void testSingleWordTitle() {
		String show = getTitleWithCapitals(HOUSE);
		Series series = new Series(HOUSE, SEASON, EPISODE);

		testSeriesParse(show, series);
	}

	@Test
	public void testUnderscoreSeparatorUppercase() {
		String show = getTitleWithCapitals(TWO_AND_A_HALF_MEN_WITH_UNDERSCORES);
		Series series = new Series(TWO_AND_A_HALF_MEN, SEASON, EPISODE);

		testSeriesParse(show, series);
	}

	@Test
	public void testUnderscoreSeparatorLowercase() {
		String show = getTitleWithLowercase(TWO_AND_A_HALF_MEN_WITH_UNDERSCORES);
		Series series = new Series(TWO_AND_A_HALF_MEN, SEASON, EPISODE);

		testSeriesParse(show, series);
	}

	@Test
	public void testPeriodSeparatorUppercase() {
		String show = getTitleWithCapitals(TWO_AND_A_HALF_MEN_WITH_PERIODS);
		Series series = new Series(TWO_AND_A_HALF_MEN, SEASON, EPISODE);

		testSeriesParse(show, series);
	}

	@Test
	public void testPeriodSeparatorLowercase() {
		String show = getTitleWithLowercase(TWO_AND_A_HALF_MEN_WITH_PERIODS);
		Series series = new Series(TWO_AND_A_HALF_MEN, SEASON, EPISODE);

		testSeriesParse(show, series);
	}

	@Test
	public void testSoYouThinkYouCanDance() {
		Series series = new Series("So You Think You Can Dance", 6, 17);

		testSeriesParse(SO_YOU, series);
	}

	@Test
	public void testRSSFeedFormat() {
		Series series = new Series("Saved By The Bell", 3, 18);

		testSeriesParse(SAVED_BY_THE_BELL, series);
	}

	// TODO: Fix or delete test

	// @Test
	// public void retrieveAvailableSeriesListRetrievesCorrectNumber() {
	// Collection<AvailableSeries> shows =
	// SeriesUtil.retrieveAvailableSeriesList(this.getClass().getResource(
	// SHOW_LIST_FILE_PATH).toString(), AVAILABLE_SERIES_REGEX,
	// SHOW_NAME_REGEX_GROUP,
	// SHOW_URL_REGEX_GROUP);
	//
	// assertEquals("Incorrect number of shows found!", EXPECTED_SHOWS,
	// shows.size());
	// }

	private void testSeriesParse(String series, Series expectedObject) {
		Series result = SeriesUtil.parseSeries(series);
		assertEquals(expectedObject, result);
	}

	private String getTitleWithCapitals(String show) {
		return getTitle(show, 'S', 'E');
	}

	private String getTitleWithLowercase(String show) {
		return getTitle(show, 's', 'e');
	}

	private String getTitle(String show, char seasonChar, char episodeChar) {
		return show + seasonChar + SEASON + episodeChar + EPISODE + ".XVID.nzb";
	}

}
