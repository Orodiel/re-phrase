(ns frontend.core
  (:require [frontend.events]
            [frontend.subs]
            [frontend.effects]
            [frontend.views :refer [re-phrase-app]]
            [re-frame.core :refer [dispatch]]
            [reagent.core :as r]
            [re-frisk.core :refer [enable-re-frisk!]]))

(enable-console-print!)
(defonce initialize-db (dispatch [:initialize-db]))

(defn ^:export main
  []
  (r/render-component [re-phrase-app]
                      (.querySelector js/document "#app")))

(main)
