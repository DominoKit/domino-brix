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

/** A target location where presenters can reveal or remove {@link Viewable} instances. */
public interface Slot {
  String BRIX_SLOT_KEY = "brix-slot-key";
  String BRIX_SLOT_TYPE = "brix-slot-type";

  /**
   * @return unique identifier for the slot
   */
  String getKey();

  /**
   * Reveals the view inside this slot.
   *
   * @param view view to attach
   */
  void reveal(Viewable view);

  /**
   * Removes the view from the slot.
   *
   * @param view view to detach
   */
  void remove(Viewable view);

  /** Hook invoked when the slot is registered. */
  default void onRegistered() {}

  /** Hook invoked when the slot is removed. */
  default void onRemoved() {}
}
