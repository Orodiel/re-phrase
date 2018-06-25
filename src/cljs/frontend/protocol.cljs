(ns frontend.protocol)

(defn load-messages-request
  [latest-id count]
  {:request :history
   :since latest-id
   :count count})

(defn send-message-request
  [message]
  {:request :send
   :text message})