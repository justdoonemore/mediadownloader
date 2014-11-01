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
package com.jdom.mediadownloader.services;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class HtmlUtil {
	private static final Logger LOG = LoggerFactory.getLogger(HtmlUtil.class);

	/**
	 * Retrieve the contents of the URL.
	 * 
	 * @param url
	 *            the url
	 * @return the contents as a string
	 */
	public static String downloadUrlContents(String url) {
		URL u;
		try {
			u = new URL(url.replaceAll(" ", ""));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		return downloadUrlContents(u);
	}

	/**
	 * Retrieve the contents of the URL.
	 * 
	 * @param url
	 *            the url
	 * @return the contents as a string
	 */
	public static String downloadUrlContents(URL url) {

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Downloading URL [%s]", url.toString()));
		}

		try {
			return Resources.toString(url, Charset.defaultCharset());
		} catch (IOException e) {
			LOG.warn("Unable to retrieve URL contents for URL [" + url + "], returning empty string!", e);
			return "";
		}
	}

}
