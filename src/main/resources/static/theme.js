(function () {
    "use strict";

    var storageKey = "klibs-lab-theme";
    var mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");

    function readSavedTheme() {
        try {
            var saved = window.localStorage.getItem(storageKey);
            return saved === "light" || saved === "dark" ? saved : null;
        } catch (_) {
            return null;
        }
    }

    function preferredTheme() {
        return readSavedTheme() || (mediaQuery.matches ? "dark" : "light");
    }

    function updateControls(theme) {
        var nextTheme = theme === "dark" ? "light" : "dark";
        var nextLabel = nextTheme === "dark" ? "Dark mode" : "Light mode";

        document.querySelectorAll("[data-theme-toggle]").forEach(function (toggle) {
            toggle.setAttribute("aria-label", "Switch to " + nextTheme + " mode");
            toggle.setAttribute("title", "Switch to " + nextTheme + " mode");

            var icon = toggle.querySelector("[data-theme-toggle-icon]");
            var label = toggle.querySelector("[data-theme-toggle-label]");
            if (icon) icon.textContent = nextTheme === "dark" ? "\u263e" : "\u2600";
            if (label) label.textContent = nextLabel;
        });
    }

    function applyTheme(theme, persist) {
        document.documentElement.dataset.theme = theme;
        if (persist) {
            try {
                window.localStorage.setItem(storageKey, theme);
            } catch (_) {
                // The visual toggle still works when storage is unavailable.
            }
        }
        updateControls(theme);
    }

    applyTheme(preferredTheme(), false);

    document.addEventListener("DOMContentLoaded", function () {
        updateControls(document.documentElement.dataset.theme);
        document.querySelectorAll("[data-theme-toggle]").forEach(function (toggle) {
            toggle.addEventListener("click", function () {
                var current = document.documentElement.dataset.theme;
                applyTheme(current === "dark" ? "light" : "dark", true);
            });
        });
    });

    mediaQuery.addEventListener("change", function (event) {
        if (!readSavedTheme()) applyTheme(event.matches ? "dark" : "light", false);
    });
})();
