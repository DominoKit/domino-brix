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
package org.dominokit.brix.api;

import static java.util.Objects.isNull;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.dominokit.brix.events.RegistrationRecord;
import org.dominokit.brix.impl.BodyElementSlot;
import org.dominokit.brix.impl.NoContentSlot;
import org.dominokit.brix.impl.PopupSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry and dispatcher for named UI slots. Slots represent attachment points in the DOM where
 * presenters reveal their views.
 */
public class BrixSlots {
  private static final Logger LOGGER = LoggerFactory.getLogger(BrixSlots.class);
  public static final String BRIX_BODY_SLOT = "brix-body-slot";
  public static final String BRIX_NO_CONTENT_SLOT = "brix-no-content-slot";
  public static final String BRIX_POPUP_SLOT = "brix-popup-slot";

  private final Map<String, Deque<Slot>> slots = new HashMap<>();
  private final Set<SlotListener> listeners = new HashSet<>();

  public BrixSlots() {
    register(BodyElementSlot.create());
    register(PopupSlot.create());
    register(NoContentSlot.create());
  }

  /**
   * Registers a listener for slot lifecycle events.
   *
   * @param listener listener to add
   * @return registration record for un-listening
   */
  public RegistrationRecord listen(SlotListener listener) {
    this.listeners.add(listener);
    return () -> listeners.remove(listener);
  }

  /**
   * Registers the slot and notifies listeners.
   *
   * @param slot slot implementation
   * @return registration record that will unregister the slot
   */
  public RegistrationRecord register(Slot slot) {
    LOGGER.info(" >> REGISTERING SLOT [" + slot.getKey() + "]");

    if (!slots.containsKey(slot.getKey())) {
      slots.put(slot.getKey(), new LinkedList<>());
    }
    slots.get(slot.getKey()).push(slot);
    slot.onRegistered();
    listeners.forEach(listener -> listener.onSlotRegistered(slot.getKey()));

    return () -> unRegister(slot);
  }

  /**
   * Unregisters a slot and triggers its removal callback.
   *
   * @param slot slot to remove
   */
  public void unRegister(Slot slot) {
    LOGGER.info(" << REMOVING SLOT [" + slot.getKey() + "]");
    if (slots.containsKey(slot.getKey())) {
      Slot result = slots.get(slot.getKey()).pop();
      result.onRemoved();
    }
  }

  /**
   * Looks up a slot by key.
   *
   * @param key slot identifier
   * @return optional latest registered slot with that key
   */
  public Optional<Slot> findSlot(String key) {
    if (isNull(key) || key.isEmpty() || !slots.containsKey(key)) {
      return Optional.empty();
    }
    return Optional.ofNullable(slots.get(key).peek());
  }

  /** Thrown when a presenter attempts to reveal without a registered slot. */
  public static class SlotNotDefinedException extends RuntimeException {
    public SlotNotDefinedException(String message) {
      super(message);
    }
  }

  /** Listener for slot registration/removal events. */
  public interface SlotListener {
    /** Called when a slot becomes available. */
    void onSlotRegistered(String key);

    /** Called when a slot is removed. */
    void onSlotRemoved(String key);
  }
}
