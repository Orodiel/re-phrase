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
      [:div.title "Disconnected"]]
     [:div.bottom_wrapper.clearfix
      [:div.message_input_wrapper
       [:input.message_input
        {:placeholder "Chose your nickname"
         :value       @input
         :on-change   #(dispatch [:update path (new-input-value %)])
         :on-key-press (fn [e]
                         (let [enter-char-code 13]
                           (when (= enter-char-code (.-charCode e))
                             (dispatch [:connection-requested]))))}]]
      [:div.connect {:on-click (when (not-empty @input) #(dispatch [:connection-requested]))}
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
        :on-key-press (fn [e]
                        (let [enter-char-code 13]
                          (when (= enter-char-code (.-charCode e))
                            (dispatch [:send-message @input]))))
        :on-change #(dispatch [:update path (new-input-value %)])}]]
     [:div.load_messages {:on-click #(dispatch [:load-history 10])}
      [:div.icon]
      [:div.text "Load"]]
     [:div.send_message {:on-click #(dispatch [:send-message @input])}
      [:div.icon]
      [:div.text "Send"]]]))

(defn chat-message
  [{:keys [author text time id]} user-name]
  (let [ownership-class (if (= author user-name) "right" "left")]
    ^{:key id} [(keyword (str "div.message.appeared." ownership-class))
                [:div.avatar]
                [:div.text_wrapper
                 [:div.text (str time " " author ": " text)]]]))

(defn chat-messages
  [user-name]
  (let [messages (subscribe [:query [:chat :messages]])]
    [:ul.messages
     (map #(chat-message % user-name) (vals @messages))]))

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
       [chat-messages @user-name]
       [chat-input]])))

(defn re-phrase-app
  []
  (let [connected? (subscribe [:connected?])]
    (if-not @connected?
        [login]
        [chat])))
