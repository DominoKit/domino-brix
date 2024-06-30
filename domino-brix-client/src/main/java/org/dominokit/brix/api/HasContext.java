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

import java.util.Set;

public interface HasContext<T> {

  default void update(T context) {
    update(context, Operation.UPDATED);
  }

  default void update(T context, Operation operation) {
    getContextListeners()
        .forEach(
            contextListener -> {
              contextListener.onContextChange(context, operation);
            });
  }

  Set<ContextListener<? super T>> getContextListeners();

  default T registerContextListener(ContextListener<? super T> contextListener) {
    getContextListeners().add(contextListener);
    return (T) this;
  }

  default T removeChangeListener(ContextListener<? super T> contextListener) {
    getContextListeners().remove(contextListener);
    return (T) this;
  }

  interface ContextListener<T> {
    void onContextChange(T context, Operation operation);
  }

  interface Operation {
    Operation CREATED = () -> "CREATED";
    Operation UPDATED = () -> "UPDATED";
    Operation DELETED = () -> "DELETED";

    String getKey();

    default boolean isEqualTo(Operation other) {
      return getKey().equals(other.getKey());
    }

    default Operation when(Operation other, Runnable runnable) {
      if (isEqualTo(other)) {
        runnable.run();
      }
      return this;
    }
  }
}
