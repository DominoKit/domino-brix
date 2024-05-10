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
package org.dominokit.brix.tasks;

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.stream.Collectors;
import org.dominokit.brix.api.StartupTask;
import org.dominokit.domino.api.shared.extension.ContextAggregator;

public class TasksAggregator extends ContextAggregator.ContextWait<Void>
    implements Comparable<TasksAggregator> {
  private ContextAggregator contextAggregator;
  private List<StartupTask> tasks;
  private TasksAggregator nextAggregator;
  private Integer order;

  public TasksAggregator(int order, List<StartupTask> tasks) {
    this.order = order;
    this.tasks = tasks;
    this.contextAggregator =
        ContextAggregator.waitFor(
                tasks.stream().map(StartupTask::getContextWait).collect(Collectors.toSet()))
            .onReady(
                () -> {
                  complete(null);
                  if (nonNull(nextAggregator)) {
                    nextAggregator.execute();
                  }
                });
  }

  public TasksAggregator setNextAggregator(TasksAggregator nextAggregator) {
    this.nextAggregator = nextAggregator;
    return this;
  }

  public void execute() {
    tasks.forEach(StartupTask::run);
  }

  @Override
  public int compareTo(TasksAggregator o) {
    return this.order.compareTo(o.order);
  }
}
