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
import javax.inject.Inject;

@Module
/**
 * Dagger module that bridges a pre-built {@link CoreComponent} into generated presentation modules,
 * allowing feature components to reuse the application's shared graph.
 */
public class CoreComponentModule implements PresentationModule {
  private CoreComponent coreComponent;

  /** Wraps an existing {@link CoreComponent} to expose shared bindings to feature modules. */
  @Inject
  public CoreComponentModule(CoreComponent coreComponent) {
    this.coreComponent = coreComponent;
  }

  @Override
  /**
   * @return the backing core component
   */
  public CoreComponent coreComponent() {
    return this.coreComponent;
  }
}
