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
package org.dominokit.brix.task;

import org.dominokit.brix.annotations.BrixTask;
import org.dominokit.brix.api.StartupTask;

@BrixTask
public class SampleTaskTwo extends StartupTask {
  @Override
  public void run() {
    System.out.println("Task two executed.");
    complete();
  }

  @Override
  public int order() {
    return 20;
  }
}