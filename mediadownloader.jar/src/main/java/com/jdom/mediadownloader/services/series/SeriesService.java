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
package com.jdom.mediadownloader.services.series;

import java.util.ArrayList;
import java.util.Collection;

import com.jdom.mediadownloader.domain.Series;
import com.jdom.mediadownloader.domain.SeriesNotification;
import com.jdom.mediadownloader.domain.User;
import com.jdom.mediadownloader.services.SeriesNotificationDASService;
import com.jdom.services.util.ServiceLocator;

public class SeriesService {

	/**
	 * Retrieve the collection of Users who should be notified when a particular
	 * series has a download occur.
	 * 
	 * @param series
	 *            the series
	 * @return the collection of users
	 */
	public static Collection<User> getUsersToNotifyForSeries(Series series) {
		Collection<User> users = new ArrayList<User>();

		SeriesNotificationDASService seriesNotificationDAS = ServiceLocator
				.getSeriesNotificationDAS();

		Collection<SeriesNotification> notifications = seriesNotificationDAS
				.getSeriesNotificationsForSeries(series);

		for (SeriesNotification notification : notifications) {
			users.add(notification.getUser());
		}

		return users;
	}

}
