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
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;
import org.dominokit.brix.annotations.BrixTask;
import org.dominokit.brix.annotations.Global;
import org.dominokit.brix.api.BrixSlots;
import org.dominokit.brix.api.Config;
import org.dominokit.brix.api.ConfigImpl;
import org.dominokit.brix.api.StartupTask;
import org.dominokit.brix.events.BrixEvents;
import org.dominokit.brix.security.IsSecurityContext;
import org.dominokit.brix.security.SecurityContext;
import org.dominokit.brix.tasks.TasksRunner;
import org.dominokit.domino.client.history.StateHistory;
import org.dominokit.domino.history.AppHistory;

@Module
public class CoreModule {

  @Singleton
  @Provides
  @Global
  public AppHistory router() {
    return new StateHistory();
  }

  @Singleton
  @Provides
  public TasksRunner tasksRunner() {
    return new TasksRunner();
  }

  @Singleton
  @Provides
  @Global
  public BrixEvents events() {
    return new BrixEvents();
  }

  @Singleton
  @Provides
  @Global
  public BrixSlots slots() {
    return new BrixSlots();
  }

  @Singleton
  @Provides
  public IsSecurityContext securityContext() {
    return new SecurityContext();
  }

  @Singleton
  @Provides
  @Global
  public Config config() {
    return new ConfigImpl();
  }

  @Provides
  @BrixTask
  public static List<StartupTask> provideEmptyTasksSet() {
    return Collections.emptyList();
  }
}
