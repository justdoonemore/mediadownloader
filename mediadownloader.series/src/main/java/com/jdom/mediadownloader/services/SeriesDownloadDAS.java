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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.persist.persistence.AbstractDAS;

/**
 * The Data Access Store (DAS) for the Series Entity.
 * 
 * @author djohnson
 */
@Repository
public class SeriesDownloadDAS extends AbstractDAS<SeriesDownload> implements
		SeriesDownloadDASService {

	public SeriesDownloadDAS() {
		super();
	}

	@Override
	protected Class<SeriesDownload> getDASClass() {
		return SeriesDownload.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SeriesDownload getBySeriesNameSeasonAndEpisode(
			SeriesDownload download) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", download.getEntity().getName());
		params.put("season", download.getSeason());
		params.put("episode", download.getEpisode());

		List<SeriesDownload> seriesList = runQuery(
				"SELECT c FROM "
						+ getDASClass().getSimpleName()
						+ " c where c.entity.name = :name and c.season = :season and c.episode = :episode",
				params);

		if (seriesList.size() == 1) {
			return seriesList.get(0);
		} else if (seriesList.size() > 1) {
			// TODO: Warning
			return seriesList.get(0);
		} else {
			return null;
		}
	}
}
