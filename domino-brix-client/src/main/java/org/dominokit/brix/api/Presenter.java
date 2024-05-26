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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import org.dominokit.brix.annotations.Global;
import org.dominokit.brix.events.BrixEvent;
import org.dominokit.brix.events.BrixEvents;
import org.dominokit.brix.events.EventListener;
import org.dominokit.brix.events.RegistrationRecord;
import org.dominokit.brix.security.Authorizer;
import org.dominokit.brix.security.DefaultAuthorizer;
import org.dominokit.brix.security.HasAuthorizer;
import org.dominokit.brix.security.HasRoles;
import org.dominokit.brix.security.SecurityContext;
import org.dominokit.domino.history.AppHistory;
import org.dominokit.domino.history.DominoHistory;
import org.dominokit.domino.history.HistoryInterceptor;
import org.dominokit.domino.history.TokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Presenter<V extends Viewable>
    implements EventListener, UiHandlers, BrixSlots.SlotListener, HasRoles, HasAuthorizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Presenter.class);

  private final String ID = UUID.randomUUID().toString();

  @Inject @Global protected AppHistory globalRouter;

  @Inject @Global protected BrixEvents events;

  @Inject @Global protected BrixSlots slots;

  @Inject protected SecurityContext securityContext;
  @Inject @Global protected Config config;

  private RegistrationRecord eventsListenerRecord;

  private boolean active = false;
  private HistoryInterceptor navigationInterceptor;
  private RegistrationRecord slotListenerRecord;
  protected DominoHistory.State state;
  private final Set<RegistrationRecord> registeredSlots = new HashSet<>();
  protected V view;
  private Set<ChildListener> childListeners = new HashSet<>();

  public Presenter() {
    LOGGER.info("Presenter [" + this + "] have been created.");
  }

  private V getView() {
    if (isNull(view)) {
      this.view = view();
    }
    return this.view;
  }

  @Inject
  public final void onPostConstruct() {
    postConstruct();
  }

  protected V view() {
    return null;
  }

  public String getRoutingPath() {
    return "";
  }

  TokenFilter tokenFilter() {
    String path = getRoutingPath();
    if (isNull(path) || path.trim().isEmpty()) {
      return TokenFilter.any();
    } else {
      return TokenFilter.startsWithPathFilter(path);
    }
  }

  public final TokenFilter getTokenFilter() {
    return tokenFilter();
  }

  protected void postConstruct() {
    LOGGER.info("Presenter [" + this + "] : PostConstruct.");
  }

  private void onAttached() {
    LOGGER.info("Presenter [" + this + "] : Attached.");
    registerSlots();
    onRevealed();
    onReady();
  }

  protected void registerSlots() {}

  private void onReady() {
    LOGGER.info("Presenter [" + this + "] : Ready.");
    Set<ChildListener> temp = new HashSet<>(childListeners);
    temp.forEach(
        listener -> {
          listener.invoke();
          childListeners.remove(listener);
        });
  }

  private void onDetached() {
    LOGGER.info("Presenter [" + this + "] : Detached.");
    onRemoved();
    if (active) {
      deactivate();
    }
  }

  protected boolean isAutoReveal() {
    return true;
  }

  @Override
  public void onEventReceived(BrixEvent event) {}

  protected boolean isEnabled() {
    return true;
  }

  void doActivate() {
    if (!active && isEnabled()) {
      if (getAuthorizer().isAuthorized(securityContext, this)) {
        LOGGER.info("Presenter [" + this + "] : Activating.");
        slotListenerRecord = slots.listen(this);
        setAttachHandlers();
        setNavigationInterceptor();
        eventsListenerRecord = events.register(this);
        this.active = true;
        onActivated();
        LOGGER.info("Presenter [" + this + "] : Activated.");
        tryReveal();
      } else {
        securityContext.reportUnAuthorizedAccess();
      }
    }
  }

  public final void activate() {
    doActivate();
  }

  private void tryReveal() {
    if (isAutoReveal() && !getView().isAttached()) {
      reveal();
    }
  }

  private void setNavigationInterceptor() {
    if (getView() instanceof CanConfirmNavigation) {
      setNavigationInterceptor((CanConfirmNavigation) getView());
    } else if (getView() instanceof ProvidesConfirmNavigation) {
      setNavigationInterceptor(((ProvidesConfirmNavigation) getView()).getConfirmNavigation());
    }
  }

  private void setNavigationInterceptor(CanConfirmNavigation canConfirmNavigation) {
    if (isNull(navigationInterceptor)) {
      navigationInterceptor =
          (tokenEvent, chain) -> {
            if (canConfirmNavigation.isConfirmNavigation()) {
              canConfirmNavigation.confirmNavigation(
                  new ConfirmNavigationHandlers() {
                    @Override
                    public void onConfirmed() {
                      LOGGER.info(
                          "Presenter ["
                              + Presenter.this.getClass().getCanonicalName()
                              + "] : Navigation out confirmed.");
                      chain.next();
                    }

                    @Override
                    public void onCanceled() {
                      LOGGER.info(
                          "Presenter ["
                              + Presenter.this.getClass().getCanonicalName()
                              + "] : Navigation out canceled.");
                      tokenEvent.cancel();
                    }
                  });
            }
          };
    }
    getRouter().addInterceptor(navigationInterceptor);
  }

  private void setAttachHandlers() {
    if (getView() instanceof IsAttachable) {
      ((IsAttachable) getView())
          .getAttachableBinder()
          .initAttachable(
              new AttachableAware() {
                @Override
                public void onAttach() {
                  onAttached();
                }

                @Override
                public void onDetach() {
                  onDetached();
                }
              });
    }
  }

  @Override
  public void onSlotRegistered(String key) {
    if (key.equals(getSlotKey())) {
      tryReveal();
    }
  }

  protected final void registerSlot(Slot slot) {
    registeredSlots.add(slots.register(slot));
  }

  @Override
  public void onSlotRemoved(String key) {}

  public final void reveal() {
    Optional<Slot> slot = getSlot();
    if (slot.isPresent()) {
      onBeforeRevealed();
      slot.get().reveal(getView());
      LOGGER.info("Presenter [" + this.getClass().getCanonicalName() + "] : Revealed.");
    } else {
      LOGGER.info(
          "Presenter slot ["
              + getSlotKey()
              + "] is not ready yet, waiting for slot to be registered.");
    }
  }

  private Optional<Slot> getSlot() {
    if (isNull(getSlotKey()) || getSlotKey().trim().isEmpty()) {
      throw new BrixSlots.SlotNotDefinedException(
          "You didnt define a slot key for this presenter.");
    }
    return slots.findSlot(getSlotKey());
  }

  protected String getSlotKey() {
    return null;
  }

  public final void deactivate() {
    if (getView().isAttached()) {
      getSlot().ifPresent(slot -> slot.remove(getView()));
    } else {
      LOGGER.info("Presenter [" + this.getClass().getCanonicalName() + "] : Deactivating.");
      eventsListenerRecord.remove();
      slotListenerRecord.remove();
      if (nonNull(navigationInterceptor)) {
        getRouter().removeInterceptor(navigationInterceptor);
      }
      this.active = false;
      onDeactivated();
      LOGGER.info("Presenter [" + this.getClass().getCanonicalName() + "] : Deactivated.");
    }
  }

  protected void onBeforeStartRouting() {}

  protected void onStateChanged() {}

  protected void onActivated() {}

  protected void onDeactivated() {}

  protected void onBeforeRevealed() {}

  protected void onRevealed() {}

  protected void onRemoved() {}

  public AppHistory getRouter() {
    return globalRouter;
  }

  public final boolean isActive() {
    return active;
  }

  @Override
  public Set<String> getRoles() {
    return new HashSet<>();
  }

  @Override
  public Authorizer getAuthorizer() {
    return DefaultAuthorizer.INSTANCE;
  }

  public final void setRoutingState(DominoHistory.State state) {
    this.state = state;
    setState();
    if (isActive()) {
      onStateChanged();
    }
  }

  public void setState() {}

  void registerChildListener(ChildListener listener) {
    this.childListeners.add(listener);
  }

  interface ChildListener {
    void invoke();
  }

  @Override
  public String toString() {
    return this.getClass().getCanonicalName() + "[" + ID + "]";
  }
}
