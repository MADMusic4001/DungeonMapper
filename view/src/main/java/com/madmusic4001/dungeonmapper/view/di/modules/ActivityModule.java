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
package com.madmusic4001.dungeonmapper.view.di.modules;

import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldActivity;
import com.madmusic4001.dungeonmapper.view.activities.selectWorld.SelectWorldActivity;
import com.madmusic4001.dungeonmapper.view.di.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/8/2015.
 */
@PerActivity
@Module
public class ActivityModule {
	private SelectWorldActivity selectWorldActivity;
	private EditWorldActivity editWorldActivity;

	public ActivityModule(SelectWorldActivity selectWorldActivity) {
		this.selectWorldActivity = selectWorldActivity;
	}

	public ActivityModule(EditWorldActivity editWorldActivity) {
		this.editWorldActivity = editWorldActivity;
	}

	@Provides @PerActivity
	public SelectWorldActivity selectWorldActivity() {
		return this.selectWorldActivity;
	}

	@Provides @PerActivity
	public EditWorldActivity editWorldActivity() {
		return this.editWorldActivity;
	}
}
