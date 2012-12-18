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
package com.jdom.services.series.download;

import com.jdom.mediadownloader.services.ConfigurationManager;

public class SabnzbdNzbDownloader implements NzbDownloader {

	public SabnzbdNzbDownloader(final ConfigurationManager configurationManager) {
	}

	@Override
	public void downloadNzbs() {
		// Empty implementation, SABNZBD will download the contents and then
		// they will be picked up in a future run
	}

}
