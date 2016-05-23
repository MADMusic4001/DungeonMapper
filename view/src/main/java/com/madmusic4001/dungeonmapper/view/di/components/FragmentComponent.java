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

import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldPropsFragment;
import com.madmusic4001.dungeonmapper.view.activities.editWorld.EditWorldRegionFragment;
import com.madmusic4001.dungeonmapper.view.di.PerFragment;
import com.madmusic4001.dungeonmapper.view.di.modules.FragmentModule;

import dagger.Subcomponent;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/19/2015.
 */
@PerFragment
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {

	void injectInto(EditWorldPropsFragment fragment);
	void injectInto(EditWorldRegionFragment fragment);
}
