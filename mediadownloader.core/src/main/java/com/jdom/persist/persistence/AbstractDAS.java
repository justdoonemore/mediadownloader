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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.jdom.logging.api.LogFactory;import com.jdom.logging.api.Logger;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractDAS<T extends Comparable<T>> implements
		AbstractDASService<T> {

	private static final Logger LOG =LogFactory.getLogger(AbstractDAS.class);

	@PersistenceContext
	protected EntityManager em;

	protected AbstractDAS() {

	}

	/**
	 * Retrieve a list of all data objects for this DAS
	 * 
	 * @return all data objects for this DAS
	 */
	@Override
	public List<T> getAll() {
		return runQuery("SELECT type FROM " + getDASClass().getSimpleName()
				+ " type");
	}

	@Override
	public T getById(int id) {
		return em.find(getDASClass(), id);
	}

	@Override
	public List<T> getMostRecent(int maxNumberOfResults) {
		return runQuery("SELECT type FROM " + getDASClass().getSimpleName()
				+ " type ORDER BY " + getIdColumn() + " DESC",
				maxNumberOfResults);
	}

	protected T findByUniqueName(String name) {
		T entity = null;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(":name", name);

		List<T> resultsWithName = runQuery("SELECT c FROM "
				+ getDASClass().getSimpleName() + " c where c.name = :name",
				params);

		if (resultsWithName.size() > 0) {
			entity = resultsWithName.get(0);
		}

		return entity;
	}

	protected String getIdColumn() {
		return "id";
	}

	/**
	 * Update the data object.
	 * 
	 * @return true if the update succeeds
	 */
	@Override
	@Transactional
	public boolean updateObject(T object) {
		boolean updated = false;

		try {
			em.merge(object);
			em.flush();

			updated = true;
		} catch (Exception e) {
			LOG.error("Exception while updating object", e);
		}

		return updated;
	}

	/**
	 * Add the data object.
	 * 
	 * @param object
	 *            the data object
	 * 
	 * @return true if the object is persisted
	 */
	@Override
	@Transactional
	public boolean addObject(T object) {
		boolean added = false;

		try {
			em.persist(object);

			em.flush();

			added = true;

		} catch (Exception e) {
			LOG.error("Exception while adding object.", e);
		}

		return added;
	}

	/**
	 * Delete the data object.
	 * 
	 * @param object
	 *            the data object
	 * 
	 * @return true if the object is deleted
	 */
	@Override
	@Transactional
	public boolean deleteObject(T object) {
		boolean deleted = false;

		try {
			// Must merge and remove the merged instance
			T mergedObject = em.merge(object);

			// Remove the merged object
			em.remove(mergedObject);

			em.flush();

			deleted = true;
		} catch (Exception e) {
			LOG.error("Exception while updating object", e);
		}
		return deleted;
	}

	/**
	 * Finds the data object.
	 * 
	 * @param object
	 *            the data object
	 * 
	 * @return the object if found, otherwise null
	 */
	@Override
	public T findObject(Object primaryKey) {
		T result = null;

		result = em.find(getDASClass(), primaryKey);

		return result;
	}

	@Override
	@Transactional
	public void deleteAll() {
		em.createQuery("DELETE FROM " + getDASClass().getSimpleName())
				.executeUpdate();
	}

	protected List<T> runQuery(String queryString) {
		return runQuery(queryString, -1);
	}

	protected List<T> runQuery(String queryString, Map<String, Object> params) {
		return runQuery(queryString, params, -1);
	}

	/**
	 * Run a specific query string and return the results.
	 * 
	 * @param queryString
	 *            The query string to run
	 * @param maxNumberOfResults
	 * @return the result list
	 */
	protected List<T> runQuery(String queryString, int maxNumberOfResults) {
		return runQuery(queryString, Collections.<String, Object> emptyMap(),
				maxNumberOfResults);
	}

	/**
	 * Run a specific query string and return the results.
	 * 
	 * @param queryString
	 *            The query string to run
	 * @param maxNumberOfResults
	 * @return the result list
	 */
	@SuppressWarnings("unchecked")
	protected List<T> runQuery(String queryString, Map<String, Object> params,
			int maxNumberOfResults) {
		Query query = em.createQuery(queryString);

		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		if (maxNumberOfResults != -1) {
			query.setMaxResults(maxNumberOfResults);
		}

		List<T> resultList = query.getResultList();
		Collections.sort(resultList);
		return resultList;
	}

	protected abstract Class<T> getDASClass();
}
