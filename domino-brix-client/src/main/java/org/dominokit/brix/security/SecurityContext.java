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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.dominokit.brix.events.BrixUser;
import org.dominokit.brix.events.UserAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SecurityContext implements IsSecurityContext {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContext.class);
  private static final BrixUser NULL_USER = new NullUser();
  private BrixUser user;
  private Runnable unauthorizedAccessHandler =
      () -> {
        LOGGER.error("Access denied, user does not have the required roles.");
      };

  @Inject
  public SecurityContext() {}

  public BrixUser getUser() {
    return nonNull(user) ? user : NULL_USER;
  }

  @Override
  public boolean isAuthenticated() {
    return getUser().isAuthenticated();
  }

  public SecurityContext setUser(BrixUser user) {
    this.user = user;
    return this;
  }

  public void setUnauthorizedAccessHandler(Runnable handler) {
    this.unauthorizedAccessHandler = handler;
  }

  public void reportUnAuthorizedAccess() {
    unauthorizedAccessHandler.run();
  }

  public boolean isAuthorizedFor(String role) {
    return getUser().getRoles().contains(role);
  }

  public boolean isAuthorizedForAll(String... roles) {
    return isAuthorizedForAll(Arrays.asList(roles));
  }

  public boolean isAuthorizedForAll(Collection<String> roles) {
    if (isNull(roles)) {
      return false;
    }
    return getUser().getRoles().containsAll(roles);
  }

  public boolean isAuthorizedForAny(String... roles) {
    return isAuthorizedForAny(Arrays.asList(roles));
  }

  public boolean isAuthorizedForAny(Collection<String> roles) {
    if (isNull(roles)) {
      return false;
    }
    return roles.stream().anyMatch(role -> getUser().getRoles().contains(role));
  }

  private static class NullUser implements BrixUser {
    @Override
    public boolean isAuthenticated() {
      return false;
    }

    @Override
    public Set<String> getRoles() {
      return Set.of();
    }

    @Override
    public String getId() {
      return "";
    }

    @Override
    public String getReference() {
      return "";
    }

    @Override
    public String getUserName() {
      return "";
    }

    @Override
    public String getFirstName() {
      return "";
    }

    @Override
    public String getLastName() {
      return "";
    }

    @Override
    public String getEmail() {
      return "";
    }

    @Override
    public Map<String, UserAttribute<?>> getAttributes() {
      return Map.of();
    }
  }
}
