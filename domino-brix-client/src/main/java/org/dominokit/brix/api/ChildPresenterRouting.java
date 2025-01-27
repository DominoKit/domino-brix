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

import static java.util.Objects.isNull;

import org.dominokit.domino.history.TokenFilter;

public class ChildPresenterRouting implements RoutingProvider {

  private RoutingProvider parent;

  public ChildPresenterRouting(RoutingProvider parent) {
    this.parent = parent;
  }

  @Override
  public boolean isHashBasedRouting() {
    return containsHashInPath() || parent.isHashBasedRouting();
  }

  public TokenFilter tokenFilter() {
    String path = getRoutingPath();
    if (isNull(path) || path.trim().isEmpty()) {
      return parent.getTokenFilter();
    } else {
      String parentPath = parent.getRoutingPath();
      if (isNull(parentPath) || parentPath.trim().isEmpty()) {
        return isHashBasedRouting()
            ? TokenFilter.startsWithFragment(getRoutingPath())
            : TokenFilter.startsWithPathFilter(getRoutingPath());
      } else {
        if (parent.isHashBasedRouting()) {
          return TokenFilter.startsWithFragment(parentPath + getRoutingPath());
        } else {
          if (isHashBasedRouting()) {
            return CompositeFilter.of(
                parent.getTokenFilter(), TokenFilter.startsWithFragment(getRoutingPath()));
          } else {
            return TokenFilter.startsWithPathFilter(parentPath + getRoutingPath());
          }
        }
      }
    }
  }
}
