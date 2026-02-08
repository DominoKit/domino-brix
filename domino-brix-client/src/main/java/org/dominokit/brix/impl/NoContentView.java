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

import org.dominokit.brix.api.AttachableAware;
import org.dominokit.brix.api.AttachableBinder;
import org.dominokit.brix.api.HandlersAware;
import org.dominokit.brix.api.IsAttachable;
import org.dominokit.brix.api.NoContentViewable;
import org.dominokit.brix.api.UiHandlers;

/** Base implementation for no-content views that can participate in attach/detach callbacks. */
public abstract class NoContentView<U extends UiHandlers>
    implements NoContentViewable, HandlersAware<U>, IsAttachable {

  private AttachableAware attachable;
  private boolean attached = false;

  @Override
  public void onReveal() {
    if (nonNull(attachable)) {
      attachable.onAttach();
      attached = true;
    }
  }

  @Override
  public void onRemove() {
    if (nonNull(attachable)) {
      attachable.onDetach();
      attached = false;
    }
  }

  @Override
  public boolean isAttached() {
    return attached;
  }

  @Override
  public String getId() {
    return "";
  }

  @Override
  public void detach() {
    this.onRemove();
  }

  @Override
  public AttachableBinder getAttachableBinder() {
    return new AttachableBinder() {
      @Override
      protected void initAttachable(AttachableAware attachableAware) {
        attachable = attachableAware;
      }
    };
  }
}
