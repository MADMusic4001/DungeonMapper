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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.controller.SelectWorldController;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.view.di.PerActivity;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Populates World information for each row in the ListView
 *
 * @author Mark Danley
 * Created 8/4/2014
 */
@PerActivity
public class WorldListAdapter extends ArrayAdapter<World> {
	private static final int LAYOUT_RESOURCE_ID = R.layout.name_timestamps_row;

	@Inject
	protected SelectWorldController controller;
	private LayoutInflater layoutInflater;
	private int[] colors = new int[]{
			R.color.list_even_row_background,
			R.color.list_odd_row_background};

	/**
	 * Creates a new {@code WorldListAdapter} instance.
	 *
	 * @param context the view {@code Context} the adapter will be attached to.
	 */
	@Inject
	public WorldListAdapter(Context context) {
		super(context, LAYOUT_RESOURCE_ID);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView;
		ViewHolder holder;

		if (convertView == null) {
			rowView = layoutInflater.inflate(LAYOUT_RESOURCE_ID, parent, false);
			holder = new ViewHolder((TextView) rowView.findViewById(R.id.nameView),
									(TextView) rowView.findViewById(R.id.createdView),
									(TextView) rowView.findViewById(R.id.modifiedView));
			rowView.setTag(holder);
		}
		else {
			rowView = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		rowView.setBackgroundColor(getContext().getResources().getColor(
				colors[position % colors.length]));
		World world = getItem(position);
		holder.nameView.setText(world.getName());
		holder.createdView.setText(getFormattedDateOrTime(world.getCreateTs().getTimeInMillis()));
		holder.modifiedView.setText(getFormattedDateOrTime(world.getModifiedTs()
																   .getTimeInMillis()));
		holder.nameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String enteredName = ((EditText)v).getText().toString();
					World world = getItem(position);
					if(!world.getName().equals(enteredName)) {
						world.setName(enteredName);
						controller.updateWorld(world);
					}
				}
				else {
					((EditText)v).selectAll();
				}
			}
		});
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
