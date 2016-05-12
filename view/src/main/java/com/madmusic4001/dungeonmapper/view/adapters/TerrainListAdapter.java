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
import android.widget.ImageView;
import android.widget.TextView;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Locale;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 8/18/2014.
 */
public class TerrainListAdapter extends ArrayAdapter<Terrain> {
	private LayoutInflater layoutInflater;
	private Locale         locale;
	private int            layoutResourceId;
	private int[] colors = new int[]{
			R.color.list_even_row_background,
			R.color.list_odd_row_background};

	/**
	 * Creates a new {@code TerrainListAdapter} instance.
	 *
	 * @param context the view {@code Context} the adapter will be attached to.
	 * @param layoutResourceId the resource id of the layout to use for the items in the list.
	 */
	public TerrainListAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutResourceId = layoutResourceId;
		locale = context.getResources().getConfiguration().locale;
	}

	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		ViewHolder holder;

		if (convertView == null) {
			rowView = layoutInflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder((ImageView) rowView.findViewById(R.id.imageView),
									(TextView) rowView.findViewById(R.id.nameView));
			rowView.setTag(holder);
		}
		else {
			rowView = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		rowView.setBackgroundColor(getContext().getResources().getColor(
				colors[position % colors.length]));
		Terrain terrain = getItem(position);
		holder.imageView.setImageBitmap(terrain.getImage());
		holder.nameView.setText(terrain.getDisplayNameForLocaleName(locale.toString()));
		return rowView;
	}

	private class ViewHolder {
		private ImageView imageView;
		private TextView  nameView;

		ViewHolder(ImageView imageView, TextView nameView) {
			this.imageView = imageView;
			this.nameView = nameView;
		}
	}
}
