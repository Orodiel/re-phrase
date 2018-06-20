(ns backend.routes
  (:use [backend.views :refer [index-page]])
  (:require [compojure.core :refer [GET POST defroutes routes]]
            [compojure.route :refer [not-found]]))

(defroutes app-routes
  (GET "/" [] #'index-page)
  (not-found "Not found"))

