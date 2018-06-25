(ns backend.server
  (:require [org.httpkit.server :refer [with-channel send! on-close on-receive run-server]]
            (compojure [core :refer [GET POST defroutes routes]]
                       [route :refer [not-found]]
                       [handler :refer [site]])
            (ring.middleware [defaults :refer [wrap-defaults site-defaults api-defaults]]
                             [resource :refer [wrap-resource]]
                             [reload :refer [wrap-reload]]
                             [keyword-params :refer [wrap-keyword-params]]
                             [params :refer [wrap-params]])
            (hiccup [core :refer [html]]
                    [page :refer [html5 include-js include-css]])
            [environ.core :refer [env]]))

(defn current-time []
  (.format
   (java.time.format.DateTimeFormatter/ofPattern "HH:mm:ss")
   (java.time.LocalDateTime/now)))

(defonce connected-users (atom {}))
(defonce messages (ref []))

(let [max-id (atom 0)]
  (defonce next-id #(swap! max-id inc)))

(defn new-message
  ([author text owner]
   {:id     (next-id)
    :time   (current-time)
    :author author
    :text   text
    :owner    owner}))

(defn send-message [channel message]
  (let [own-message (= (:owner message) channel)
        result-message (-> message
                           (assoc :own? own-message)
                           (dissoc :owner)
                           (str))]
    (send! channel result-message)))

(defn broadcast-message [message]
  (dosync (alter messages conj message))
  (doseq [channel (keys @connected-users)]
    (send-message channel message)))

(defn handle-send [{:keys [name]} channel {:keys [text]}]
  (let [message (new-message name text channel)]
    (broadcast-message message)))

(defn select-messages [from to]
  (filter #(<= from (:id %) to) @messages))

(defn handle-load-history [channel {:keys [since count]}]
  (let [selected-messages (select-messages (- since count) since)]
    (println "sm: " selected-messages)
    (doseq [message selected-messages]
      (send-message channel message))))

(defn handle-user-input [channel edn-command]
  (println "! " edn-command)
  (let [user-params (@connected-users channel)
        {:keys [request] :as command} (clojure.edn/read-string edn-command)]
    (when (= :send request)
      (handle-send user-params channel command))
    (when (= :history request)
      (handle-load-history channel command))))

(defn user-notification [user-params notification]
  (new-message "System" (str "User '" (:name user-params) "' " notification) nil))

(defn add-user [channel params]
  (swap! connected-users assoc channel params)
  (broadcast-message (user-notification params "has joined")))

(defn remove-user [channel]
  (let [user-params (@connected-users channel)]
    (swap! connected-users dissoc channel)
    (broadcast-message (user-notification user-params "has left"))))

(defn chat-handler [{params :params :as request}]
  (with-channel request channel
    (add-user channel params)
    (on-close channel (fn [_] (remove-user channel)))
    (on-receive channel #(handle-user-input channel %))))

(defn wrap-dir-index [handler-fn]
  (fn [request]
    (handler-fn
     (update-in request [:uri]
                #(if (= "/" %)
                   "/index.html"
                   %)))))

(defroutes app-routes
  (GET "/chat/:name" [] #'chat-handler)
  (not-found "Not found"))

(def handler
  (routes
   (-> #'app-routes
       (wrap-resource "public")
       (wrap-params)
       (wrap-dir-index))))

(def server-port (Integer. (or (env :port) 8080)))
(defn start-server []
  (run-server #'handler {:port server-port}))
