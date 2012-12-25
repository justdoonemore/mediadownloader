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
package com.jdom.mediadownloader.series.download;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesBuilder;

public class RssSeriesDownloadLinkTest {

	private static final String SERIES_DISPLAY_NAME = "Heroes.S02E05.PDTV.XviD-FiHTV";

	private final RssLink testLink = new RssLink(
			"<item><title>[22518]-[FULL]-[#a.b.teevee@EFNet]-[ "
					+ SERIES_DISPLAY_NAME
					+ " ]-[04/26] - &quot;hitched.nz.s02e03.pdtv.xvid-fihtv.nfo&quot; yEnc</title>"
					+ "<link>http://nzbindex.nl/release/18835347/22518-FULL-a.b.teeveeEFNet-Hitched.NZ.S02E03.PDTV.XviD-FiHTV-0426-hitched.nz.s02e03.pdtv.xvid-fihtv.nfo.nzb</link>"
					+ "<description><![CDATA[<p><font color=\"#8e8e8e\">alt.binaries.multimedia, alt.binaries.teevee</font><br />"
					+ "<b>207.59 MB</b><br />"
					+ "1 hour<br />"
					+ "<font color=\"#21A517\">23 files (573 parts)</font>"
					+ "<font color=\"#8e8e8e\">by teevee@4u.net (teevee)</font><br />"
					+ "<font color=\"#e2a500\">"
					+ "1 NFO | 8 PAR2 | 13 ARCHIVE</font>"
					+ "</p>]]></description>"
					+ "<category>alt.binaries.multimedia</category>"
					+ "<category>alt.binaries.teevee</category>"
					+ "<pubDate>Tue, 20 Apr 2010 13:34:51 +0200</pubDate>"
					+ "<guid isPermaLink=\"true\">http://nzbindex.nl/release/18835347/22518-FULL-a.b.teeveeEFNet-Hitched.NZ.S02E03.PDTV.XviD-FiHTV-0426-hitched.nz.s02e03.pdtv.xvid-fihtv.nfo.nzb</guid>"
					+ "<enclosure url=\"http://nzbindex.nl/download/18835347-1271769487/22518-FULL-a.b.teeveeEFNet-Hitched.NZ.S02E03.PDTV.XviD-FiHTV-0426-hitched.nz.s02e03.pdtv.xvid-fihtv.nfo.nzb\" length=\"217669327\" type=\"text/xml\" />"
					+ "</item>");

	private static final String[] TITLE_EXCLUSIONS = { "720p", "BluRay" };

	private final Series targetSeries = new SeriesBuilder().withName("Heroes")
			.withSeason(2).withEpisode(5).build();

	private final RssSeriesDownloadLink rssSeriesDownloadLink = new RssSeriesDownloadLink(
			targetSeries, testLink, TITLE_EXCLUSIONS);

	@Before
	public void setUp() {

	}

	@Test
	public void findsSeriesWithSameEpisodeInSameSeason() throws Exception {
		assertTrue(rssSeriesDownloadLink.matchesSeriesDownloadCriteria());
	}

	@Test
	public void findsSeriesWithNewerEpisodeInSameSeason() throws Exception {
		testLink.setDisplayName(getDisplayNameForSeasonEpisode("2", "7"));

		assertTrue(rssSeriesDownloadLink.matchesSeriesDownloadCriteria());
	}

	@Test
	public void findsSeriesWithNewerSeasonAndSameEpisode() throws Exception {
		testLink.setDisplayName(getDisplayNameForSeasonEpisode("3", "5"));

		assertTrue(rssSeriesDownloadLink.matchesSeriesDownloadCriteria());
	}

	@Test
	public void skipsSeriesWithOlderSeasonAndNewerEpisode() throws Exception {
		testLink.setDisplayName(getDisplayNameForSeasonEpisode("1", "5"));

		assertFalse(rssSeriesDownloadLink.matchesSeriesDownloadCriteria());
	}

	@Test
	public void skipsSeriesWithExcludedTitle() throws Exception {
		testLink.setDisplayName(SERIES_DISPLAY_NAME + "." + TITLE_EXCLUSIONS[0]);

		assertFalse(rssSeriesDownloadLink.matchesSeriesDownloadCriteria());
	}

	@Test
	public void skipsSeriesWithOlderSeasonAndOlderEpisode() throws Exception {
		testLink.setDisplayName(getDisplayNameForSeasonEpisode("1", "2"));

		assertFalse(rssSeriesDownloadLink.matchesSeriesDownloadCriteria());
	}

	@Test
	public void skipsSeriesWithNoDisplayName() {
		testLink.setDisplayName(SERIES_DISPLAY_NAME.replaceAll(
				SERIES_DISPLAY_NAME, ""));

		assertFalse(rssSeriesDownloadLink.matchesSeriesDownloadCriteria());
	}

	private String getDisplayNameForSeasonEpisode(String season, String episode) {

		return SERIES_DISPLAY_NAME.replaceAll("S02E05", "S0" + season + "E0"
				+ episode);
	}

}
