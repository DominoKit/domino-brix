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

import org.dominokit.domino.history.HistoryToken;
import org.dominokit.domino.history.NormalizedToken;
import org.dominokit.domino.history.TokenFilter;

public class CompositeFilter implements TokenFilter {

  private final TokenFilter root;
  private final TokenFilter[] subFilters;

  public static CompositeFilter of(TokenFilter root, TokenFilter... subFilters) {
    return new CompositeFilter(root, subFilters);
  }

  public CompositeFilter(TokenFilter root, TokenFilter... subFilters) {
    this.root = root;
    this.subFilters = subFilters;
  }

  @Override
  public boolean filter(HistoryToken token) {
    return root.filter(token) && TokenFilter.and(subFilters).filter(token);
  }

  @Override
  public NormalizedToken normalizeToken(String rootPath, String token) {
    return root.normalizeToken(rootPath, token);
  }
}
