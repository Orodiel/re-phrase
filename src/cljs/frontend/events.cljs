(ns frontend.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
            [frontend.db :refer [default-db]]
            [cljs.tools.reader.edn :refer [read-string]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    default-db))

(reg-event-db
  :update
  (fn [db [_ path value]]
    (assoc-in db path value)))

(reg-event-db
  :websocket-opened
  (fn [db [_ socket]]
    (-> db
        (assoc-in [:connection :socket] socket)
        (assoc-in [:connection :connected?] true))))

(reg-event-db
  :websocket-closed
  (fn [db _]
    (-> db
        (dissoc [:connection :socket])
        (assoc-in [:connection :connected?] false))))

(reg-event-db
  :message-received
  (fn [db [_ message]]
    (update-in db [:chat :messages] #(->> message
                                          (read-string)
                                          (conj %)))))

(reg-event-fx
  :message-polling-required
  (fn [{:keys [db]} [_ handler]]
    {:websocket {:action :register-handler
                 :on-receive #(dispatch [:message-received %])
                 :socket (get-in db [:connection :socket])}}))

(reg-event-fx
  :connection-requested
  (fn [{:keys [db]} _]
    {:websocket {:action :open
                 :endpoint ((:url-fn (:connection db)) (:user db))
                 :on-open #(dispatch [:websocket-opened %])
                 :on-close #(dispatch [:websocket-closed %])}}))

(reg-event-fx
  :send-message
  (fn [_ [_ message]]
    {:websocket {:action :send
                 :message message}}))

(reg-event-fx
  :enable-re-frisk
  (fn [_ _]
    {:re-frisk {:action :enable}}))

(reg-event-fx
  :register-socket-handler
  (fn [{:keys [db]} [_ handler]]
    {:websocket {:socket (get-in db [:connection :socket])
                 :action :reg-listener
                 :on-receive handler}}))

(reg-event-fx
  :send-message
  (fn [{:keys [db]} [_ message]]
    {:websocket {:socket (get-in db [:connection :socket])
                 :action :send
                 :message message}
     :db (assoc-in db [:chat :input] "")}))