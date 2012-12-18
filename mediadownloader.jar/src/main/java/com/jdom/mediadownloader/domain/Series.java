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

import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class Series extends AbstractEntity<Series> {

	private static final Pattern COLON_PATTERN = Pattern.compile(":");

	private static final Pattern AMPERSAND_PATTERN = Pattern.compile("&");

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5341991679804476496L;

	private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

	private int id;

	private String name;

	private int season;

	private int episode;

	public Series() {

	}

	public Series(String name, int season, int episode) {
		this.name = name;
		this.season = season;
		this.episode = episode;
	}

	/**
	 * Auto-generated primary key.
	 * 
	 * @return the id for this entity object
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	@Override
	public int compareTo(Series o) {
		int compareVal = this.getName().compareToIgnoreCase(o.getName());

		// Sort by Series name
		return compareVal;
	}

	/**
	 * Determine whether or not another entity is equal to this one.
	 */
	@Override
	public boolean equals(Object o) {
		boolean equal = false;

		if (o instanceof Series) {
			Series series = (Series) o;

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

	/**
	 * Clone the series.
	 */
	@Override
	public Series clone() {
		Series series = new Series(name, season, episode);

		return series;
	}

	private String leftPadZerosToTwoPlaces(int season) {
		return StringUtils.leftPad("" + season, 2, '0');
	}
}
