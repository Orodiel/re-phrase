(ns frontend.ws
  (:require [haslett.client :as ws]
            [re-frame.core :refer [dispatch]]
            [cljs.core.async :refer [<! >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn open-socket-channel
  [endpoint on-open on-close]
  (go (let [socket (<! (ws/connect endpoint))]
        (on-open socket)
        (on-close (<! (:close-status socket))))))

(defn register-handler
  [{:keys [source]} handler]
  (go-loop []
           (let [input (<! source)]
             (when-not (nil? input)
               (handler input)
               (recur)))))

(defn send-message
  [{:keys [sink]} message]
  (when (some? sink)
    (go (>! sink message))))