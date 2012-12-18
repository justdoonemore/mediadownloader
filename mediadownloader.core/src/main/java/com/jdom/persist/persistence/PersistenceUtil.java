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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

public class PersistenceUtil {

	private static final Logger LOG = Logger.getLogger(PersistenceUtil.class);

	static void close(EntityManager em, EntityManagerFactory factory) {
		if (em != null) {
			try {
				em.close();
			} catch (Throwable t) {
				LOG.error("Throwable closing entity manager:" + t.getMessage());
			}
		}

		if (factory != null) {
			try {
				factory.close();
			} catch (Throwable t) {
				LOG.error("Throwable closing entity manager factory:"
						+ t.getMessage());
			}
		}
	}
}
