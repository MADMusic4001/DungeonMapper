/**
 * Copyright (C) 2014 MadMusic4001
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
package com.madmusic4001.dungeonmapper.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.madmusic4001.dungeonmapper.R;

import java.util.Locale;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 8/18/2014.
 */
public class LocaleListAdapter extends ArrayAdapter<Locale> {
	private LayoutInflater layoutInflater;

	/**
	 * Creates a new {@code TerrainListAdapter} instance.
	 *
	 * @param context the view {@code Context} the adapter will be attached to.
	 */
	public LocaleListAdapter(Context context) {
		super(context, android.R.layout.simple_spinner_item);
		layoutInflater = (LayoutInflater) context.getSystemService(Context
																		   .LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		ViewHolder holder;

		if (convertView == null) {
			rowView = layoutInflater.inflate(R.layout.local_list_row, parent, false);
			holder = new ViewHolder((TextView) rowView.findViewById(R.id.displayName),
									(TextView) rowView.findViewById(R.id.displayCode));
			rowView.setTag(holder);
		}
		else {
			rowView = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		Locale locale = getItem(position);
		holder.displayName.setText(locale.getDisplayName());
		holder.displayCode.setText(locale.toString());
		return rowView;
	}

	private class ViewHolder {
		private TextView displayName;
		private TextView displayCode;

		ViewHolder(TextView imageView, TextView nameView) {
			this.displayName = imageView;
			this.displayCode = nameView;
		}
	}
}
