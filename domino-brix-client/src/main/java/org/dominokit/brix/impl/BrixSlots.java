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
package org.dominokit.brix.impl;

import elemental2.dom.Element;
import org.dominokit.brix.api.Slot;
import org.dominokit.domino.ui.IsElement;

public class BrixSlots {

  public static Slot acceptOneElementSlot(String key, Element element) {
    return AcceptOneElementSlot.of(key, element);
  }

  public static Slot acceptOneElementSlot(String key, IsElement<? extends Element> element) {
    return AcceptOneElementSlot.of(key, element);
  }

  public static Slot appendElementSlot(String key, Element element) {
    return AppendElementSlot.of(key, element);
  }

  public static Slot appendElementSlot(String key, IsElement<? extends Element> element) {
    return AppendElementSlot.of(key, element);
  }

  public static Slot byIdSlot(String key) {
    return ElementIdSlot.of(key);
  }
}
