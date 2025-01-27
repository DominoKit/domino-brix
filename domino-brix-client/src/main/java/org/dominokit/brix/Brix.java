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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import javax.inject.Inject;
import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.BrixStartupTask;
import org.dominokit.brix.api.Config;
import org.dominokit.brix.events.BrixEvents;
import org.dominokit.domino.history.AppHistory;

public class Brix {

  private static final Brix INSTANCE =
      Brix_Factory.newInstance(DaggerCoreComponent.builder().build());

  private final CoreComponent coreComponent;

  @Inject
  public Brix(CoreComponent coreComponent) {
    this.coreComponent = coreComponent;
  }

  public CoreComponent getCoreComponent() {
    return coreComponent;
  }

  public static Brix get() {
    return INSTANCE;
  }

  public AppHistory router() {
    return coreComponent.core().getRouter();
  }

  public BrixSlots slots() {
    return coreComponent.core().getSlots();
  }

  public BrixEvents events() {
    return coreComponent.core().getEvents();
  }

  public Config config() {
    return coreComponent.core().getConfig();
  }

  public void init(Map<String, String> config) {
    coreComponent.core().init(config);
  }

  public void start(Collection<BrixStartupTask> tasks, Runnable handler) {
    coreComponent.core().start(new HashSet<>(tasks), handler);
  }
}
