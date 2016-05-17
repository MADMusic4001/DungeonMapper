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
package com.madmusic4001.dungeonmapper.data.dao.impl.sql;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;

/**
 * Data that can be used during SQL storage operations to filter results.
 */
public class DaoFilterSqlImpl extends DaoFilter {
	/**
	 * Creates a DaoFilterSqlImpl instance
	 *
	 * @param operator  the comparison operator to use
	 * @param fieldName  the name of the SQL column to filter on
	 * @param value  the value to compare to
	 */
	public DaoFilterSqlImpl(Operator operator, String fieldName, String value) {
		super(operator, fieldName, value);
	}

	@Override
	public String getFilterString() {
		StringBuilder builder = new StringBuilder(this.getFieldName().length() + 10);
		builder.append(this.getFieldName());

		switch (this.getOperator()) {
			case EQUALS:
				builder.append("=?");
				break;
			case NOT_EQUALS:
				builder.append("!=?");
				break;
			case GREATER_THAN:
				builder.append(">?");
				break;
			case GREATER_THAN_OR_EQUAL:
				builder.append(">=?");
				break;
			case LESS_THAN:
				builder.append("<?");
				break;
			case LESS_THAN_OR_EQUAL:
				builder.append("<=?");
				break;
			case IN:
				builder.append(" in(?)");
				break;
			case NOT_IN:
				builder.append(" not in(?)");
				break;
		}
		return builder.toString();
	}
}
