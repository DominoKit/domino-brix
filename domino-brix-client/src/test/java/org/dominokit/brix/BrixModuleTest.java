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
package org.dominokit.brix;

import org.dominokit.brix.views.TestSlot;
import org.dominokit.domino.history.AppHistory;
import org.dominokit.domino.history.StateToken;
import org.junit.jupiter.api.Test;

public class BrixModuleTest {

  @Test
  public void testModule() {
    TestComponent testComponent =
        DaggerTestComponent.builder()
            .testModuleOne(TestModuleOne_Factory.newInstance(Brix.get().getCoreComponent()))
            .build();
    Brix.get().register(testComponent.module());
    Brix.get().start(() -> System.out.println("All tasks are completed."));
    TestSlot testSlot = new TestSlot();
    Brix.get().slots().register(testSlot);
    AppHistory router = Brix.get().router();
    router.fireState(StateToken.of("presenters/test?queryParam=value1"));
    //    Brix.get()
    //        .events()
    //        .fireEvent(
    //            this,
    //            new BrixEvent() {
    //              @Override
    //              public String getType() {
    //                return "EVENT-1";
    //              }
    //            });
    //    Brix.get()
    //        .events()
    //        .fireEvent(
    //            this,
    //            new BrixEvent() {
    //              @Override
    //              public String getType() {
    //                return "EVENT-2";
    //              }
    //            });

    //    testComponent.testPresenter().deactivate();
    //    Brix.get()
    //        .events()
    //        .fireEvent(
    //            this,
    //            new BrixEvent() {
    //              @Override
    //              public String getType() {
    //                return "EVENT-3";
    //              }
    //            });
  }
}
