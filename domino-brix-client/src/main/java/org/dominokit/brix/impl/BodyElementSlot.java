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
import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.Slot;
import org.dominokit.brix.api.Viewable;
import org.dominokit.domino.ui.IsElement;
import org.dominokit.domino.ui.elements.BodyElement;
import org.dominokit.domino.ui.utils.ElementsFactory;

/** Slot that attaches views directly to the document body. */
public class BodyElementSlot implements Slot {

  private BodyElement body = ElementsFactory.elements.body();

  private static final BodyElementSlot INSTANCE = new BodyElementSlot();

  private Viewable currentView;

  public static BodyElementSlot create() {
    return INSTANCE;
  }

  private BodyElementSlot() {}

  @Override
  public String getKey() {
    return BrixSlots.BRIX_BODY_SLOT;
  }

  @Override
  public void reveal(Viewable view) {
    if (nonNull(view)) {
      if (view instanceof IsElement<?>) {
        remove(currentView);
        DomGlobal.console.info(
            "Revealing view [" + view.getId() + "] into slot [" + getKey() + "]");
        body.appendChild((IsElement<? extends Element>) view);
        this.currentView = view;
      } else {
        throw new IllegalArgumentException(
            "Body slot requires a view that implements IsElement interface.");
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
            "Body slot requires a view that implements IsElement interface.");
      }
    }
  }
}
