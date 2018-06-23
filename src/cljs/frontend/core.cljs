(ns frontend.core
  (:require [reagent.core :as r]
            [common :refer [app-container-id]]))

(def app-state (r/atom 0))

(defn content []
  [:div
   [:p "You clicked " @app-state " times"]
   [:button {:on-click #(swap! app-state inc)}
    "Click me"]])

(r/render-component [content] (.querySelector js/document app-container-id))