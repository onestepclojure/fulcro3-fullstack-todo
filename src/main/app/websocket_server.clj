(ns app.websocket-server)

(comment
  (ns fulcro_todomvc.websocket-server
    (:require
      [fulcro_todomvc.server :as plain-server]
      [clojure.core.async :as async]
      [com.fulcrologic.fulcro.networking.server-middleware :refer [not-found-handler]]
      [com.fulcrologic.fulcro.networking.websockets :as fws]
      [immutant.web :as web]
      [ring.middleware.content-type :refer [wrap-content-type]]
      [ring.middleware.not-modified :refer [wrap-not-modified]]
      [ring.middleware.resource :refer [wrap-resource]]
      [ring.middleware.params :refer [wrap-params]]
      [ring.middleware.keyword-params :refer [wrap-keyword-params]]
      [ring.util.response :refer [response file-response resource-response]]
      [taoensso.sente.server-adapters.immutant :refer [get-sch-adapter]]))

  (def server (atom nil))

  (defn http-server []
    (let [websockets (fws/start! (fws/make-websockets
                                   (fn [query] (async/<!! (plain-server/parser {} query)))
                                   {:http-server-adapter (get-sch-adapter)
                                    :sente-options       {:csrf-token-fn nil}}))
          middleware (-> not-found-handler
                       (fws/wrap-api websockets)
                       wrap-keyword-params
                       wrap-params
                       (wrap-resource "public")
                       wrap-content-type
                       wrap-not-modified)
          result     (web/run middleware {:host "0.0.0.0"
                                          :port 3000})]
      (reset! server
        (fn []
          (fws/stop! websockets)
          (web/stop result)))))

  (comment

    ;; start
    (http-server)

    ;; stop
    (@server)

    ;; look at server "db"
    @item-db

    ))
