(ns frontend.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
  :query
  (fn [db [_ query]]
    (get-in db query)))

(reg-sub
  :connected?
  (fn [db _]
    (get-in db [:connection :connected?])))

(reg-sub
  :first-message-id
  (fn [query_v _]
    (subscribe [:query [:chat :messages]]))
  (fn [messages _]
    (first (keys messages))))