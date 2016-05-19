/**
 * Copyright (C) 2016 MadInnovations
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

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 5/18/2016.
 */
public interface FilterCreator {
	/**
	 * Creates a DaoFilter instance from the given values.
	 *
	 * @param operator  the operator to use when comparing the field value.
	 * @param fieldName  the name of the field to filter on.
	 * @param value  the value to use in the filter comparison.
	 * @return  a DaoFilter instance.
	 */
	public DaoFilter createDaoFilter(DaoFilter.Operator operator, String fieldName, String value);
}
