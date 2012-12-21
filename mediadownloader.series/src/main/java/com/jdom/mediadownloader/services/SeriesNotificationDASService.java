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
 */package com.jdom.mediadownloader.services;

import java.util.Collection;

import com.jdom.mediadownloader.domain.User;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesNotification;
import com.jdom.persist.persistence.AbstractDASService;

public interface SeriesNotificationDASService extends AbstractDASService<SeriesNotification> {

	/**
	 * Retrieve the SeriesNotification objects associated to a specific user.
	 * 
	 * @param user
	 *            the user
	 * @return the collection of SeriesNotification objects
	 */
	public Collection<SeriesNotification> getSeriesNotificationsForUser(
			User user);

	/**
	 * Retrieve the SeriesNotification objects associated to a specific series.
	 * 
	 * @param series
	 *            the series
	 * @return the collection of SeriesNotification objects
	 */
	public Collection<SeriesNotification> getSeriesNotificationsForSeriesByName(
			Series series);
}