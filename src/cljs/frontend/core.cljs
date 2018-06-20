(ns frontend.core
  (:require [reagent.core :as r]))

(def app-state (r/atom 0))

(defn content []
  [:div
   [:p "You clicked " @app-state " times"]
   [:button "Click me"
    {:on-click #(swap! app-state inc)}]])

(r/render-component [content] (.querySelector js/document "#app"))