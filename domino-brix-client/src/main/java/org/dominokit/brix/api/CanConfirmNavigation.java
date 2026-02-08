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
 * Implemented by views that can prompt the user before navigating away. Presenters automatically
 * wire a history interceptor when the view implements this contract.
 */
public interface CanConfirmNavigation {

  /**
   * @return {@code true} if navigation should be confirmed
   */
  boolean isConfirmNavigation();

  /**
   * Triggers the confirmation flow.
   *
   * @param handlers callbacks to signal confirmation or cancellation
   */
  void confirmNavigation(ConfirmNavigationHandlers handlers);
}
