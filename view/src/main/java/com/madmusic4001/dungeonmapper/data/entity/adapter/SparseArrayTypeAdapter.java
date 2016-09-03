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
package com.madmusic4001.dungeonmapper.data.entity.adapter;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 5/19/2016.
 */
public class SparseArrayTypeAdapter<T> extends TypeAdapter<SparseArray<T>> {
	private final Gson gson = new Gson();
	private final Class<T> classOfT;
	private final Type typeOfSparseArrayOfT      = new TypeToken<SparseArray<T>>() {}.getType();
	private final Type typeOfSparseArrayOfObject = new TypeToken<SparseArray<Object>>() {}.getType();

	/**
	 * Creates a new instance of SparseArrayTypeAdapter<T> with the given parameter.
	 *
	 * @param classOfT  an instance of Class<T>
	 */
	public SparseArrayTypeAdapter(Class<T> classOfT) {
		this.classOfT = classOfT;
	}

	@Override
	public void write(JsonWriter jsonWriter, SparseArray<T> tSparseArray) throws IOException {
		if (tSparseArray == null) {
			jsonWriter.nullValue();
			return;
		}
		gson.toJson(gson.toJsonTree(tSparseArray, typeOfSparseArrayOfT), jsonWriter);
	}

	@Override
	public SparseArray<T> read(JsonReader jsonReader) throws IOException {
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		}
		SparseArray<Object> temp = gson.fromJson(jsonReader, typeOfSparseArrayOfObject);
		SparseArray<T> result = new SparseArray<>(temp.size());
		int key;
		JsonElement tElement;
		for (int i = 0; i < temp.size(); i++) {
			key = temp.keyAt(i);
			tElement = gson.toJsonTree(temp.get(key));
			result.put(key, gson.fromJson(tElement, classOfT));
		}
		return result;
	}
}
