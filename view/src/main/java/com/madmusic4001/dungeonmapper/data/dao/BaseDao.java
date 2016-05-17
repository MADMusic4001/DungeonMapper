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
 * Declaration of methods that data access objects must implement.
 */
public interface BaseDao<T> {
	/**
	 * Returns the number of instances of T in the storage medium.
	 *
	 * @param filters  a collection of DaoFilter instances that will be used to filter the
	 *                 instances to be included in the count. If the collection is null or
	 *                 empty then the method will return a count of all instances.
	 * @return the number of instance of T in the storage medium meeting the filter criteria.
	 * Implementors must throw a DaoException if an error occurs.
	 */
	public int count(Collection<DaoFilter> filters);

	/**
	 * Load an instance of T from storage.
	 *
	 * @param id  the value of the id of the instance to load.
	 * @return an instance of type T if the item is found or null if not found. Implementing
	 * classes should throw a DaoException if any error occurs.
	 */
	public T load(int id);

	/**
	 * Load all instances of T from storage which match the filter. Implementors must return an
	 * empty Collection if there are no instances of T in the storage medium and throw a
	 * DaoException if any errors occur.
	 *
	 * @param filters  a collection of DaoFilter instances containing the information to filter on.
	 *                 If the collection is null or emtpy then all instances will be loaded.
	 * @return a Collection of T instances which match the filters. Implementors must return an
	 * empty Collection if there are no instances of T in the storage medium and throw a
	 * DaoException if any errors occur.
	 */
	public Collection<T> load(Collection<DaoFilter> filters);

	/**
	 * Save an existing or new instance of T to persistent storage. Implementing classes should
	 * throw a DaoException if any error occurs.
	 *
	 * @param entity the instance of T to be saved.
	 * @return true if the entity was saved successfully, otherwise false
	 */
	public boolean save(T entity);

	/**
	 * Deletes an existing instance of T from persistent storage. If the _ID field value is < 0 then
	 * the non-null values of the other fields will be used to filter which entities will be deleted.
	 *
	 * @param filters  a collection of DaoFilter instances containing the information to filter on.
	 *                 If the collection is null or empty then all instances will be deleted
	 * @return the number of affected entity instances.
	 */
	public int delete(Collection<DaoFilter> filters);
}
