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

public abstract class BrixStartupTask {

  private final ContextAggregator.ContextWait<BrixStartupTask> contextWait;

  protected BrixStartupTask() {
    this.contextWait = new ContextAggregator.ContextWait<>();
  }

  public ContextAggregator.ContextWait<BrixStartupTask> getContextWait() {
    return contextWait;
  }

  public final void complete() {
    contextWait.complete(this);
  }

  public abstract void run();

  public int order() {
    return 0;
  }
}
