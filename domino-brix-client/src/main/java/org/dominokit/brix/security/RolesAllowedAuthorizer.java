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

public class RolesAllowedAuthorizer implements Authorizer {
  public static final Authorizer INSTANCE = new RolesAllowedAuthorizer();

  @Override
  public boolean isAuthorized(IsSecurityContext context, HasRoles hasRoles) {
    return context.getUser().isAuthenticated() && context.isAuthorizedForAny(hasRoles.getRoles());
  }
}
