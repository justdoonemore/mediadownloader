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

import static org.junit.Assert.assertEquals;

import com.jdom.mediadownloader.domain.User;

public final class UserTest extends AbstractEntityTest<User> {

	@Override
	public void testNoArgConstructor() {
		User user = new User();
		assertEntityValues(user, null, null);
	}

	@Override
	public void testAllArgConstructor() {
		User user = getNonDefaultValueInstance();

		assertEntityValues(user, UserFixture.getName(),
				UserFixture.getEmailAddress());
	}

	@Override
	protected void assertSameEntityValues(User instance, User cloned) {
		assertEquals(instance.getEmailAddress(), cloned.getEmailAddress());
		assertEquals(instance.getName(), cloned.getName());
	}

	/**
	 * Asserts correct values on the user object.
	 * 
	 * @param userToCheck
	 * @param expectedName
	 * @param expectedEmailAddress
	 */
	private void assertEntityValues(User userToCheck, String expectedName,
			String expectedEmailAddress) {
		assertEquals(expectedName, userToCheck.getName());
		assertEquals(expectedEmailAddress, userToCheck.getEmailAddress());
	}

	@Override
	protected User getNonDefaultValueInstance() {
		return UserFixture.getTestUser();
	}
}
