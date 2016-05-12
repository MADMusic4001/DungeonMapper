/**
 * Copyright (C) 2015 MadMusic4001
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
package com.madmusic4001.dungeonmapper.view.activities.selectWorld;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.madmusic4001.dungeonmapper.R;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/25/2015.
 */
public class DbImportDialogFragment extends DialogFragment {
	private ImportDialogListener   listener;
	private int					   selectedItem = 0;

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it.
	 */
	public interface ImportDialogListener {
		void onDialogCancelClick(DialogFragment dialog);
		void onDialogOverwriteClick(DialogFragment dialog);
		void onDialogKeepClick(DialogFragment dialog);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			listener = (ImportDialogListener) activity;
		}
		catch (ClassCastException ex) {
			throw new ClassCastException(activity.getClass().getName() + "must implement "
												 + "DbImportDialogFragment.ImportDialogListener.");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		return builder.setTitle(R.string.alert_db_import_title)
				.setSingleChoiceItems(
						R.array.alert_db_import_choices,
						0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								selectedItem = which;
							}
						})
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (DbImportDialogFragment.this.selectedItem == 1) {
							listener.onDialogOverwriteClick(DbImportDialogFragment.this);
						}
						else if (DbImportDialogFragment.this.selectedItem == 0) {
							listener.onDialogKeepClick(DbImportDialogFragment.this);
						}
						else {
							listener.onDialogCancelClick(DbImportDialogFragment.this);
						}
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onDialogCancelClick(DbImportDialogFragment.this);
					}
				})
				.create();
	}
}
