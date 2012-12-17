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
 */package com.jdom.tvshowdownloader.domain;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class BaseSeries<T> extends AbstractEntity<T> {

	private static final Pattern COLON_PATTERN = Pattern.compile(":");

	private static final Pattern AMPERSAND_PATTERN = Pattern.compile("&");

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5341991679804476496L;

	private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

	protected int id;

	private String name;

	private int season;

	private int episode;

	public BaseSeries() {

	}

	public BaseSeries(String name, int season, int episode) {
		this.name = name;
		this.season = season;
		this.episode = episode;
	}

	/**
	 * Auto-generated primary key.
	 * 
	 * @return the id for this entity object
	 */
	public abstract int getId();

	public abstract void setId(int id);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	/**
	 * Increment the season by 1.
	 */
	public void incrementSeason() {
		season++;
	}

	/**
	 * Increment the episode by 1.
	 */
	public void incrementEpisode() {
		episode++;
	}

	/**
	 * Determine whether or not another entity is equal to this one.
	 */
	@Override
	public boolean equals(Object o) {
		boolean equal = false;

		if (o instanceof BaseSeries) {
			@SuppressWarnings("rawtypes")
			BaseSeries series = (BaseSeries) o;

			equal = normalizedNameEquals(this.getName(), series.getName())
					&& (series.getEpisode() == this.getEpisode())
					&& (series.getSeason() == this.getSeason());

		}

		return equal;
	}

	/**
	 * Compares the normalized versions of series names. Removes special
	 * characters and also performs case insensitive.
	 * 
	 * @param name
	 *            the first name
	 * @param name2
	 *            the second name
	 * @return true if they are equal
	 */
	public static boolean normalizedNameEquals(String name, String name2) {
		return normalizeName(name).equalsIgnoreCase(normalizeName(name2));
	}

	/**
	 * Normalizes names.
	 * 
	 * @param name
	 *            the name
	 * @return the normalized name
	 */
	private static String normalizeName(String name) {
		if (name != null) {
			name = AMPERSAND_PATTERN.matcher(name).replaceAll("and");
			name = COLON_PATTERN.matcher(name).replaceAll("");
		}

		return name;
	}

	/**
	 * Retrieve the hashcode for this object.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getName())
				.append(this.getEpisode()).append(this.getSeason())
				.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[").append(this.getName())
				.append("]");
		sb.append(" season [").append(this.getSeason()).append("]");
		sb.append(" episode [").append(this.getEpisode()).append("]");

		return sb.toString();
	}

	public String toDownloadedEpisodeNamingString() {
		String showNameReplacingSpaces = normalizeName(SPACE_PATTERN.matcher(
				getName()).replaceAll("_"));

		StringBuilder sb = new StringBuilder(showNameReplacingSpaces)
				.append("_S");
		sb.append(leftPadZerosToTwoPlaces(this.getSeason())).append("E");
		sb.append(leftPadZerosToTwoPlaces(this.getEpisode()));

		return sb.toString();
	}

	private String leftPadZerosToTwoPlaces(int season) {
		return StringUtils.leftPad("" + season, 2, '0');
	}
}
