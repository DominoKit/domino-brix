/*
 * Copyright © 2019 Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dominokit.brix.events;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

@Singleton
public class BrixEvents {

  private final Set<EventListener> listeners = new HashSet<>();

  public RegistrationRecord register(EventListener listener) {
    listeners.add(listener);
    return () -> unregister(listener);
  }

  public void unregister(EventListener listener) {
    listeners.remove(listener);
  }

  public void fireEvent(BrixEvent event) {
    fireEvent(null, event);
  }

  public void fireEvent(Object source, BrixEvent event) {
    event.setSource(source);
    listeners.forEach(listener -> listener.onEventReceived(event));
  }
}
