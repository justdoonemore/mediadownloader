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
package com.jdom.mediadownloader.domain;

import com.jdom.mediadownloader.domain.User;

public final class UserFixture {

	private static final String EMAIL_ADDRESS = "test@test.com";

	private static final String NAME = "TestUser";

	public static final User TEST_USER = new User(NAME, EMAIL_ADDRESS);

	public static String getEmailAddress() {
		return EMAIL_ADDRESS;
	}

	public static String getName() {
		return NAME;
	}

	public static User getTestUser() {
		return TEST_USER;
	}

}
