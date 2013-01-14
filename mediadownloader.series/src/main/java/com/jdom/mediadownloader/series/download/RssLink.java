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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.jdom.logging.api.LogFactory;
import com.jdom.logging.api.Logger;
import com.jdom.util.regex.RegexMatch;
import com.jdom.util.regex.RegexUtil;

public class RssLink {

	private static final Logger LOG = LogFactory.getLogger(RssLink.class);

	private static final String TITLE_REGEX = "<title>\\[.*?\\]-\\[FULL\\]-\\[#a.b.teevee.*\\]-\\[\\s*(.*?\\.(S|s)\\d+(E|e)\\d+\\..*?)\\s*\\].*?</title>";

	private static final String URL_REGEX = "url=\"(.*?)\"";

	private final String originalText;

	private String displayName;

	private URL url;

	public RssLink(String link) {
		this.originalText = link;

		Collection<RegexMatch> titleMatch = RegexUtil.findRegexMatches(
				originalText, TITLE_REGEX);

		if (!titleMatch.isEmpty()) {
			displayName = titleMatch.iterator().next().getGroup(1);
		}

		Collection<RegexMatch> urlMatch = RegexUtil.findRegexMatches(
				originalText, URL_REGEX);

		if (!urlMatch.isEmpty()) {
			try {
				url = new URL(urlMatch.iterator().next().getGroup(1));
			} catch (MalformedURLException e) {
				LOG.error("MalformedUrlException trying to use url: " + url);
			}
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public URL getUrl() {
		return url;
	}

	/**
	 * Package level setter.
	 * 
	 * @param displayName
	 *            the display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
