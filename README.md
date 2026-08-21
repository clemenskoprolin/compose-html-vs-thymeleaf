# Compose HTML vs Thymeleaf

A Spring Boot prototype that renders the same klibs.io project catalog in two ways:

- `GET /composehtml` calls the local `composeHtmlToString { ... }` implementation.
- `GET /thymeleaf` returns the conventional `catalog.html` Thymeleaf view.

Both endpoints use the same `CatalogPage` model, the same compiled Tailwind stylesheet, and the same search flow.

## Run it

This project expects the Compose Multiplatform checkout beside it at `../compose-multiplatform/html`.

```shell
./gradlew bootRun
```

Then open either:

- <http://localhost:8080/composehtml>
- <http://localhost:8080/thymeleaf>

To point at another checkout location:

```shell
./gradlew bootRun -Pcompose.html.checkout=/absolute/path/to/compose-multiplatform/html
```

## Develop with automatic reloads

Use three terminals from the project directory:

```shell
# Terminal 1: rebuild Tailwind whenever Kotlin or template classes change
npm run css:watch

# Terminal 2: recompile Kotlin whenever source files change
./gradlew classes --continuous

# Terminal 3: run the server; DevTools restarts it after compilation
./gradlew bootRun
```

## What happens on a search

1. The browser submits the GET form, for example `/composehtml?query=html&platforms=wasm`.
2. Spring asks `CatalogService` for a shared page model.
3. `KlibsMcpClient` initializes the public Streamable HTTP MCP endpoint and calls the `searchProjects` tool, passing the selected platforms straight through.
4. The selected renderer turns the model into a complete HTML response, and the browser loads the new page.

## Tailwind CSS

The generated CSS is checked in under `src/main/resources/static/app.css`, so Node is not required to run the server. Regenerate it after changing UI classes with:

```shell
npm install
npm run css:build
```

During development, use `npm run css:watch` instead; it stays running and regenerates the stylesheet after each UI edit.

## Verify

```shell
./gradlew test
```
