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
package org.dominokit.brix.tests;

import elemental2.dom.HTMLDivElement;
import javax.inject.Inject;
import org.dominokit.brix.annotations.UiView;
import org.dominokit.brix.api.Slot;
import org.dominokit.brix.api.UiHandlers;
import org.dominokit.brix.impl.BrixView;

@UiView
public class ShellViewImpl extends BrixView<HTMLDivElement, UiHandlers> implements TestViewOne {

  @Inject
  public ShellViewImpl() {}

  @Override
  public HTMLDivElement element() {
    return null;
  }

  @Override
  public boolean isAttached() {
    return false;
  }

  @Override
  public Slot getContentSlot(String key) {
    return null;
  }

  @Override
  public Slot getMenuSlot(String key) {
    return null;
  }
}
