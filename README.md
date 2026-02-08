![logoimage](https://raw.githubusercontent.com/DominoKit/DominoKit.github.io/master/logo/128.png)

<a title="Gitter" href="https://gitter.im/DominoKit/domino"><img src="https://badges.gitter.im/Join%20Chat.svg"></a>
[![Development Build Status](https://github.com/DominoKit/domino-brix/actions/workflows/deploy.yaml/badge.svg?branch=development)](https://github.com/DominoKit/domino-brix/actions/workflows/deploy.yaml?branch=development)
![Sonatype Nexus (Snapshots)](https://img.shields.io/badge/Snapshot-HEAD--SNAPSHOT-orange)
![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)

# Domino Brix

Domino Brix is a lightweight, annotation-processor-driven MVP framework for DominoKit GWT/J2CL apps. It wires routing, view slotting, events, startup tasks, and security using Dagger DI plus Domino history/UI utilities.

## What it provides
- Presenter and view lifecycle management with `Presenter` and `Viewable`.
- Routing over `domino-history` with `RouterManager` and `AppHistory`.
- Slotting via `BrixSlots` with default body and popup slots.
- Event bus via `BrixEvents` with lightweight listener registration.
- Startup task orchestration via ordered `BrixStartupTask`.
- Security checks through `Authorizer` strategies and `SecurityContext`.

## Modules
- `domino-brix-client`: runtime and APIs.
- `domino-brix-processor`: annotation processor and source generation.
- `domino-brix-shared`: shared models (events and user abstractions).

## Build and test
- Java 17 project (compiler release 11 bytecode).
- Run `mvn verify` at the repository root.

## Quick start (high level)
1. Implement a view and annotate the concrete class with `@UiView`.
2. Create a presenter extending `Presenter<ViewType>` and annotate with `@BrixPresenter` (optionally `@BrixRoute`).
3. Wrap the feature in `@BrixComponent(presenter=YourPresenter.class)`.
4. Initialize and start Brix from the client entry point.

```java
Brix.get().init(config);
Brix.get().start(startupTasks, () -> {
  // app started
});
```

## Documentation
- Draft docs live in `docs/`.
- `docs/sample` contains a sample Domino Brix application layout.

## License
See `LICENSE`.
 


