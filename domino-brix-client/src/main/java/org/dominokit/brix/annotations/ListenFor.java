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
import org.dominokit.brix.events.BrixEvent;

/** Use this annotation to make a method in a presenter proxy a listener for specific event */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ListenFor {

  /**
   * @return the type of {@link BrixEvent} that method is listening for
   */
  Class<? extends BrixEvent> value();
}
