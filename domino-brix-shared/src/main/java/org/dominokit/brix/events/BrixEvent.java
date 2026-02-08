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

import static java.util.Objects.nonNull;

import java.util.Optional;

/**
 * Base type for events dispatched through {@link org.dominokit.brix.events.BrixEvents}. Concrete
 * subclasses carry payload specific to a feature. The framework records the source when events are
 * fired to help consumers trace the origin.
 */
public abstract class BrixEvent {

  private final Object source;
  private final long timestamp;
  private final String eventId;

  protected BrixEvent() {
    this(null);
  }

  protected BrixEvent(Object source) {
    this.source = source;
    this.timestamp = System.currentTimeMillis();
    this.eventId = java.util.UUID.randomUUID().toString();
  }

  /**
   * Returns the object that fired the event if it was captured.
   *
   * @return optional event source
   */
  public Optional<Object> getSource() {
    return Optional.ofNullable(source);
  }

  /**
   * Returns the event creation time as a millisecond epoch value.
   *
   * @return event timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Returns a unique identifier for this event instance.
   *
   * @return event id
   */
  public String getEventId() {
    return eventId;
  }

  /**
   * Returns the concrete type of this event.
   *
   * @return event class
   */
  public Class<? extends BrixEvent> getType() {
    return getClass();
  }

  /**
   * Checks if this event matches the given event type. Useful for listeners that handle multiple
   * event classes.
   *
   * @param target the expected event class
   * @return {@code true} if this event has the same class as the target class
   */
  public boolean isSameType(Class<? extends BrixEvent> target) {
    return nonNull(target) && target.equals(getClass());
  }
}
