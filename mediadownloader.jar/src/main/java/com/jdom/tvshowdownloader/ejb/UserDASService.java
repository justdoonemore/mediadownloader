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

import com.jdom.persist.persistence.AbstractDASService;
import com.jdom.tvshowdownloader.domain.User;

public interface UserDASService extends AbstractDASService<User> {

    /**
     * Return the user associated to a specific name.
     * 
     * @param name
     *            the name of the user
     * @return the user or null if no user was found
     */
    User findByName(String name);

}