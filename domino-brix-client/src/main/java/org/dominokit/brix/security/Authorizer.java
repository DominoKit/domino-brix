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

import org.dominokit.brix.events.HasRoles;

/**
 * Strategy used by presenters to evaluate whether access should be granted based on the current
 * security context and required roles.
 */
public interface Authorizer {
  /**
   * Determines if the context allows the resource represented by {@link HasRoles}.
   *
   * @param context security context containing the current user
   * @param hasRoles instance exposing required roles
   * @return {@code true} if access is permitted
   */
  boolean isAuthorized(IsSecurityContext context, HasRoles hasRoles);
}
