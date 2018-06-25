(ns frontend.db)

(def default-db
  {:login      {:input ""}
   :user       {:name ""}
   :chat       {:input    ""
                :messages []}
   :connection {:socket     nil
                :connected? false
                :url-fn     #(str "ws://localhost:8080/chat/" (:name %))}})
;(.-localion (js/document))
