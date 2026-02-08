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

import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.dominokit.brix.annotations.Global;
import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.BrixStartupTask;
import org.dominokit.brix.api.Config;
import org.dominokit.brix.api.ConfigImpl;
import org.dominokit.brix.events.BrixEvents;
import org.dominokit.brix.security.SecurityContext;
import org.dominokit.brix.tasks.TasksRunner;
import org.dominokit.domino.history.AppHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core runtime services holder created by Dagger. Exposes globally shared router, event bus, slots,
 * configuration, security context, and startup runner used by the framework.
 */
@Singleton
public class BrixCore {

  private static final Logger LOGGER = LoggerFactory.getLogger(BrixCore.class);
  private final AppHistory router;
  private final TasksRunner tasksRunner;
  private final BrixEvents events;
  private final BrixSlots slots;
  private final Config config;
  private final SecurityContext securityContext;

  @Inject
  public BrixCore(
      @Global AppHistory router,
      TasksRunner tasksRunner,
      @Global BrixEvents events,
      @Global BrixSlots slots,
      @Global Config config,
      SecurityContext securityContext) {
    this.router = router;
    this.tasksRunner = tasksRunner;
    this.events = events;
    this.slots = slots;
    this.config = config;
    this.securityContext = securityContext;
  }

  /**
   * @return shared application router
   */
  public AppHistory getRouter() {
    return router;
  }

  /**
   * @return shared event bus
   */
  public BrixEvents getEvents() {
    return this.events;
  }

  /**
   * @return shared slots registry
   */
  public BrixSlots getSlots() {
    return this.slots;
  }

  /**
   * @return shared configuration holder
   */
  public Config getConfig() {
    return this.config;
  }

  /**
   * @return security context used by authorizers
   */
  public SecurityContext getSecurityContext() {
    return securityContext;
  }

  /**
   * @return orchestrator responsible for ordered startup tasks
   */
  public TasksRunner getTasksRunner() {
    return tasksRunner;
  }

  /**
   * Initializes configuration with the provided map.
   *
   * @param config key/value config entries
   */
  public void init(Map<String, String> config) {
    ((ConfigImpl) getConfig()).init(config);
  }

  /**
   * Executes the supplied startup tasks and notifies the handler upon completion.
   *
   * @param tasks tasks to execute grouped by their order
   * @param handler callback invoked when all tasks finish
   */
  public void start(Set<BrixStartupTask> tasks, Runnable handler) {
    tasksRunner.runTasks(tasks, handler);
  }
}
