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

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.dominokit.brix.annotations.BrixTask;
import org.dominokit.brix.api.BrixStartupTask;
import org.dominokit.brix.task.SampleTaskOneBrix;
import org.dominokit.brix.task.SampleTaskTwoBrix;
import org.dominokit.domino.client.history.StateHistory;
import org.dominokit.domino.history.AppHistory;

@Module
public class TestModuleOne implements PresentationModule {

  private CoreComponent coreComponent;

  @Inject
  public TestModuleOne(CoreComponent coreComponent) {
    this.coreComponent = coreComponent;
  }

  @Override
  public CoreComponent coreComponent() {
    return coreComponent;
  }

  @Singleton
  @Provides
  @AppOne
  public AppHistory router() {
    return new StateHistory("appone");
  }

  @Singleton
  @Provides
  @IntoSet
  @BrixTask
  public BrixStartupTask taskOne() {
    return new SampleTaskOneBrix();
  }

  @Singleton
  @Provides
  @IntoSet
  @BrixTask
  public BrixStartupTask taskTwo() {
    return new SampleTaskTwoBrix();
  }
}
