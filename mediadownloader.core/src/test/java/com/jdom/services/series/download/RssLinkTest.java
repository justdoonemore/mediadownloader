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
 */package com.jdom.services.series.download;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jdom.services.series.download.RssLink;

public class RssLinkTest {

    private static final String TEST_ITEM = "<item>\n"
            + "<title>[21905]-[FULL]-[#a.b.teevee@EFNet]-[ Saved.By.the.Bell.S03E18.DVDRip.XviD-FQM ]-[01/35]"
            + "- &quot;saved.by.the.bell.s03e18.dvdrip.xvid-fqm.nfo&quot; yEnc</title>\n"
            +

            "<link>http://nzbindex.nl/release/18834892/21905-FULL-a.b.teeveeEFNet-Saved.By.the.Bell.S03E18.DVDRip.XviD-FQM-0135-saved.by.the.bell.s03e18.dvdrip.xvid-fqm.nfo.nzb</link>"
            + "<description><![CDATA[<p><font color=\"#8e8e8e\">alt.binaries.multimedia,"
            + "alt.binaries.teevee</font><br />"
            + "<b>211.85 MB</b><br />"
            + "2 hours<br />"
            + "<font color=\"#21A517\">34 files (888 parts)</font>"
            + "<font color=\"#8e8e8e\">by Fake@address.com (Yenc-PP-GB-12b4)</font><br />"
            + "<font color=\"#e2a500\">"
            + "1 NFO | 19 PAR2 | 13 ARCHIVE</font>"
            + "</p>]]></description>"
            + "<category>alt.binaries.multimedia</category>"
            + "<category>alt.binaries.teevee</category>"
            + "<pubDate>Tue, 20 Apr 2010 12:52:40 +0200</pubDate>"
            + "<guid "
            + "isPermaLink=\"true\">http://nzbindex.nl/release/18834892/21905-FULL-a.b.teeveeEFNet-Saved.By.the.Bell.S03E18.DVDRip.XviD-FQM-0135-saved.by.the.bell.s03e18.dvdrip.xvid-fqm.nfo.nzb</guid>"
            + "<enclosure "
            + "url=\"http://nzbindex.nl/download/18834892-1271769487/21905-FULL-a.b.teeveeEFNet-Saved.By.the.Bell.S03E18.DVDRip.XviD-FQM-0135-saved.by.the.bell.s03e18.dvdrip.xvid-fqm.nfo.nzb\""
            + "length=\"222145302\" type=\"text/xml\" />" + "</item>";

    @Test
    public void testItemTitleIsResolvedCorrectly() {
        RssLink rssLink = new RssLink(TEST_ITEM);

        assertEquals("Saved.By.the.Bell.S03E18.DVDRip.XviD-FQM", rssLink.getDisplayName());
    }

    @Test
    public void testItemUrlIsResolvedCorrectly() {
        RssLink rssLink = new RssLink(TEST_ITEM);

        assertEquals(
                "http://nzbindex.nl/download/18834892-1271769487/21905-FULL-a.b.teeveeEFNet-Saved.By.the.Bell.S03E18.DVDRip.XviD-FQM-0135-saved.by.the.bell.s03e18.dvdrip.xvid-fqm.nfo.nzb",
                rssLink.getUrl().toString());
    }
}
