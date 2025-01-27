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
import static java.util.Objects.nonNull;

import org.dominokit.domino.history.TokenFilter;

public interface RoutingProvider {
  default String getRoutingPath() {
    return "";
  }

  default boolean isHashBasedRouting() {
    return containsHashInPath();
  }

  default boolean containsHashInPath() {
    String path = getRoutingPath();
    if (nonNull(path) && !path.isEmpty()) {
      return path.contains("#");
    }
    return false;
  }

  default TokenFilter tokenFilter() {
    String path = getRoutingPath();
    if (isNull(path) || path.trim().isEmpty()) {
      return TokenFilter.any();
    } else {
      return isHashBasedRouting()
          ? TokenFilter.startsWithFragment(path)
          : TokenFilter.startsWithPathFilter(path);
    }
  }

  default TokenFilter getTokenFilter() {
    return tokenFilter();
  }
}
