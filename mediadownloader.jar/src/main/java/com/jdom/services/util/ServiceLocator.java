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
 */package com.jdom.services.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jdom.services.series.download.queue.SeriesDownloadQueueManager;
import com.jdom.tvshowdownloader.ejb.ConfigurationManagerService;
import com.jdom.tvshowdownloader.ejb.SeriesDASService;
import com.jdom.tvshowdownloader.ejb.SeriesDownloadDASService;
import com.jdom.tvshowdownloader.ejb.SeriesNotificationDASService;
import com.jdom.tvshowdownloader.ejb.UserDASService;

public class ServiceLocator implements ApplicationContextAware {

	private static ApplicationContext ctx;

	private ServiceLocator() {

	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		ServiceLocator.ctx = arg0;
	}

	/**
	 * Retrieve the Configuration Manager bean.
	 * 
	 * @return The configuration manager bean
	 */
	public static ConfigurationManagerService getConfigurationManager() {
		return ctx.getBean(ConfigurationManagerService.class.getName(),
				ConfigurationManagerService.class);
	}

	/**
	 * Retrieve the Series DAS.
	 * 
	 * @return The series das
	 */
	public static SeriesDASService getSeriesDAS() {
		return ctx.getBean(SeriesDASService.class.getName(),
				SeriesDASService.class);
	}

	/**
	 * Retrieve the User DAS.
	 * 
	 * @return The user das
	 */
	public static UserDASService getUserDAS() {
		return ctx
				.getBean(UserDASService.class.getName(), UserDASService.class);
	}

	/**
	 * Retrieve the SeriesNotificationDAS.
	 * 
	 * @return The SeriesNotificationDAS
	 */
	public static SeriesNotificationDASService getSeriesNotificationDAS() {
		return ctx.getBean(SeriesNotificationDASService.class.getName(),
				SeriesNotificationDASService.class);
	}

	public static SeriesDownloadDASService getSeriesDownloadDAS() {
		return ctx.getBean(SeriesDownloadDASService.class.getName(),
				SeriesDownloadDASService.class);
	}

	public static SeriesDownloadQueueManager getSeriesDownloadQueueManager() {
		return ctx.getBean(SeriesDownloadQueueManager.class);
	}
}
