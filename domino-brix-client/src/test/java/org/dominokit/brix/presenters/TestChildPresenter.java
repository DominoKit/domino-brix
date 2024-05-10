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

import java.util.List;
import java.util.UUID;
import org.dominokit.brix.api.ChildPresenter;
import org.dominokit.brix.events.BrixEvent;
import org.dominokit.brix.views.TestView;
import org.dominokit.domino.history.StateToken;

public abstract class TestChildPresenter extends ChildPresenter<TestPresenter, TestView>
    implements ChildUiHandlers {

  private String id;
  List<String> queryParam;

  @Override
  protected void postConstruct() {
    this.id = UUID.randomUUID().toString();
  }

  @Override
  protected void onActivated() {
    System.out.println("Query param = " + queryParam);
  }

  public void callView() {
    view().render(id);
    globalRouter.fireState(StateToken.of("hello1"));
    getRouter().fireState(StateToken.of("appone/hello2"));
  }

  public void onEventType1(BrixEvent event) {
    System.out.println("Event 1 received. [" + event + "].");
  }

  public void onEventType2(BrixEvent event) {
    System.out.println("Event 2 received. [" + event + "].");
  }

  @Override
  public void onClick(String id) {
    System.out.println(">>OnClick<< " + id + " : " + this.id);
  }
}
