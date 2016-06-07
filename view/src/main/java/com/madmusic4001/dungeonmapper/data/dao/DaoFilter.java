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
 * Data that can be used during persistent data storage operations to filter results.
 */
public abstract class DaoFilter {
	public enum Operator {
		EQUALS,
		NOT_EQUALS,
		GREATER_THAN,
		GREATER_THAN_OR_EQUAL,
		LESS_THAN,
		LESS_THAN_OR_EQUAL,
		IN,
		NOT_IN
	}
	private Operator operator;
	private String   fieldName;
	private String   value;

	public DaoFilter(Operator operator, String fieldName, String value) {
		this.operator = operator;
		this.fieldName = fieldName;
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getValue() {
		return value;
	}

	public abstract String getFilterString();

	@Override
	public String toString() {
		return "DaoFilter{" +
				"operator=" + operator +
				", fieldName='" + fieldName + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}