/**
 * Copyright (C) 2016 MadInnovations
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
package com.madmusic4001.dungeonmapper.controller.events.world;

import com.madmusic4001.dungeonmapper.controller.events.DeletedEvent;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.Collection;

/**
 * Event representing the results of a request to delete 1 or more {@link World} instances.
 */
public class WorldsDeletedEvent extends DeletedEvent<World> {
	/**
	 * @see DeletedEvent#DeletedEvent(boolean, int, Collection)
	 */
	public WorldsDeletedEvent(boolean success, int numDeleted, Collection<World> deleted) {
		super(success, numDeleted, deleted);
	}
}
