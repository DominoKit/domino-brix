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

import static java.util.Objects.nonNull;
import static org.dominokit.domino.ui.utils.Domino.elementOf;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import org.dominokit.brix.api.Slot;
import org.dominokit.brix.api.Viewable;
import org.dominokit.domino.ui.IsElement;

/** Slot that targets a DOM element by id and replaces its contents with the provided view. */
public class ElementIdSlot implements Slot {

  private final String key;

  public static ElementIdSlot of(String key) {
    return new ElementIdSlot(key);
  }

  public ElementIdSlot(String key) {
    this.key = key;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public void reveal(Viewable view) {
    if (nonNull(view)) {
      if (view instanceof IsElement<?>) {
        elementOf(DomGlobal.document.getElementById(getKey()))
            .clearElement()
            .appendChild((IsElement<? extends Element>) view);
      } else {
        throw new IllegalArgumentException(
            "Element by id slot requires a view that implements IsElement interface.");
      }
    }
  }

  @Override
  public void remove(Viewable view) {
    if (nonNull(view)) {
      if (view instanceof IsElement<?>) {
        elementOf((IsElement<? extends Element>) view).remove();
      } else {
        throw new IllegalArgumentException(
            "Element by id slot requires a view that implements IsElement interface.");
      }
    }
  }
}
