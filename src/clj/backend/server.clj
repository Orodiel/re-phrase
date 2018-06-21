(ns backend.server
  (:require [org.httpkit.server :as http-kit]
            [compojure.core :refer [routes]]
            [backend.routes :refer [static-routes]]
            [backend.handlers :refer [handler]]))

(def server-port 8080)
(defn start-server []
  (http-kit/run-server #'handler {:port server-port}))

