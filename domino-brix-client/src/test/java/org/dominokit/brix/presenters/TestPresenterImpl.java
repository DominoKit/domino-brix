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

import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.dominokit.brix.AppOne;
import org.dominokit.brix.events.BrixEvent;
import org.dominokit.brix.views.TestSlot;
import org.dominokit.domino.history.AppHistory;

@Singleton
public class TestPresenterImpl extends TestPresenter {

  @Inject @AppOne AppHistory appOneRouter;

  @Inject Lazy<TestPresenterRouting> routing;

  @Inject
  public TestPresenterImpl() {}

  @Override
  protected String getSlotKey() {
    return TestSlot.TEST_SLOT;
  }

  @Override
  public void onEventReceived(BrixEvent event) {}

  @Override
  public String getRoutingPath() {
    return "presenters/test";
  }
}
