(ns backend.views
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]))

(defn index-page
  [_]
  (html5
   (html
    [:head
     [:title "Re-phrase"]
     [:meta {:charset "utf-8"}]]
    [:body
     [:div#app]
     (hiccup.page/include-js "cljs/main.js")])))