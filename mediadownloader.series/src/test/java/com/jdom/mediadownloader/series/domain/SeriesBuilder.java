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
package com.jdom.mediadownloader.series.domain;

import com.jdom.mediadownloader.series.domain.Series;

/**
 * @author djohnson
 * 
 */
public class SeriesBuilder {

	private String name = "seriesName";

	private int season = 2;

	private int episode = 1;

	public SeriesBuilder() {
	}

	/**
	 * @param anotherBuilder
	 */
	public SeriesBuilder(SeriesBuilder anotherBuilder) {
		this.name = anotherBuilder.name;
		this.season = anotherBuilder.season;
		this.episode = anotherBuilder.episode;
	}

	/**
	 * @param name
	 * @return
	 */
	public SeriesBuilder withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @param season
	 * @return
	 */
	public SeriesBuilder withSeason(int season) {
		this.season = season;
		return this;
	}

	/**
	 * @param episode
	 * @return
	 */
	public SeriesBuilder withEpisode(int episode) {
		this.episode = episode;
		return this;
	}

	/**
	 * @return
	 */
	public SeriesBuilder withIncrementedEpisode() {
		this.episode++;
		return this;
	}

	/**
	 * @return
	 */
	public Series build() {
		return new Series(name, season, episode);
	}

}
