(ns frontend.db
  (:require [clojure.string :refer [replace]]))

(def default-db
  {:login      {:input ""}
   :user       {:name ""}
   :chat       {:input    ""
                :messages (sorted-map-by <)}
   :connection {:socket     nil
                :connected? false
                :url-fn     #(str "ws://localhost:8080/chat/" (:name %))}})