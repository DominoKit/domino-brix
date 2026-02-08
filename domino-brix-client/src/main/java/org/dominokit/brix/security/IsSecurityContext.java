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
package org.dominokit.brix.security;

import java.util.Collection;
import org.dominokit.brix.events.BrixUser;

/**
 * Contract for retrieving user details and executing role-based authorization checks. Presenters
 * rely on this context when applying {@link Authorizer} strategies.
 */
public interface IsSecurityContext {
  /**
   * @return the current authenticated user
   */
  BrixUser getUser();

  /**
   * @return {@code true} when a user is authenticated
   */
  boolean isAuthenticated();

  /**
   * @return {@code true} when the user has the given role
   */
  boolean isAuthorizedFor(String role);

  /**
   * @return {@code true} when the user has all provided roles
   */
  boolean isAuthorizedForAll(String... roles);

  /** Reports unauthorized access to the configured handler. */
  void reportUnAuthorizedAccess();

  /**
   * @return {@code true} when the user has all provided roles
   */
  boolean isAuthorizedForAll(Collection<String> roles);

  /**
   * @return {@code true} when the user has any of the provided roles
   */
  boolean isAuthorizedForAny(String... roles);

  /**
   * @return {@code true} when the user has any of the provided roles
   */
  boolean isAuthorizedForAny(Collection<String> roles);
}
