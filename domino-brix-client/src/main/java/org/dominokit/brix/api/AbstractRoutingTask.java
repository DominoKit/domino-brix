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

/**
 * Base routing task that listens to history changes and activates a presenter when its filter
 * matches.
 *
 * @param <P> presenter type
 */
public abstract class AbstractRoutingTask<P extends Presenter<?>> implements RoutingTask {

  protected final AppHistory history;
  protected final DominoHistory.StateListener listener;

  public AbstractRoutingTask(AppHistory history, P presenter) {
    this.history = history;
    this.listener =
        state -> {
          presenter.setRoutingState(state);
          presenter.activate();
        };
    //    this.history.listen(presenter.getTokenFilter(), listener);
  }
}
