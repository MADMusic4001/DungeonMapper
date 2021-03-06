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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

/**
 *
 */
public class TerrainSpinnerAdapter extends ArrayAdapter<Terrain> {
	private LayoutInflater       inflater;
	private View.OnClickListener onStickyToggleListener;

	/**
	 * @see android.widget.ArrayAdapter#ArrayAdapter(android.content.Context, int)
	 */
	public TerrainSpinnerAdapter(Context context, View.OnClickListener onStickyToggleListener) {
		super(context, android.R.layout.simple_spinner_item);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onStickyToggleListener = onStickyToggleListener;
	}

	/**
	 * @see android.widget.SpinnerAdapter#getDropDownView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ImageView(getContext());
			convertView.setBackgroundColor(Color.GRAY);
		}

		((ImageView) convertView).setImageBitmap(getItem(position).getImage());
		return convertView;
	}

	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.sticky_image_spinner_row, parent, false);

			holder = new ViewHolder();
			holder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
			holder.stickyToggle = (ImageView) convertView.findViewById(R.id.stickyToggle);
			holder.stickyToggle.setOnClickListener(onStickyToggleListener);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.itemImage.setImageBitmap(getItem(position).getImage());
		if (parent.getTag() != null && (Boolean)parent.getTag()) {
			holder.stickyToggle.setImageResource(android.R.drawable.ic_secure);
		}
		else {
			holder.stickyToggle.setImageResource(android.R.drawable.ic_partial_secure);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView itemImage;
		ImageView stickyToggle;
	}
}
