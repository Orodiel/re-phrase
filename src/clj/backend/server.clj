(ns backend.server
  (:use [backend.routes :refer [app-routes]])
  (:require [org.httpkit.server :as http-kit]
            [compojure.core :refer [routes]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]))

(def handler
  (routes
   (-> #'app-routes
       (wrap-resource "public")
       (wrap-defaults site-defaults)
       (wrap-reload))))

(def server-port 8080)
(defn start-server []
  (http-kit/run-server #'handler {:port server-port}))

(def server-state (atom {}))

