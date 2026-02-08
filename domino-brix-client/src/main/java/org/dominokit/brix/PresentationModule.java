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

/**
 * Exposes the core graph to feature modules. Implementations supply the {@link CoreComponent} that
 * backs shared router, events, slots, and configuration bindings.
 */
@Module
public interface PresentationModule {

  /**
   * @return the application-level core component
   */
  CoreComponent coreComponent();

  /**
   * @return global router reference shared with feature components
   */
  @Singleton
  @Provides
  @Global
  default AppHistory globalRouter() {
    return coreComponent().core().getRouter();
  }

  /**
   * @return shared event bus instance
   */
  @Singleton
  @Provides
  @Global
  default BrixEvents globalEvents() {
    return coreComponent().core().getEvents();
  }

  /**
   * @return shared slots registry
   */
  @Singleton
  @Provides
  @Global
  default BrixSlots globalSlots() {
    return coreComponent().core().getSlots();
  }

  /**
   * @return shared configuration holder
   */
  @Singleton
  @Provides
  @Global
  default Config globalConfig() {
    return coreComponent().core().getConfig();
  }

  /**
   * @return security context used by authorizers in presenters
   */
  @Singleton
  @Provides
  default IsSecurityContext globalSecurityContext() {
    return coreComponent().core().getSecurityContext();
  }
}
