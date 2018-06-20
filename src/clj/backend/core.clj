(ns backend.core
  (:gen-class)
  (:use [backend.server :refer [start-server server-port]])
  (:require [clojure.tools.nrepl.server :as nrepl]))

(def repl-port 5301)

(defn -main []
  (start-server)
  (println (str "Server started: http://localhost:" server-port))
  (nrepl/start-server :bind "0.0.0.0" :port repl-port)
  (println (str "nREPL server started: 'lein repl :connect " repl-port "'")))