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
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.view.di.PerActivity;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Populates Map information for each row in the ListView
 *
 * @author Mark
 *         Created 8/13/2014.
 */
@PerActivity
public class RegionListAdapter extends ArrayAdapter<Region> {
	private static final int LAYOUT_RESOURCE_ID = R.layout.name_timestamps_row;

	private   LayoutInflater           layoutInflater;
	private int[] colors = new int[]{
			R.color.list_even_row_background,
			R.color.list_odd_row_background};

	/**
	 * Creates a new {@code MapListAdapter} instance.
	 *
	 * @param context the view {@code Context} the adapter will be attached to.
	 */
	@Inject
	public RegionListAdapter(Context context) {
		super(context, LAYOUT_RESOURCE_ID);
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
			rowView = layoutInflater.inflate(LAYOUT_RESOURCE_ID, parent, false);
			holder = new ViewHolder(
					(TextView) rowView.findViewById(R.id.nameView),
					(TextView) rowView.findViewById(R.id.createdView),
					(TextView) rowView.findViewById(R.id.modifiedView));
			rowView.setTag(holder);
		}
		else {
			rowView = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		rowView.setBackgroundColor(ContextCompat.getColor(getContext(), colors[position % colors.length]));
		Region region = getItem(position);
		if(region != null) {
			holder.nameView.setText(region.getName());

			holder.createdView.setText(getFormattedDateOrTime(region.getCreateTs()
					.getTimeInMillis()));
			holder.modifiedView.setText(getFormattedDateOrTime(region.getModifiedTs()
					.getTimeInMillis()));
		}
		return rowView;
	}

	private String getFormattedDateOrTime(long timeInMillis) {
		String result;

		Calendar modDateTime = Calendar.getInstance();
		modDateTime.setTimeInMillis(timeInMillis);
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		if (modDateTime.after(today)) {
			result = DateFormat.getTimeFormat(getContext()).format(modDateTime.getTime());
		}
		else {
			result = DateFormat.getDateFormat(getContext()).format(modDateTime.getTime());
		}
		return result;
	}

	private class ViewHolder {
		private TextView nameView;
		private TextView createdView;
		private TextView modifiedView;

		ViewHolder(TextView nameView, TextView createdView, TextView modifiedView) {
			this.nameView = nameView;
			this.createdView = createdView;
			this.modifiedView = modifiedView;
		}
	}
}
