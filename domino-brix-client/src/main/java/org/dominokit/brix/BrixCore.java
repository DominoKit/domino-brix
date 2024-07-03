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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.dominokit.brix.annotations.Global;
import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.Config;
import org.dominokit.brix.api.ConfigImpl;
import org.dominokit.brix.api.RoutingTask;
import org.dominokit.brix.api.StartupTask;
import org.dominokit.brix.events.BrixEvents;
import org.dominokit.brix.security.SecurityContext;
import org.dominokit.brix.tasks.TasksRunner;
import org.dominokit.domino.history.AppHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class BrixCore {

  private static final Logger LOGGER = LoggerFactory.getLogger(BrixCore.class);
  private final AppHistory router;
  private final TasksRunner tasksRunner;
  private final BrixEvents events;
  private final BrixSlots slots;
  private final Config config;
  private final SecurityContext securityContext;

  private final Set<StartupTask> tasks = new HashSet<>();

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

  public AppHistory getRouter() {
    return router;
  }

  public BrixEvents getEvents() {
    return this.events;
  }

  public BrixSlots getSlots() {
    return this.slots;
  }

  public Config getConfig() {
    return this.config;
  }

  public SecurityContext getSecurityContext() {
    return securityContext;
  }

  public TasksRunner getTasksRunner() {
    return tasksRunner;
  }

  public void register(StartupTask task) {
    this.tasks.add(task);
  }

  public void register(RoutingTask task) {
    LOGGER.info("Registering routing task : " + task.getClass().getCanonicalName());
  }

  public void init(Map<String, String> config) {
    ((ConfigImpl) getConfig()).init(config);
  }

  public void start(Runnable handler) {
    tasksRunner.runTasks(tasks, handler);
  }
}
