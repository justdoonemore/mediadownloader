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

import org.springframework.stereotype.Repository;

import com.jdom.mediadownloader.domain.User;
import com.jdom.persist.persistence.AbstractDAS;

@Repository
public class UserDAS extends AbstractDAS<User> implements UserDASService {

	public UserDAS() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByName(String name) {
		return findByUniqueName(name);
	}

	@Override
	protected Class<User> getDASClass() {
		return User.class;
	}
}
