(ns frontend.effects
  (:require [re-frame.core :refer [reg-fx dispatch]]
            [re-frisk.core :refer [enable-re-frisk!]]
            [frontend.ws :as ws]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(reg-fx
  :websocket
  (fn [{:keys [action endpoint on-open on-receive on-close socket message]}]
    (let [handler (case action
                    :open #(ws/open-socket-channel endpoint on-open on-close)
                    :register-handler #(ws/register-handler socket on-receive)
                    :send #(ws/send-message socket message))]
      (handler))))

(reg-fx
  :re-frisk
  (fn []
    (enable-re-frisk! {:x 0 :y 0})))