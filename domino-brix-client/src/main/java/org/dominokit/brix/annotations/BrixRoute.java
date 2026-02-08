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
package org.dominokit.brix.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dominokit.brix.api.RoutingProvider;

/**
 * Associates a presenter with a routing path. The processor generates a routing provider that
 * registers the presenter with the router.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface BrixRoute {

  /**
   * @return the String module name
   */
  String value() default "";

  Class<? extends RoutingProvider> router() default UnspecifiedRouter.class;

  public final class UnspecifiedRouter implements RoutingProvider {}
}
