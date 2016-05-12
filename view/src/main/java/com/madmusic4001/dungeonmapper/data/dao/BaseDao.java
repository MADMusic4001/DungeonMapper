/**
 * Copyright (C) 2015 MadMusic4001
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madmusic4001.dungeonmapper.data.dao;

import java.util.Collection;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/2/2015.
 */
public interface BaseDao<T> {

	/**
	 * Returns the number of instances of T in the storage medium.
	 *
	 * @return the number of instance of T in the storage medium. Implementors must throw a
	 * DaoException if an error occurs.
	 */
	public int count();

	/**
	 * Load an instance of T from storage.
	 *
	 * @param id
	 * @return an instance of type T if the item is found or null if not found. Implementing
	 * classes should throw a DaoException if any error occurs.
	 */
	public T load(String id);

	/**
	 * Load all instances of T from storage which match the filter. Implementors must return an
	 * empty Collection if there are no instances of T in the storage medium and throw a
	 * DaoException if any errors occur.
	 *
	 * @param filter an instance of T containing the information to filter on. Null fields will
	 *                  be ignored.
	 * @return a Collection of T instances which match the filter. Implementors must return an
	 * empty Collection if there are no instances of T in the storage medium and throw a
	 * DaoException if any errors occur.
	 */
	public Collection<T> loadWithFilter(T filter);

	/**
	 * Loads all instances of T from persistent storage.
	 *
	 * @return a Collection of T instanced. Implementors must return an empty Collection if there
	 * are no instances of T in the storage medium and throw a DaoException if any errors occur.
	 */
	public Collection<T> loadAll();

	/**
	 * Save an existing or new instance of T to persistent storage. Implementing classes should
	 * throw a DaoException if any error occurs.
	 *
	 * @param entity the instance of T to be saved.
	 */
	public void save(T entity);

	/**
	 * Delete an existing instance of T from persistent storage. Implementing classes should
	 * throw a DaoException if any error occurs. Non-existing of the entity in the storage
	 * medium should not be considered an error.
	 *
	 * @param entity the instance of T to be saved.
	 */
	public void delete(T entity);
}
