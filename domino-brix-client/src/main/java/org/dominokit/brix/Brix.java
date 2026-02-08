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

/**
 * Entry point facade for Domino Brix runtime. Provides access to the shared router, slots manager,
 * event bus, configuration, and startup workflow for the application.
 *
 * <p>Use {@link #get()} to access the singleton instance.
 */
public class Brix {

  private static final Brix INSTANCE =
      Brix_Factory.newInstance(DaggerCoreComponent.builder().build());

  private final CoreComponent coreComponent;

  @Inject
  public Brix(CoreComponent coreComponent) {
    this.coreComponent = coreComponent;
  }

  /**
   * @return the Dagger core component backing this facade
   */
  public CoreComponent getCoreComponent() {
    return coreComponent;
  }

  /**
   * @return singleton instance
   */
  public static Brix get() {
    return INSTANCE;
  }

  /**
   * @return global application router
   */
  public AppHistory router() {
    return coreComponent.core().getRouter();
  }

  /**
   * @return global slots manager
   */
  public BrixSlots slots() {
    return coreComponent.core().getSlots();
  }

  /**
   * @return global event bus
   */
  public BrixEvents events() {
    return coreComponent.core().getEvents();
  }

  /**
   * @return global configuration holder
   */
  public Config config() {
    return coreComponent.core().getConfig();
  }

  /**
   * Initializes the configuration map for the application. Must be called before {@link #start} to
   * make configuration available to components.
   *
   * @param config key/value configuration entries
   */
  public void init(Map<String, String> config) {
    coreComponent.core().init(config);
  }

  /**
   * Runs the supplied startup tasks sequentially and invokes the handler once completed.
   *
   * @param tasks ordered tasks to execute
   * @param handler callback executed after all tasks finish
   */
  public void start(Collection<BrixStartupTask> tasks, Runnable handler) {
    coreComponent.core().start(new HashSet<>(tasks), handler);
  }
}
