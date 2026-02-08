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
package org.dominokit.brix.events;

import java.util.Map;

/** Basic immutable profile details for a user. */
public interface UserProfile {

  /**
   * @return stable unique identifier for the user
   */
  String getId();

  /**
   * @return external reference or source-specific id
   */
  String getReference();

  /**
   * @return username used to log in or display
   */
  String getUserName();

  /**
   * @return user given name
   */
  String getFirstName();

  /**
   * @return user family name
   */
  String getLastName();

  /**
   * @return preferred email address
   */
  String getEmail();

  /**
   * Arbitrary attributes associated with the user.
   *
   * @return map of attribute name to attribute wrapper
   */
  Map<String, UserAttribute<?>> getAttributes();
}
