# Compose HTML server rendering examples

This repository contains two focused Spring Boot applications built on the same klibs.io catalog backend:

- `ssr-comparison` renders every request as a complete page, once with Compose HTML and once with Thymeleaf.
- `hydrated-search` server-renders Compose HTML, transfers a public catalog snapshot, and hydrates the existing DOM so later searches update in place.
- `catalog` contains the MCP client, cache, project models, and README support shared by both applications.

The projects use the Compose Multiplatform checkout at `../compose-multiplatform/html` by default. Override it with `-Pcompose.html.checkout=/absolute/path` when needed.

## Classical SSR comparison

Run:

```shell
./gradlew :ssr-comparison:bootRun
```

Then open:

- <http://localhost:8080/composehtml>
- <http://localhost:8080/thymeleaf>

Both routes use the same `CatalogPage`, stylesheet, and GET-based search flow. Every search asks Spring for a model and returns a newly rendered document. The renderer timing is written to the application log.

## Hydrated search

Run:

```shell
./gradlew :hydrated-search:bootRun
```

Then open <http://localhost:8081/>.

[`SearchDocumentRenderer.kt`](hydrated-search/src/jvmMain/kotlin/com/example/htmlcomparison/hydration/SearchDocumentRenderer.kt) wraps the shared [`SearchView.kt`](hydrated-search/src/commonMain/kotlin/com/example/htmlcomparison/hydration/SearchView.kt) in a `HydrationRoot` and transfers a public [`SearchState`](hydrated-search/src/commonMain/kotlin/com/example/htmlcomparison/hydration/SearchState.kt). In the browser, [`Main.kt`](hydrated-search/src/jsMain/kotlin/com/example/htmlcomparison/hydration/Main.kt) calls `hydrateRoot` and starts the stateful [`SearchApp`](hydrated-search/src/jsMain/kotlin/com/example/htmlcomparison/hydration/SearchApp.kt).

```text
JVM                                      Browser

static header                            hydrateRoot
HydrationRoot(SearchState)       ───▶    SearchApp
  SearchView                               SearchView
static footer
```

The initial response contains the complete page plus a serialized state. The browser deserializes that state and adopts the existing `SearchView` DOM. The static header and footer remain outside the composition.

Later interactions call `/api/search`, then recompose the view and update the URL in place. Typing is debounced, and stale responses are ignored. The form and filter links retain normal GET URLs, so search still works as a full-page request without JavaScript.

## Tailwind CSS

Shared browser assets live in `web-assets` and are included by both applications. The generated stylesheet is checked in, so Node is not required to run either server.

After changing UI classes, regenerate it with:

```shell
npm install
npm run css:build
```

For continuous regeneration, use `npm run css:watch`.

## Verify

```shell
./gradlew :catalog:test \
  :ssr-comparison:test \
  :hydrated-search:jsBrowserDistribution
```
