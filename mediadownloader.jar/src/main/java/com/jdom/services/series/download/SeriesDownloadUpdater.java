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

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdom.services.series.actions.NzbRetrieveAction;

public final class SeriesDownloadUpdater {

	private static final SeriesDownloadUpdater INSTANCE = new SeriesDownloadUpdater();

	private NzbRetrieveAction nzbRetrieveAction;

	public SeriesDownloadUpdater() {
	}

	public static void update() {
		boolean initialize = (INSTANCE.nzbRetrieveAction == null);
		if (initialize) {
			final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					"/applicationContext.xml", SeriesDownloadUpdater.class);

			context.close();
		}

		INSTANCE.performUpdate();
	}

	// Used by Spring
	@SuppressWarnings("unused")
	private static SeriesDownloadUpdater getInstance() {
		return INSTANCE;
	}

	public void performUpdate() {
		try {
			nzbRetrieveAction.moveDownloadedVideos();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void setNzbRetrieveAction(NzbRetrieveAction action) {
		this.nzbRetrieveAction = action;
	}
}