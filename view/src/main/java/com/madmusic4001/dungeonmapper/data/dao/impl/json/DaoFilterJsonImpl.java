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
package com.madmusic4001.dungeonmapper.data.dao.impl.json;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 5/18/2016.
 */
public class DaoFilterJsonImpl extends DaoFilter {
	/**
	 * @see DaoFilter#DaoFilter(Operator, String, String)
	 */
	public DaoFilterJsonImpl(Operator operator, String fieldName, String value) {
		super(operator, fieldName, value);
	}

	@Override
	public String getFilterString() {
		throw new UnsupportedOperationException("No filter string needed for Json implementations");
	}
}
