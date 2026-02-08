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

/** Lightweight event bus for in-memory event dispatch between presenters. */
@Singleton
public class BrixEvents {

  private final Set<EventListener> listeners = new HashSet<>();

  /**
   * Registers a listener to receive all fired events.
   *
   * @param listener target listener
   * @return registration record for removing the listener
   */
  public RegistrationRecord register(EventListener listener) {
    listeners.add(listener);
    return () -> unregister(listener);
  }

  /** Removes the listener from the bus. */
  public void unregister(EventListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires an event without a source.
   *
   * @param event event to dispatch
   */
  public void fireEvent(BrixEvent event) {
    fireEvent(null, event);
  }

  /**
   * Fires an event, validating the source for listeners.
   *
   * <p>Event sources are immutable; when present they must match the provided source. If the event
   * has no source, the {@code source} argument is ignored.
   *
   * @param source object that raised the event
   * @param event event payload
   */
  public void fireEvent(Object source, BrixEvent event) {
    if (event.getSource().isPresent()) {
      Object existing = event.getSource().orElse(null);
      if (source != null && existing != source) {
        throw new IllegalStateException("Event source is immutable and already set.");
      }
    }
    listeners.forEach(listener -> listener.onEventReceived(event));
  }
}
