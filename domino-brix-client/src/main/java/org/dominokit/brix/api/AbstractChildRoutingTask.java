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

import org.dominokit.domino.history.AppHistory;
import org.dominokit.domino.history.DominoHistory;

public abstract class AbstractChildRoutingTask<
        P extends Presenter<?>, C extends ChildPresenter<P, ?>>
    implements RoutingTask {
  private final AppHistory history;

  private final DominoHistory.StateListener listener;

  public AbstractChildRoutingTask(P parent, C presenter) {
    this.history = parent.getRouter();
    bind(parent, presenter);
    this.listener =
        state -> {
          presenter.setRoutingState(state);
          presenter.activate();
        };
    //    this.history.listen(presenter.getTokenFilter(), listener);
  }

  protected void bind(P parent, C child) {
    child.setParent(parent);
    child.onBindParent(parent);
  }
}
