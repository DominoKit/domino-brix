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
package org.dominokit.brix.tests.presenters;

import dagger.Lazy;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import org.dominokit.brix.annotations.BrixPresenter;
import org.dominokit.brix.annotations.BrixRoute;
import org.dominokit.brix.annotations.BrixSlot;
import org.dominokit.brix.annotations.FragmentParameter;
import org.dominokit.brix.annotations.ListenFor;
import org.dominokit.brix.annotations.OnActivated;
import org.dominokit.brix.annotations.OnBeforeReveal;
import org.dominokit.brix.annotations.OnDeactivated;
import org.dominokit.brix.annotations.OnRemove;
import org.dominokit.brix.annotations.OnReveal;
import org.dominokit.brix.annotations.OnStateChanged;
import org.dominokit.brix.annotations.PathParameter;
import org.dominokit.brix.annotations.QueryParameter;
import org.dominokit.brix.annotations.RegisterSlots;
import org.dominokit.brix.annotations.UiHandler;
import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.ChildPresenter;
import org.dominokit.brix.tests.SampleEvent;
import org.dominokit.brix.tests.SampleEvent2;
import org.dominokit.brix.tests.TestViewOne;

@BrixPresenter
@BrixSlot(BrixSlots.BRIX_BODY_SLOT)
@RegisterSlots({"content", "menu"})
@PermitAll
@BrixRoute("/childone")
public class TestPresenterOneChild extends ChildPresenter<TestPresenterOne, TestViewOne>
    implements TestViewOne.TestOneUiHandlers {

  @Inject Lazy<TestViewOne> view;
  @PathParameter String patha;

  @QueryParameter List<String> queryParam;

  @FragmentParameter String fragmentParam;

  @ListenFor(SampleEvent.class)
  public void onSampleEvent(SampleEvent event) {}

  @ListenFor(SampleEvent2.class)
  public void onSampleEvent2(SampleEvent2 event) {}

  @PostConstruct
  public void afterConstructed() {}

  @PostConstruct
  public void afterConstructed2() {}

  @OnStateChanged
  public void stateChanged1() {}

  @OnStateChanged
  public void stateChangedO2() {}

  @OnActivated
  public void activated1() {}

  @OnActivated
  public void activated2() {}

  @OnDeactivated
  public void deactivated1() {}

  @OnDeactivated
  public void deactivated2() {}

  @OnBeforeReveal
  public void beforeReveal1() {}

  @OnBeforeReveal
  public void beforeReveal2() {}

  @OnReveal
  public void reveal1() {}

  @OnReveal
  public void reveal12() {}

  @OnRemove
  public void remove1() {}

  @OnRemove
  public void remove2() {}

  @UiHandler
  public void handler1() {}

  @UiHandler
  public String handler2(int a) {
    return null;
  }

  @UiHandler
  public String handler3(Consumer<String> a) {
    return null;
  }
}
