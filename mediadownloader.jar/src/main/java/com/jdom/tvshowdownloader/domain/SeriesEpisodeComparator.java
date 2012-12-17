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

import java.util.Comparator;

import com.jdom.util.compare.CompareUtil;

public class SeriesEpisodeComparator implements Comparator<Series> {

	@Override
	public int compare(Series seriesOne, Series seriesTwo) {
		int order = CompareUtil.EQUAL;

		int firstSeason = seriesOne.getSeason();
		int firstEpisode = seriesOne.getEpisode();
		int secondSeason = seriesTwo.getSeason();
		int secondEpisode = seriesTwo.getEpisode();

		if (firstSeason > secondSeason) {
			order = CompareUtil.GREATER_THAN;
		} else if (firstSeason < secondSeason) {
			order = CompareUtil.LESS_THAN;
		} else {
			if (firstEpisode > secondEpisode) {
				order = CompareUtil.GREATER_THAN;
			} else if (firstEpisode < secondEpisode) {
				order = CompareUtil.LESS_THAN;
			}
		}

		return order;

	}

}
