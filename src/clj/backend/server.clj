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
                    [page :refer [html5 include-js]])
            [common :refer [app-container-id]]))

(defn index-page
  [_]
  (html5
   (html
    [:head
     [:title "Re-phrase"]
     [:meta {:charset "utf-8"}]]
    [:body
     [(keyword (str "div" app-container-id))]
     (include-js "cljs/main.js")])))

(defn current-time []
  (.format
   (java.time.format.DateTimeFormatter/ofPattern "HH:mm:ss")
   (java.time.LocalDateTime/now)))

(defonce connected-users (atom {}))
(let [max-id (atom 0)]
  (defonce next-id #(swap! max-id inc)))

(defn new-message [author text]
  {:id     (next-id)
   :time   (current-time)
   :author author
   :text   text})

(defonce messages (ref []))

(defn send-message [channel message]
  (send! channel (str message)))

(defn broadcast-message [message]
  (dosync (alter messages conj message))
  (doseq [channel (keys @connected-users)]
    (send-message channel message)))

(defn handle-user-input [from-channel command]
  (let [user-param (@connected-users from-channel)
        formatted-message (new-message (:name user-param) command)]
    (broadcast-message formatted-message)))

(defn user-notification [user-params notification]
  (new-message "System" (str "User '" (:name user-params) "' " notification)))

(defn add-user [channel params]
  (swap! connected-users assoc channel params)
  (broadcast-message (user-notification params "has joined")))

(defn remove-user [channel]
  (let [user-params (connected-users channel)]
    (swap! connected-users dissoc channel)
    (broadcast-message (user-notification user-params "has left"))))

(defn chat-handler [{params :params :as request}]
  (with-channel request channel
    (add-user channel params)
    (on-close channel (fn [_] (remove-user channel)))
    (on-receive channel #(handle-user-input channel %))))

(defroutes app-routes
  (GET "/" [] #'index-page)
  (GET "/chat/:name" [] #'chat-handler)
  (not-found "Not found"))

(def handler
  (routes
   (-> #'app-routes
       (wrap-resource "public")
       (wrap-params)
       (wrap-keyword-params))))

(def server-port 8080)
(defn start-server []
  (run-server #'handler {:port server-port}))
