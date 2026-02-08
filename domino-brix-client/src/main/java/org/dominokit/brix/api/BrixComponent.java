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

/**
 * Marker for components that participate in presenter lifecycle. Generated components implement
 * these callbacks to mirror presenter activation.
 */
public interface BrixComponent {
  /** Called when the owning presenter becomes active. */
  void onActivated();

  /** Called when the owning presenter is deactivated. */
  void onDeactivated();
}
