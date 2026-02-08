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

import static java.util.Objects.isNull;

import elemental2.dom.Element;
import org.dominokit.brix.api.AttachableAware;
import org.dominokit.brix.api.AttachableBinder;
import org.dominokit.brix.api.HandlersAware;
import org.dominokit.brix.api.UiHandlers;
import org.dominokit.brix.api.View;
import org.dominokit.brix.api.ViewablePopup;
import org.dominokit.domino.ui.utils.BaseDominoElement;

/**
 * Base view implementation built on Domino UI elements. Provides attachable binding and optional UI
 * handlers wiring for generated views.
 */
public abstract class BrixView<E extends Element, U extends UiHandlers>
    extends BaseDominoElement<E, BrixView<E, U>> implements View, HandlersAware<U> {

  private AttachableBinder attachableBinder;

  @Override
  public AttachableBinder getAttachableBinder() {
    if (isNull(attachableBinder)) {
      attachableBinder =
          new AttachableBinder() {
            @Override
            protected void initAttachable(AttachableAware attachableAware) {
              onAttached(mutationRecord -> attachableAware.onAttach());
              onDetached(mutationRecord -> attachableAware.onDetach());
            }
          };
      return attachableBinder;
    } else {
      return NoOpAttachableBinder.INSTANCE;
    }
  }

  private static class NoOpAttachableBinder extends AttachableBinder {
    private static final AttachableBinder INSTANCE = new NoOpAttachableBinder();

    @Override
    protected void initAttachable(AttachableAware attachableAware) {
      // Do nothing view attachable is already binded.
    }
  }

  @Override
  public void detach() {
    if (this instanceof ViewablePopup) {
      ((ViewablePopup) this).close();
    } else {
      remove();
    }
  }
}
