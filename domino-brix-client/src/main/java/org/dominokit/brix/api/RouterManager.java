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

import org.dominokit.brix.Brix;
import org.dominokit.brix.HasPresenterProvider;
import org.dominokit.domino.history.TokenFilter;

/**
 * Utility responsible for binding presenter instances to router token filters. Generated routing
 * classes delegate here to wire presenters to history changes.
 */
public class RouterManager {

  /**
   * Registers a route for a standalone presenter.
   *
   * @param routing provider exposing the route filter
   * @param componentProvider provider that supplies the presenter component
   * @param <P> presenter type
   */
  public static <P extends Presenter<?>> void initRoute(
      RoutingProvider routing,
      ComponentProvider<? extends HasPresenterProvider<? extends PresenterProvider<P>>>
          componentProvider) {
    Brix.get()
        .getCoreComponent()
        .core()
        .getRouter()
        .listen(
            routing.getTokenFilter(),
            state -> {
              Presenter<?> presenter = componentProvider.get().getPresenterProvider().get();
              presenter.bindToComponent(componentProvider);
              presenter.setRoutingState(state);
              if (!presenter.isActive()) {
                presenter.activate();
              }
            });

    Brix.get()
        .getCoreComponent()
        .core()
        .getRouter()
        .listen(
            TokenFilter.not(routing.getTokenFilter()),
            state -> {
              Presenter<?> presenter = componentProvider.get().getPresenterProvider().get();
              if (presenter.isActive()) {
                presenter.detach();
              }
            });
  }

  /**
   * Registers a route for a child presenter that requires a parent presenter to be active.
   *
   * @param routing provider exposing the route filter
   * @param componentProvider provider that supplies the child presenter component
   * @param parentProvider provider that supplies the parent presenter component
   * @param <P> parent presenter type
   * @param <C> child presenter type
   */
  public static <P extends Presenter<?>, C extends ChildPresenter<P, ?>> void initRoute(
      RoutingProvider routing,
      ComponentProvider<? extends HasPresenterProvider<? extends PresenterProvider<C>>>
          componentProvider,
      ComponentProvider<? extends HasPresenterProvider<? extends PresenterProvider<P>>>
          parentProvider) {
    Brix.get()
        .getCoreComponent()
        .core()
        .getRouter()
        .listen(
            routing.getTokenFilter(),
            state -> {
              C presenter = componentProvider.get().getPresenterProvider().get();
              presenter.bindToComponent(componentProvider);
              presenter.setParent(parentProvider.get().getPresenterProvider().get());
              presenter.setRoutingState(state);
              if (!presenter.isActive()) {
                presenter.activate();
              }
            });
    Brix.get()
        .getCoreComponent()
        .core()
        .getRouter()
        .listen(
            TokenFilter.not(routing.getTokenFilter()),
            state -> {
              C presenter = componentProvider.get().getPresenterProvider().get();
              if (presenter.isActive()) {
                presenter.detach();
              }
            });
  }
}
