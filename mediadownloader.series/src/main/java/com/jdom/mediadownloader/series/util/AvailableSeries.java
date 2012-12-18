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
 */package com.jdom.mediadownloader.series.util;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AvailableSeries implements Comparable<AvailableSeries> {

	private final String name;

	private final String url;

	/**
	 * Construct the available series.
	 * 
	 * @param name
	 *            the name of the series
	 * @param url
	 *            the series specific url
	 */
	public AvailableSeries(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		boolean retVal = false;

		// Only consider the name important
		if (obj instanceof AvailableSeries) {
			AvailableSeries that = (AvailableSeries) obj;

			retVal = this.getName().equals(that.getName());
		}

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getName()).toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(AvailableSeries that) {
		return this.getName().compareTo(that.getName());
	}
}
