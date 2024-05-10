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

/** A base class for lazy initialization of objects. */
public abstract class LazyInitializer {

  private Runnable function;
  private Runnable originalFunction;
  private Runnable doOnceFunction;
  private boolean initialized = false;
  private Set<Runnable> functions = new HashSet<>();
  private Set<Runnable> doOnce = new HashSet<>();
  private Set<Runnable> doOnReset = new HashSet<>();

  /**
   * Constructs a BaseLazyInitializer with the given LambdaFunction.
   *
   * @param function The LambdaFunction to initialize the object.
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

  /**
   * Applies the lazy initialization logic.
   *
   * @return This BaseLazyInitializer instance.
   */
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

  /**
   * Executes the given LambdaFunction if the object has already been initialized.
   *
   * @param lambdaFunction The LambdaFunction to execute if initialized.
   * @return This BaseLazyInitializer instance.
   */
  public LazyInitializer ifInitialized(Runnable lambdaFunction) {
    if (isInitialized()) {
      lambdaFunction.run();
    }
    return this;
  }

  /**
   * Executes the provided LambdaFunctions when the object has been initialized. If the object has
   * not been initialized yet, stores the functions for future execution.
   *
   * @param functions The LambdaFunctions to execute when initialized.
   * @return This BaseLazyInitializer instance.
   */
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

  /**
   * Executes the given LambdaFunction once when the object is initialized. If the object has
   * already been initialized, the function is executed immediately.
   *
   * @param function The LambdaFunction to execute once.
   * @return This BaseLazyInitializer instance.
   */
  public LazyInitializer doOnce(Runnable function) {
    if (isInitialized()) {
      function.run();
    } else {
      doOnce.add(function);
    }
    return this;
  }

  /**
   * Adds a LambdaFunction to be executed when the object is reset.
   *
   * @param function The LambdaFunction to execute on reset.
   * @return This BaseLazyInitializer instance.
   */
  public LazyInitializer onReset(Runnable function) {
    doOnReset.add(function);
    return this;
  }

  /**
   * Checks if the object has been initialized.
   *
   * @return True if the object has been initialized, false otherwise.
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Resets the object to its initial state, allowing it to be initialized again. Executes
   * registered onReset LambdaFunctions.
   */
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
