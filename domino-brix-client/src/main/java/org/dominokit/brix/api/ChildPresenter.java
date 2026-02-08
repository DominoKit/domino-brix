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

/**
 * Presenter that defers activation until its parent presenter is active. Useful for hierarchical
 * routing where child views depend on parent layout.
 */
public abstract class ChildPresenter<P extends Presenter<? extends Viewable>, V extends Viewable>
    extends Presenter<V> {
  private P parent;

  /**
   * @return the parent presenter
   */
  public P getParent() {
    return parent;
  }

  @Override
  void doActivate() {
    if (getParent().isActive()) {
      super.doActivate();
    } else {
      getParent().registerChildListener(super::doActivate);
    }
  }

  /** Sets the parent presenter instance. */
  public void setParent(P parent) {
    this.parent = parent;
  }

  /**
   * Hook invoked when binding to a parent. Override to coordinate child/parent state before
   * activation.
   */
  public void onBindParent(P parent) {}
}
