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

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import javax.inject.Singleton;
import org.dominokit.brix.annotations.BrixRoutingTask;
import org.dominokit.brix.api.RoutingTask;
import org.dominokit.brix.presenters.ChildPresenterRouting;
import org.dominokit.brix.presenters.ChildUiHandlers;
import org.dominokit.brix.presenters.TestChildPresenterImpl;
import org.dominokit.brix.presenters.TestPresenterImpl;
import org.dominokit.brix.presenters.TestPresenterRouting;
import org.dominokit.brix.presenters.TestUiHandlers;

@Module
public interface MVPModule {

  @Binds
  TestUiHandlers testPresenter(TestPresenterImpl impl);

  @Binds
  ChildUiHandlers childPresenter(TestChildPresenterImpl impl);

  @Singleton
  @Binds
  @IntoSet
  @BrixRoutingTask
  RoutingTask testRoutingTask(TestPresenterRouting routing);

  @Singleton
  @Binds
  @IntoSet
  @BrixRoutingTask
  RoutingTask childRoutingTask(ChildPresenterRouting routing);
}
