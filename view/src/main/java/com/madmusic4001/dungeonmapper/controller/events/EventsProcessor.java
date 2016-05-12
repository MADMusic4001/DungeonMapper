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
package com.madmusic4001.dungeonmapper.controller.events;

import android.content.Context;
import android.os.AsyncTask;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/2/2015.
 */
public abstract class EventsProcessor<T extends EventsProcessor<T>> {
	private Context context;
	private EventsVisitor visitor;

	/**
	 * Constructor for dependency injection
	 */
	public EventsProcessor(Context context) {
		this.context = context;
	}

	/**
	 * Based on the Visitor pattern UI classes (typically Views, Activities or Fragments) execute
	 * this Visitor 'accept' method. The implementation of this method will do the
	 * actual processing of the event either synchronously or asynchronously as needed. After
	 * processing is complete the implementing class will call the eventProcessed 'visit' method
	 * which would be implemented by the UI class.
	 *
	 * @param visitor an instance of a UI class implementing the EventVisitor interface.
	 */
	public void processEvent(EventsVisitor visitor, Object... params) {
		this.visitor = visitor;
	}

	public EventsVisitor getVisitor() {
		return visitor;
	}

	public Context getContext() {
		return context;
	}

	/**
	 *
	 */
	public interface EventsVisitor {
		/**
		 * Implementations of BaseEvent will call this method when event processing is finished.
		 * This method must be called on the UI thread. Implementing an AsyncTask in the
		 * {@link EventsProcessor#processEvent(EventsVisitor, Object...)} implementation then
		 * calling
		 * this method from the {@link AsyncTask#onPostExecute(Object)} method implementation
		 * preferred manner to accomplish this. If the event can be processed synchronously then
		 * this method can be called directly from the {@link EventsProcessor#processEvent
		 * (EventVisitor, Object...)} after processing is complete.
		 *
		 */
		public void eventProcessed();
	}
}
