(ns com.fulcrologic.fulcro.algorithms.lookup
  "Namespace with support for finding plug-in algorithms on the app. Avoids circular references
  within the library itself."
  (:require
    [taoensso.timbre :as log]))

(defn app-algorithm
  "Get the current value of a particular Fulcro plugin algorithm.  These are set by default and can be overridden
  when you create your fulcro app.

  `app` - The application
  `k` - the algorithm to obtain. This can be a plain keyword or a symbol of the algorithm desired.

  Supported algorithms that can be obtained/overridden in Fulcro (check the source of app/fulcro-app if you suspect this is out
  of date):

  - `:tx!` - Internal implementation of transaction submission. Default `app/default-tx!`
  - `:global-eql-transform` - A `(fn [tx] tx')` that is applied to all outgoing requests (when using default `tx!`).
     Defaults to stripping things like `:ui/*` and form state config joins.
  - `:remote-error?` - A `(fn [result] boolean)` that defines what a remote error is.
  - `:global-error-action` - A `(fn [env] ...)` that is run on any remote error (as defined by `remote-error?`).
  - `:optimized-render!` - The concrete render algorithm for optimized renders (not root refreshes)
  - `:render!` - The top-level render function. Calls root render or optimized render by default. Renders on the calling thread.
  - `:schedule-render!` - The call that schedules a render. Defaults to using `js/requestAnimationFrame`.
  - `:default-result-action!` -  The action used for remote results in all mutations that do not have a `result-action` section.
  - `:index-root!` - The algorithm that scans the current query from root an indexes all classes by their queries.
  - `:index-component!` - The algorithm that adds a component to indexes when it mounts.
  - `:drop-component!` - The algorithm that removes a component from indexes when it unmounts.
  - `:props-middleware` - Middleware that can modify `props` for all components.
  - `:render-middleware` - Middlware that wraps all `render` methods of `defsc` components.

  Returns nil if the algorithm is currently undefined.
  "
  [{:com.fulcrologic.fulcro.application/keys [algorithms] :as app} k]
  (when-let [nm (when (or (string? k) (keyword? k) (symbol? k))
                  (keyword "com.fulcrologic.fulcro.algorithm" (name k)))]
    (when-not (contains? algorithms nm)
      (log/warn "Attempt to access an undefined app algorithm" k))
    (get-in app [:com.fulcrologic.fulcro.application/algorithms nm] nil)))
