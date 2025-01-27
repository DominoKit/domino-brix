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
import static java.util.stream.Collectors.groupingBy;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.dominokit.brix.api.BrixStartupTask;
import org.dominokit.domino.api.shared.extension.ContextAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TasksRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(TasksRunner.class);

  public void runTasks(Set<BrixStartupTask> tasks, Runnable completeHandler) {
    TreeSet<TasksAggregator> sorted =
        tasks.stream()
            .collect(groupingBy(BrixStartupTask::order))
            .entrySet()
            .stream()
            .map(taskEntry -> new TasksAggregator(taskEntry.getKey(), taskEntry.getValue()))
            .collect(Collectors.toCollection(TreeSet::new));

    Iterator<TasksAggregator> iterator = sorted.iterator();
    TasksAggregator current = null;
    if (iterator.hasNext()) {
      current = iterator.next();
    }
    while (nonNull(current) && iterator.hasNext()) {
      TasksAggregator next = iterator.next();
      current.setNextAggregator(next);
      current = next;
    }

    ContextAggregator.waitFor(sorted).onReady(completeHandler::run);

    if (sorted.iterator().hasNext()) {
      TasksAggregator first = sorted.first();
      first.execute();
    } else {
      completeHandler.run();
    }
    LOGGER.info("Tasks execution completed.!");
  }
}
