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
 */package com.jdom.mediadownloader.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jdom.mediadownloader.domain.AbstractEntity;

public abstract class AbstractEntityTest<ENTITY extends AbstractEntity<ENTITY>> {

	@Test
	public abstract void testNoArgConstructor();

	@Test
	public abstract void testAllArgConstructor();

	@Test
	public void testCloneMethod() {
		ENTITY instance = getNonDefaultValueInstance();
		ENTITY cloned = instance.clone();

		assertSameEntityValues(instance, cloned);
	}

	@Test
	public void testEqualsReturnsTrueForSameValues() {
		ENTITY instance = getNonDefaultValueInstance();
		ENTITY cloned = instance.clone();
		assertTrue(instance.equals(cloned));
	}

	@Test
	public void testCompareToReturnsZeroForSameValues() {
		ENTITY instance = getNonDefaultValueInstance();
		ENTITY cloned = instance.clone();
		assertEquals(0, cloned.compareTo(instance));
		assertEquals(0, instance.compareTo(cloned));
	}

	/**
	 * Assert that two versions of the entity class have the same instance
	 * variable values.
	 * 
	 * @param instance
	 *            the original instance
	 * @param cloned
	 *            the cloned instance
	 */
	protected abstract void assertSameEntityValues(ENTITY instance,
			ENTITY cloned);

	/**
	 * Get the instance with non-default values.
	 * 
	 * @return the instance without default values
	 */
	protected abstract ENTITY getNonDefaultValueInstance();
}
