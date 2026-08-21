# Compose HTML vs Thymeleaf

A Spring Boot prototype that renders the same klibs.io project catalog in two ways:

- `GET /composehtml` calls the local `composeHtmlToString { ... }` implementation.
- `GET /thymeleaf` returns the conventional `catalog.html` Thymeleaf view.

Both endpoints use the same `CatalogPage` model, the same compiled Tailwind stylesheet, and the same GET search flow. Their form actions differ so each renderer remains selected after a search, while the comparison link keeps the active query when switching renderers.

The shared theme control follows the operating-system light/dark preference initially. Once toggled, the selected mode is stored in the browser and survives searches, reloads, and switches between the two renderers.

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

## What happens on a search

1. The browser submits a normal `GET`, for example `/composehtml?query=html`.
2. Spring asks `CatalogService` for a shared page model.
3. `KlibsMcpClient` initializes the public Streamable HTTP MCP endpoint and calls the `searchProjects` tool.
4. The selected renderer turns the model into the response document.

The current public klibs.io endpoint is stateless, but the client also preserves an `Mcp-Session-Id` when a compatible server returns one. Results are cached for two minutes so switching renderers does not immediately repeat the external search. If klibs.io is unavailable, the page remains usable and clearly labels its local preview data.

## Tailwind CSS

The generated CSS is checked in under `src/main/resources/static/app.css`, so Node is not required to run the server. Regenerate it after changing UI classes with:

```shell
npm install
npm run css:build
```

## Verify

```shell
./gradlew test
```
