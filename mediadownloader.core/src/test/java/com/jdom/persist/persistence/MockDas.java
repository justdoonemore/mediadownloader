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
package com.jdom.persist.persistence;

import com.jdom.mediadownloader.domain.AbstractEntity;

public class MockDas extends
		AbstractDAS<com.jdom.persist.persistence.MockDas.MockEntity> {

	@Override
	protected Class<MockEntity> getDASClass() {
		return MockEntity.class;
	}

	public static class MockEntity extends AbstractEntity<MockEntity> {

		/**
		 * serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;
		private int id;

		@Override
		public MockEntity clone() {
			return new MockEntity();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public String toString() {
			return "Mock";
		}

		@Override
		public int compareTo(MockEntity arg0) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see com.jdom.mediadownloader.domain.AbstractEntity#getId()
		 */
		@Override
		public int getId() {
			return id;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see com.jdom.mediadownloader.domain.AbstractEntity#setId(int)
		 */
		@Override
		public void setId(int id) {
			this.id = id;
		}
	}

}
