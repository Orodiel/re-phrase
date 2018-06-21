(ns backend.routes
  (:require [compojure.core :refer [GET POST defroutes routes]]
            [compojure.route :refer [not-found]]
            [backend.views :refer [index-page]]))

(defroutes static-routes
  (GET "/" [] #'index-page)
  (not-found "Not found"))