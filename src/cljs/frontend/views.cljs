(ns frontend.views
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn- new-input-value
  [input-change]
  (str (-> input-change
           .-target
           .-value)))

(defn login
  []
  (let [path [:user :name]
        input (subscribe [:query path])]
    [:div.login_window
     [:div.top_menu
      [:div.buttons
       [:div.button.minimize {:on-click #(dispatch [:enable-re-frisk])}]]
      [:div.title "Connect"]]
     [:div.bottom_wrapper.clearfix
      [:div.message_input_wrapper
       [:input.message_input
        {:placeholder "Chose your nickname"
         :value       @input
         :on-change   #(dispatch [:update path (new-input-value %)])}]]
      [:div.send_message {:on-click (when (not-empty @input) #(dispatch [:connection-requested]))}
       [:div.icon]
       [:div.text "Connect"]]]]))

(defn chat-input
  []
  (let [path [:chat :input]
        input (subscribe [:query path])]
    [:div.bottom_wrapper.clearfix
     [:div.message_input_wrapper
      [:input.message_input
       {:placeholder "Type your message here..."
        :value @input
        :on-key-press #((let [enter-char-code 13]
                          (when (= enter-char-code (.-charCode %))
                            (dispatch [:send-message @input]))))
        :on-change #(dispatch [:update path (new-input-value %)])}]]
     [:div.send_message {:on-click #(dispatch [:send-message @input])}
      [:div.icon]
      [:div.text "Send"]]]))

(defn chat-message
  [{:keys [author text time own? id]}]
  (let [ownership-class (if own? "right" "left")]
    ^{:key id} [(keyword (str "div.message.appeared." ownership-class))
                [:div.avatar]
                [:div.text_wrapper
                 [:div.text (str time " " author ": " text)]]]))

(defn chat-messages
  []
  (let [messages (subscribe [:query [:chat :messages]])]
    [:ul.messages
     (map chat-message @messages)]))

(defn chat
  []
  (dispatch [:message-polling-required])
  (let [user-name (subscribe [:query [:user :name]])]
    (fn []
      [:div.chat_window
       [:div.top_menu
        [:div.buttons
         [:div.button.minimize {:on-click #(dispatch [:enable-re-frisk])}]]
        [:div.title (str "Re-phrase (" @user-name ")")]]
       [chat-messages]
       [chat-input]])))

(defn re-phrase-app
  []
  (let [connected? (subscribe [:connected?])]
    (if-not @connected?
        [login]
        [chat])))
