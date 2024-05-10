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
package org.dominokit.brix.views;

import org.dominokit.brix.api.Slot;
import org.dominokit.brix.api.Viewable;

public class TestSlot implements Slot {

  public static final String TEST_SLOT = "TEST-SLOT";
  private Viewable view;

  @Override
  public String getKey() {
    return TEST_SLOT;
  }

  @Override
  public void reveal(Viewable view) {
    if (view instanceof Attachable) {
      ((Attachable) view).setAttached(true);
      this.view = view;
    }
  }

  @Override
  public void remove(Viewable view) {
    if (view instanceof Attachable) {
      ((Attachable) view).setAttached(false);
    }
  }

  public Viewable getView() {
    return view;
  }
}
