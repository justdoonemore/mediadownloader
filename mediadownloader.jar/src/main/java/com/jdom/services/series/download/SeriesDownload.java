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

import java.io.Serializable;
import java.net.URL;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jdom.mediadownloader.domain.Series;

/**
 * Helper class which associates a URL for a specific season/episode to a
 * Series.
 * 
 * @author djohnson
 * 
 */
public class SeriesDownload implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -285907628366943955L;

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

	private Series series;

	private String nzbTitle;

	private int season;

	private int episode;

	private URL link;

	public SeriesDownload(Series series, String nzbTitle, int season,
			int episode, URL link) {
		this.series = series;
		this.nzbTitle = nzbTitle;
		this.season = season;
		this.episode = episode;
		this.link = link;
	}

	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	public String getNzbTitle() {
		return nzbTitle;
	}

	public void setNzbTitle(String nzbTitle) {
		this.nzbTitle = nzbTitle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean retVal = false;

		// The two objects are equal if their series objects are equal
		// and the season and episode for download are equal
		if (obj instanceof SeriesDownload) {
			SeriesDownload that = (SeriesDownload) obj;

			retVal = this.getSeries().equals(that.getSeries());

			if (retVal) {
				retVal = (this.getSeason() == that.getSeason())
						&& (this.getEpisode() == that.getEpisode());
			}
		}

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(53, 19);

		return hashCodeBuilder.append(this.getSeries())
				.append(this.getSeason()).append(this.getEpisode())
				.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[" + series.getName() + "::" + season + "::" + episode + "]";
	}
}
