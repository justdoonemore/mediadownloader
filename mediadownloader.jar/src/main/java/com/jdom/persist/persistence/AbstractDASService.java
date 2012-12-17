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
 */package com.jdom.persist.persistence;

import java.util.List;

/**
 * Retrieve all of the entity objects for this DAS.
 * 
 * @author djohnson
 * 
 * @param <T>
 *            The entity for this DAS
 */
public interface AbstractDASService<T> {
    public List<T> getAll();

    public List<T> getMostRecent(int maxNumberOfResults);

    public boolean updateObject(T object);

    public boolean addObject(T object);

    public boolean deleteObject(T object);

    public T findObject(Object primaryKey);
}
