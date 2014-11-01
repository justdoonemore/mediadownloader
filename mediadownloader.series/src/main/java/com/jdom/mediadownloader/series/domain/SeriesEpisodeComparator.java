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

import java.util.Comparator;

public class SeriesEpisodeComparator implements Comparator<Series> {

	@Override
	public int compare(Series seriesOne, Series seriesTwo) {
		int order = 0;

		int firstSeason = seriesOne.getSeason();
		int firstEpisode = seriesOne.getEpisode();
		int secondSeason = seriesTwo.getSeason();
		int secondEpisode = seriesTwo.getEpisode();

		if (firstSeason > secondSeason) {
			order = 1;
		} else if (firstSeason < secondSeason) {
			order = -1;
		} else {
			if (firstEpisode > secondEpisode) {
				order = 1;
			} else if (firstEpisode < secondEpisode) {
				order = -1;
			}
		}

		return order;

	}

}
