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
import javax.inject.Singleton;
import org.dominokit.brix.annotations.Global;
import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.Config;
import org.dominokit.brix.events.BrixEvents;
import org.dominokit.brix.security.IsSecurityContext;
import org.dominokit.domino.history.AppHistory;

@Module
public interface PresentationModule {

  CoreComponent coreComponent();

  @Singleton
  @Provides
  @Global
  default AppHistory globalRouter() {
    return coreComponent().core().getRouter();
  }

  @Singleton
  @Provides
  @Global
  default BrixEvents globalEvents() {
    return coreComponent().core().getEvents();
  }

  @Singleton
  @Provides
  @Global
  default BrixSlots globalSlots() {
    return coreComponent().core().getSlots();
  }

  @Singleton
  @Provides
  @Global
  default Config globalConfig() {
    return coreComponent().core().getConfig();
  }

  @Singleton
  @Provides
  default IsSecurityContext globalSecurityContext() {
    return coreComponent().core().getSecurityContext();
  }
}
