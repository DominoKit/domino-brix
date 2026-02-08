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

/**
 * Allows storing and propagating a typed context object with lifecycle operations to interested
 * listeners.
 *
 * @param <T> type that owns the context
 */
public interface HasContext<T> {

  /**
   * Updates the context and notifies listeners.
   *
   * @param context new context value
   */
  default void update(IsContext<T> context) {
    setContext(context);
    getContextListeners()
        .forEach(
            contextListener -> {
              contextListener.onContextChange(context);
            });
  }

  /** Sets the current context. */
  void setContext(IsContext<T> context);

  /**
   * @return current context value
   */
  IsContext<T> getContext();

  /**
   * @return listeners interested in context changes
   */
  Set<ContextListener<T>> getContextListeners();

  /**
   * Registers a listener and immediately notifies it if context already exists.
   *
   * @param contextListener listener to add
   * @return this instance for chaining
   */
  default T registerContextListener(ContextListener<T> contextListener) {
    getContextListeners().add(contextListener);
    if (nonNull(getContext()) && nonNull(getContext().getData())) {
      contextListener.onContextChange(getContext());
    }
    return (T) this;
  }

  /**
   * Removes a context listener.
   *
   * @param contextListener listener to remove
   * @return this instance for chaining
   */
  default T removeContextListener(ContextListener<? super T> contextListener) {
    getContextListeners().remove(contextListener);
    return (T) this;
  }

  /** Listener notified when the context changes. */
  interface ContextListener<T> {
    void onContextChange(IsContext<T> context);
  }

  /** Describes an operation performed on the context payload. */
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

  /** Wrapper for context data, operation type, and source. */
  interface IsContext<T> {
    /**
     * @return context data
     */
    T getData();

    /**
     * @return operation performed on the data
     */
    default Operation getOperation() {
      return Operation.UPDATED;
    }

    /**
     * @return source object that produced the context
     */
    Object getSource();

    /** Factory to create a context with explicit operation. */
    static <T> IsContext<T> of(Object source, T data, Operation operation) {
      return new IsContext<T>() {
        @Override
        /** {@inheritDoc} */
        public T getData() {
          return data;
        }

        @Override
        /** {@inheritDoc} */
        public Object getSource() {
          return source;
        }

        @Override
        /** {@inheritDoc} */
        public Operation getOperation() {
          return operation;
        }
      };
    }

    /** Creates an UPDATED context wrapper. */
    static <T> IsContext<T> updated(Object source, T data) {
      return of(source, data, Operation.UPDATED);
    }

    /** Creates a DELETED context wrapper. */
    static <T> IsContext<T> deleted(Object source, T data) {
      return of(source, data, Operation.DELETED);
    }

    /** Creates a CREATED context wrapper. */
    static <T> IsContext<T> created(Object source, T data) {
      return of(source, data, Operation.CREATED);
    }
  }
}
