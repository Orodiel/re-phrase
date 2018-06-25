(ns frontend.db
  (:require [clojure.string :refer [replace]]))

(def default-db
  {:login      {:input ""}
   :user       {:name ""}
   :chat       {:input    ""
                :messages []}
   :connection {:socket     nil
                :connected? false
                :url-fn     #(-> (.-location js/document)
                                 (str "chat/" (:name %))
                                 (replace "http" "ws"))}})