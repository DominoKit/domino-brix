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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Default in-memory implementation of {@link Config}. */
@Singleton
public class ConfigImpl implements Config {
  private final Map<String, String> configs = new HashMap<>();

  @Inject
  public ConfigImpl() {}

  /**
   * Initializes the configuration map. This method may only be called once.
   *
   * @param configs map of configuration entries
   * @throws IllegalOperationException if called after initialization
   */
  public void init(Map<String, String> configs) {
    if (!this.configs.isEmpty()) {
      throw new IllegalOperationException("Config have already been initialized.");
    }
    this.configs.putAll(configs);
  }

  @Override
  /** {@inheritDoc} */
  public Optional<String> get(String key) {
    return Optional.ofNullable(configs.get(key));
  }

  /** Thrown when attempting to mutate a sealed configuration. */
  public static final class IllegalOperationException extends RuntimeException {
    public IllegalOperationException(String message) {
      super(message);
    }
  }
}
