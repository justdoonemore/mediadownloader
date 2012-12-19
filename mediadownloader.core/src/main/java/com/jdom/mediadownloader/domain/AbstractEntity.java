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

import java.io.Serializable;

/**
 * This class represents an abstract persistence entity. All persistence classes
 * should be serializable and comparable.
 * 
 * @author djohnson
 * 
 */
public abstract class AbstractEntity<T> implements Serializable, Comparable<T>,
		Cloneable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4454083113523797223L;

	public abstract int getId();

	public abstract void setId(int id);

	/**
	 * Forces an entity class to override toString().
	 */
	@Override
	public abstract String toString();

	/**
	 * Forces an entity class to override clone().
	 */
	@Override
	public abstract T clone();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}
