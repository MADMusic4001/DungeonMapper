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
package com.madmusic4001.dungeonmapper.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.view.adapters.FileSelectorAdapter;
import com.madmusic4001.dungeonmapper.view.utils.BundleConstants;
import com.madmusic4001.dungeonmapper.view.utils.FileInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/25/2015.
 */
public class FileSelectorDialogFragment extends DialogFragment {
	private static final int    DIALOG_LOAD_FILE = 1000;
	private String						extension = ".dmw";
	private ArrayList<FileInfo>			fileList;
	private File 						path = Environment.getExternalStorageDirectory();
	private String						chosenFile;
	private FileSelectorDialogListener	listener;
	private AlertDialog					dialog;

	public interface FileSelectorDialogListener {
		void onFileSelected(String fileName);
	}

	private void loadFileList() {
		if (path.exists()) {
			final FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					Log.d(this.getClass().getName(), "Accepting file dir " + dir.getName() + ", "
							+ "filename = " + filename);
					File sel = new File(dir, filename);
					return filename.endsWith(extension) || sel.isDirectory();
				}
			};
			String[] tempFileList = path.list(filter);
			fileList = new ArrayList<FileInfo>(tempFileList.length + 1);
			if(path.getParent() != null) {
				fileList.add(new FileInfo("..", true));
			}
			for(String fileName : tempFileList) {
				File file = new File(path, fileName);
				fileList.add(new FileInfo(fileName, file.isDirectory()));
			}
			Log.d(this.getClass().getName(),
				  fileList.size() + " files loaded");
		}
		else {
			fileList = new ArrayList<>(0);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (FileSelectorDialogListener) activity;
		}
		catch (ClassCastException ex) {
			throw new ClassCastException(
					activity.getClass().getName() + " must implement "
							+ "FileSelectorDialogFragment.FileSelectorDialogListener.");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		extension = getArguments().getString(BundleConstants.FILE_SELECTOR_FILTER);
		loadFileList();
		final ArrayAdapter<FileInfo> filesListAdapter = new FileSelectorAdapter(getActivity());

		filesListAdapter.addAll(fileList);
		filesListAdapter.notifyDataSetChanged();

		dialog = builder.setTitle(String.format(getString(R.string.alert_fs_title),
												extension,
												path.getAbsolutePath()))
			.setSingleChoiceItems(filesListAdapter,
				-1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int which) {
						FileInfo fileInfo = fileList.get(which);
						File file = new File(path + File.separator + fileInfo.getFileName());
						if(file.isDirectory()) {
							path = file;
							FileSelectorDialogFragment.this.getDialog().setTitle(
									String.format(getString(R.string.alert_fs_title),
												  FileSelectorDialogFragment.this.extension,
												  path.getAbsolutePath()));
							loadFileList();
							filesListAdapter.clear();
							filesListAdapter.addAll(fileList);
							filesListAdapter.notifyDataSetChanged();
							chosenFile = null;
						}
						else {
							for(FileInfo aFileInfo : fileList) {
								if(aFileInfo != fileInfo && aFileInfo.isSelected()) {
									aFileInfo.setSelected(false);
								}
								else if(aFileInfo == fileInfo) {
									fileInfo.setSelected(!fileInfo.isSelected());
									if(fileInfo.isSelected()) {
										chosenFile = path + File.separator + fileInfo.getFileName();
									}
									else {
										chosenFile = null;
									}
								}
							}
							filesListAdapter.notifyDataSetChanged();
						}
					}
				})
			.setNegativeButton(android.R.string.cancel, null)
			.setPositiveButton(android.R.string.ok,
							   new DialogInterface.OnClickListener() {
								   @Override
								   public void onClick(DialogInterface dialog, int which) {
									   listener.onFileSelected(chosenFile);
								   }
							   })
			.create();
		return dialog;
	}
}
