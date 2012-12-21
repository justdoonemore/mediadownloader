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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jdom.mediadownloader.domain.User;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesNotification;
import com.jdom.persist.persistence.AbstractDAS;

@Repository
public class SeriesNotificationDAS extends AbstractDAS<SeriesNotification>
		implements SeriesNotificationDASService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<SeriesNotification> getSeriesNotificationsForUser(
			User user) {
		Collection<SeriesNotification> notifications = runQuery("SELECT c FROM "
				+ SeriesNotification.class.getSimpleName()
				+ " c where c.user = '" + user.getId() + "'");

		return notifications;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param series
	 * @return
	 */
	@Override
	public Collection<SeriesNotification> getSeriesNotificationsForSeriesByName(
			Series series) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", series.getName());

		Collection<SeriesNotification> notifications = runQuery(
				"SELECT c FROM " + SeriesNotification.class.getSimpleName()
						+ " c where c.series.name = :name", params);

		return notifications;
	}

	@Override
	protected Class<SeriesNotification> getDASClass() {
		return SeriesNotification.class;
	}
}