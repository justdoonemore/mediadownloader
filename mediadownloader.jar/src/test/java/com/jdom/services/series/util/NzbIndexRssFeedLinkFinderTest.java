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

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jdom.junit.utils.FileContentsDownload;
import com.jdom.services.series.util.NzbIndexRssFeedLinkFinder;
import com.jdom.tvshowdownloader.domain.Series;
import com.jdom.util.html.exception.MalformedURLException;

public class NzbIndexRssFeedLinkFinderTest extends
		AbstractSeriesLinkFinderTest<MockNzbIndexRssFeedLinkFinder> {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		linkFinder.setSeriesSearchPage("/html/nzbindex_rss.htm");
	}

	@BeforeClass
	public static void staticSetup() {
		System.setProperty("initial.age", Integer.toString(700));
		System.setProperty("default.age", Integer.toString(30));
	}

	@Test
	public void testRetrievingLinks() throws MalformedURLException {
		Series simpsons = new Series("The Simpsons", 1, 1);

		Collection<String> links = linkFinder
				.getPossibleLinksForSeries(simpsons);

		assertEquals(250, links.size());
	}

	@Test
	public void testGetSeriesSearchPageUsesInitialAgeForNewSeries()
			throws MalformedURLException {
		Series simpsons = new Series("The Simpsons", 1, 1);

		String seriesSearchUrl = new NzbIndexRssFeedLinkFinder(
				new FileContentsDownload()) {
			@Override
			String getSeriesDownloadUrl() {
				return "http://@SHOW@/@AGE@";
			}
		}.getSeriesSearchPage(simpsons);

		assertEquals("http://The.Simpsons/700", seriesSearchUrl);
	}

	@Test
	public void testGetSeriesSearchPageUsesDefaultAgeForNotNewSeries()
			throws MalformedURLException {
		Series simpsons = new Series("The Simpsons", 1, 2);

		String seriesSearchUrl = new NzbIndexRssFeedLinkFinder(
				new FileContentsDownload()) {
			@Override
			String getSeriesDownloadUrl() {
				return "http://@SHOW@/@AGE@";
			}
		}.getSeriesSearchPage(simpsons);

		assertEquals("http://The.Simpsons/30", seriesSearchUrl);
	}

	@Override
	protected MockNzbIndexRssFeedLinkFinder getLinkFinder() {
		return new MockNzbIndexRssFeedLinkFinder(new FileContentsDownload());
	}
}
