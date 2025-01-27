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

public interface UserAttribute<T> {

  default Class<T> getType() {
    return nonNull(getValue()) ? (Class<T>) getValue().getClass() : null;
  }

  default boolean isOfType(Class<T> type) {
    return nonNull(getType()) && getType().equals(type);
  }

  default void ifTypeIs(Class<T> type, Consumer<T> consumer) {
    if (isOfType(type)) {
      consumer.accept(getValue());
    }
  }

  T getValue();

  static UserAttribute<String> of(String value) {
    return () -> value;
  }

  static UserAttribute<Integer> of(Integer value) {
    return () -> value;
  }

  static UserAttribute<Double> of(Double value) {
    return () -> value;
  }

  static UserAttribute<Long> of(Long value) {
    return () -> value;
  }

  static UserAttribute<Boolean> of(Boolean value) {
    return () -> value;
  }
}
