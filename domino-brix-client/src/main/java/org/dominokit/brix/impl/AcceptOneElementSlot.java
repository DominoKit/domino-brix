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
import org.dominokit.domino.ui.utils.DominoElement;

/**
 * Slot that clears its host element before revealing the incoming view, ensuring only one child is
 * displayed at a time.
 */
public class AcceptOneElementSlot implements Slot {

  private static final String ACCEPT_ONE_ELEMENT_SLOT = "brix-accept-one-element-slot";

  private final String key;

  private final DominoElement<Element> element;

  public static AcceptOneElementSlot of(String key, Element element) {
    return new AcceptOneElementSlot(key, element);
  }

  public static AcceptOneElementSlot of(String key, IsElement<? extends Element> element) {
    return new AcceptOneElementSlot(key, element.element());
  }

  public AcceptOneElementSlot(String key, Element element) {
    this(key, elementOf(element));
  }

  public AcceptOneElementSlot(String key, IsElement<Element> element) {
    this(key, elementOf(element));
  }

  public AcceptOneElementSlot(String key, DominoElement<Element> element) {
    this.key = key;
    this.element = element;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public void reveal(Viewable view) {
    if (nonNull(view)) {
      if (view instanceof IsElement<?>) {
        DomGlobal.console.info("Revealing view [" + view.getId() + "] into slot [" + key + "]");
        element.clearElement().appendChild((IsElement<? extends Element>) view);
      } else {
        throw new IllegalArgumentException(
            "AcceptOneElement slot requires a view that implements IsElement interface.");
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
            "AcceptOneElement slot requires a view that implements IsElement interface.");
      }
    }
  }

  @Override
  public void onRegistered() {
    this.element.setAttribute(Slot.BRIX_SLOT_KEY, key);
    this.element.setAttribute(Slot.BRIX_SLOT_TYPE, ACCEPT_ONE_ELEMENT_SLOT);
  }

  @Override
  public void onRemoved() {
    this.element.removeAttribute(Slot.BRIX_SLOT_KEY);
    this.element.removeAttribute(Slot.BRIX_SLOT_TYPE);
  }
}
