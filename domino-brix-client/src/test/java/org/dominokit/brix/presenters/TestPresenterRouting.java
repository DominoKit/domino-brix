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
package org.dominokit.brix.presenters;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.dominokit.brix.annotations.Global;
import org.dominokit.brix.api.RoutingTask;
import org.dominokit.domino.history.AppHistory;
import org.dominokit.domino.history.DominoHistory;
import org.dominokit.domino.history.TokenFilter;

@Singleton
public class TestPresenterRouting implements RoutingTask {

  private final AppHistory history;
  private final DominoHistory.StateListener listener;

  @Inject
  public TestPresenterRouting(@Global AppHistory history, TestPresenterImpl presenter) {
    this.history = history;
    this.listener =
        state -> {
          presenter.queryParam = state.token().getQueryParameter("queryParam");
          presenter.activate();
        };
    this.history.listen(TokenFilter.startsWith(presenter.getRoutingPath()), listener);
  }

  public void stop() {
    this.history.removeListener(listener);
  }
}
