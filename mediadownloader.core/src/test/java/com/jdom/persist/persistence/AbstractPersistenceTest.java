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

import java.sql.Connection;
import java.sql.DriverManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public abstract class AbstractPersistenceTest<DAS, ENTITY> {
	protected EntityManagerFactory emFactory;

	protected EntityManager em;

	protected DAS das;

	private Connection connection;

	@Before
	public void setUp() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");
		connection = DriverManager.getConnection(
				"jdbc:hsqldb:mem:unit-testing-jpa", "sa", "");

		emFactory = Persistence.createEntityManagerFactory("MEDIASERVER::TEST");
		em = emFactory.createEntityManager();

		das = getDas(em);
	}

	@After
	public void tearDown() throws Exception {
		if (em != null) {
			em.close();
		}
		if (emFactory != null) {
			emFactory.close();
		}

		connection.createStatement().execute("SHUTDOWN");
	}

	protected void beginTransaction() {
		em.getTransaction().begin();
	}

	protected void commitTransaction() {
		em.getTransaction().commit();
	}

	protected void rollbackTransaction() {
		em.getTransaction().setRollbackOnly();
	}

	protected abstract DAS getDas(EntityManager em);
}
