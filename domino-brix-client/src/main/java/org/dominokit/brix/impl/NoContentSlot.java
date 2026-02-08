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

import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.NoContentViewable;
import org.dominokit.brix.api.Slot;
import org.dominokit.brix.api.Viewable;

/** Slot dedicated to rendering fallback no-content views. */
public class NoContentSlot implements Slot {

  public static NoContentSlot create() {
    return new NoContentSlot();
  }

  @Override
  public String getKey() {
    return BrixSlots.BRIX_NO_CONTENT_SLOT;
  }

  @Override
  public void reveal(Viewable view) {
    if (nonNull(view)) {
      if (view instanceof NoContentViewable) {
        ((NoContentViewable) view).onReveal();
      } else {
        throw new IllegalArgumentException(
            "Append element slot requires a view that implements IsElement interface.");
      }
    }
  }

  @Override
  public void remove(Viewable view) {
    if (nonNull(view)) {
      if (view instanceof NoContentViewable) {
        ((NoContentViewable) view).onRemove();
      } else {
        throw new IllegalArgumentException(
            "Append element slot requires a view that implements IsElement interface.");
      }
    }
  }
}
