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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for lazy initialization of objects. Encapsulates logic for one-time execution,
 * deferred callbacks, and reset hooks.
 */
public abstract class LazyInitializer {

  private Runnable function;
  private Runnable originalFunction;
  private Runnable doOnceFunction;
  private boolean initialized = false;
  private Set<Runnable> functions = new HashSet<>();
  private Set<Runnable> doOnce = new HashSet<>();
  private Set<Runnable> doOnReset = new HashSet<>();

  /**
   * @param function runnable that performs the initialization work
   */
  public LazyInitializer(Runnable function) {
    this.function = function;
    this.originalFunction = function;
    initDoOnce();
  }

  private void initDoOnce() {
    this.doOnceFunction =
        () -> {
          for (Runnable func : doOnce) {
            func.run();
          }
          this.doOnceFunction = () -> {};
        };
  }

  /** Applies the lazy initialization logic. */
  public LazyInitializer apply() {
    if (!initialized) {
      function.run();
      function = () -> {};
      this.doOnceFunction.run();
      for (Runnable func : functions) {
        func.run();
      }
      this.initialized = true;
    }
    return this;
  }

  /** Executes the given function if already initialized. */
  public LazyInitializer ifInitialized(Runnable lambdaFunction) {
    if (isInitialized()) {
      lambdaFunction.run();
    }
    return this;
  }

  /** Executes or defers functions to run once initialization completes. */
  public LazyInitializer whenInitialized(Runnable... functions) {
    if (isInitialized()) {
      for (Runnable func : functions) {
        func.run();
      }
    } else {
      this.functions.addAll(Arrays.asList(functions));
    }
    return this;
  }

  /** Executes the given function once on initialization, or immediately if already initialized. */
  public LazyInitializer doOnce(Runnable function) {
    if (isInitialized()) {
      function.run();
    } else {
      doOnce.add(function);
    }
    return this;
  }

  /** Adds a function to run when the initializer is reset. */
  public LazyInitializer onReset(Runnable function) {
    doOnReset.add(function);
    return this;
  }

  /**
   * @return true when initialization has already run
   */
  public boolean isInitialized() {
    return initialized;
  }

  /** Resets state allowing re-application and triggers registered reset callbacks. */
  public void reset() {
    if (isInitialized()) {
      this.function = this.originalFunction;
      initDoOnce();
      this.initialized = false;
      for (Runnable func : doOnReset) {
        func.run();
      }
    }
  }
}
