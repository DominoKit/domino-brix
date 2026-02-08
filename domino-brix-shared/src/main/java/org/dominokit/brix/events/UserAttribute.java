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

import static java.util.Objects.nonNull;

import java.util.function.Consumer;

/**
 * Wrapper for arbitrary typed user attributes. Provides helper utilities to inspect the underlying
 * type without manual casts.
 *
 * @param <T> attribute value type
 */
public interface UserAttribute<T> {

  /**
   * @return runtime type for the stored value, or {@code null} if none is set
   */
  default Class<T> getType() {
    return nonNull(getValue()) ? (Class<T>) getValue().getClass() : null;
  }

  /**
   * Checks if the attribute matches the provided type.
   *
   * @param type the expected class
   * @return {@code true} when the value is not null and is exactly of the given type
   */
  default boolean isOfType(Class<T> type) {
    return nonNull(getType()) && getType().equals(type);
  }

  /**
   * Invokes the consumer if the stored value is of the requested type.
   *
   * @param type target type
   * @param consumer handler to execute when the type matches
   */
  default void ifTypeIs(Class<T> type, Consumer<T> consumer) {
    if (isOfType(type)) {
      consumer.accept(getValue());
    }
  }

  /**
   * @return the raw attribute value
   */
  T getValue();

  /** Convenience factory for string attributes. */
  static UserAttribute<String> of(String value) {
    return () -> value;
  }

  /** Convenience factory for integer attributes. */
  static UserAttribute<Integer> of(Integer value) {
    return () -> value;
  }

  /** Convenience factory for double attributes. */
  static UserAttribute<Double> of(Double value) {
    return () -> value;
  }

  /** Convenience factory for long attributes. */
  static UserAttribute<Long> of(Long value) {
    return () -> value;
  }

  /** Convenience factory for boolean attributes. */
  static UserAttribute<Boolean> of(Boolean value) {
    return () -> value;
  }
}
