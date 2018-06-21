(ns backend.handlers
  (:require [backend.routes :refer [static-routes]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [routes]]))

(def handler
  (routes
    (-> #'static-routes
        (wrap-resource "public")
        (wrap-defaults site-defaults)
        (wrap-reload))))
