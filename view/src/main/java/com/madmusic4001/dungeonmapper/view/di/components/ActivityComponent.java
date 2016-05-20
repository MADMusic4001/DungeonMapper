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
package com.madmusic4001.dungeonmapper.view.di.components;

import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldActivity;
import com.madmusic4001.dungeonmapper.view.activities.selectWorld.SelectWorldActivity;
import com.madmusic4001.dungeonmapper.view.di.PerActivity;
import com.madmusic4001.dungeonmapper.view.di.modules.ActivityModule;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;
import com.madmusic4001.dungeonmapper.view.di.modules.ViewModule;

import dagger.Subcomponent;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 7/4/2015.
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
	FragmentComponent newFragmentComponent(FragmentModule fragmentModule);
//	ViewComponent newViewComponent(ViewModule viewModule);

	void injectInto(SelectWorldActivity activity);
	void injectInto(EditWorldActivity activity);
}
