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
package org.dominokit.brix.api;

import org.dominokit.domino.api.shared.extension.ContextAggregator;

/**
 * Defines a startup task executed during application boot. Tasks are grouped by {@link #order()}
 * and executed in sequence; use {@link #complete()} to signal completion of asynchronous work.
 */
public abstract class BrixStartupTask {

  private final ContextAggregator.ContextWait<BrixStartupTask> contextWait;

  protected BrixStartupTask() {
    this.contextWait = new ContextAggregator.ContextWait<>();
  }

  /**
   * @return synchronization helper used by the task runner
   */
  public ContextAggregator.ContextWait<BrixStartupTask> getContextWait() {
    return contextWait;
  }

  /** Marks the task as completed. */
  public final void complete() {
    contextWait.complete(this);
  }

  /** Executes the task logic. Implementations must call {@link #complete()} when done. */
  public abstract void run();

  /**
   * Defines the order group this task belongs to. Tasks with the same order execute in parallel;
   * the runner proceeds to the next order once all tasks in the current group complete.
   *
   * @return order identifier, defaults to zero
   */
  public int order() {
    return 0;
  }
}
