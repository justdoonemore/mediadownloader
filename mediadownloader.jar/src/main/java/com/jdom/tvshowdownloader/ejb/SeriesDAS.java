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
 */package com.jdom.tvshowdownloader.ejb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jdom.persist.persistence.AbstractDAS;
import com.jdom.tvshowdownloader.domain.Series;

/**
 * The Data Access Store (DAS) for the Series Entity.
 * 
 * @author djohnson
 */
@Repository
public class SeriesDAS extends AbstractDAS<Series> implements SeriesDASService {

	public SeriesDAS() {
		super();
	}

	@Override
	protected Class<Series> getDASClass() {
		return Series.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Series getSeriesByName(String name) {
		Series series = null;

		String lowerCaseName = name.toLowerCase();

		// Simple hack because law and order svu is funky
		if (lowerCaseName.startsWith("law") && lowerCaseName.endsWith("svu")
				&& lowerCaseName.indexOf("order") > -1) {
			lowerCaseName = "law & order: svu";
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", lowerCaseName);

		List<Series> theSeries = runQuery("SELECT c FROM "
				+ getDASClass().getSimpleName()
				+ " c where lower(c.name) = :name", params);

		if (theSeries.size() > 0) {
			series = theSeries.get(0);
		}

		return series;
	}
}
