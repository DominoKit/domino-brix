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

import static java.util.Objects.nonNull;

import java.util.Set;

public interface HasContext<T> {

  default void update(IsContext<T> context) {
    setContext(context);
    getContextListeners()
        .forEach(
            contextListener -> {
              contextListener.onContextChange(context);
            });
  }

  void setContext(IsContext<T> context);

  IsContext<T> getContext();

  Set<ContextListener<T>> getContextListeners();

  default T registerContextListener(ContextListener<T> contextListener) {
    getContextListeners().add(contextListener);
    if (nonNull(getContext()) && nonNull(getContext().getData())) {
      contextListener.onContextChange(getContext());
    }
    return (T) this;
  }

  default T removeContextListener(ContextListener<? super T> contextListener) {
    getContextListeners().remove(contextListener);
    return (T) this;
  }

  interface ContextListener<T> {
    void onContextChange(IsContext<T> context);
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

  interface IsContext<T> {
    T getData();

    default Operation getOperation() {
      return Operation.UPDATED;
    }

    Object getSource();

    static <T> IsContext<T> of(Object source, T data, Operation operation) {
      return new IsContext<T>() {
        @Override
        public T getData() {
          return data;
        }

        @Override
        public Object getSource() {
          return source;
        }

        @Override
        public Operation getOperation() {
          return operation;
        }
      };
    }

    static <T> IsContext<T> updated(Object source, T data) {
      return of(source, data, Operation.UPDATED);
    }

    static <T> IsContext<T> deleted(Object source, T data) {
      return of(source, data, Operation.DELETED);
    }

    static <T> IsContext<T> created(Object source, T data) {
      return of(source, data, Operation.CREATED);
    }
  }
}
