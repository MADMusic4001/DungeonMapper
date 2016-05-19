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

import java.util.Collection;

/**
 * Interface declaring commonly used SQL string constants
 */
public abstract class BaseDaoSql {
	public static final String CREATE_TABLE = "CREATE TABLE ";
	public static final String TEXT = " TEXT ";
	public static final String INTEGER = " INTEGER ";
	public static final String LONG = " LONG ";
	public static final String REAL = " REAL ";
	public static final String BOOLEAN = " BOOLEAN ";
	public static final String BLOB = " BLOB ";
	public static final String NOT_NULL = " NOT NULL ";
	public static final String PRIMARY_KEY = " PRIMARY KEY ";
	public static final String FOREIGN_KEY = " FOREIGN KEY ";
	public static final String COMMA = ",";
	public static final String SPACE = " ";
	public static final String CONSTRAINT = "CONSTRAINT ";
	public static final String UNIQUE = " UNIQUE ";
	public static final String REFERENCES = "REFERENCES ";
	public static final String CHECK = " CHECK ";
	public static final String SELECT = "SELECT";
	public static final String INSERT = "INSERT";
	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";
	public static final String FROM = "FROM";
	public static final String WHERE = "WHERE";
	public static final String ON = " ON ";
	public static final String IN = " IN ";
	public static final String RESTRICT = " RESTRICT ";
	public static final String CASCADE = " CASCADE ";
	public static final String EQUALS = "=";
	public static final String NOT = "!";
	public static final String AND = "and";
	public static final String PLACEHOLDER = "?";

	public String buildWhereArgs(Collection<DaoFilter> filters, Collection<String> args) {
		boolean isFirst = true;
		String whereClause = "";

		if(filters != null) {
			for(DaoFilter filter : filters) {
				if(!isFirst) {
					whereClause = whereClause + SPACE + AND + SPACE + filter.getFilterString();
				}
				else {
					whereClause = filter.getFilterString();
				}
				args.add(filter.getValue());
				isFirst = false;
			}
		}
		return whereClause;
	}
}
