(defproject re-phrase "0.1.0-SNAPSHOT"
  :description "Simple Clojure(Script) chat"
  :url "https://github.com/Orodiel/re-phrase"
  :scm {:name "git"
        :url  "https://www.github.com/cloverage/cloverage"
        :tag  "HEAD"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha3"]
                 [org.clojure/clojurescript "1.9.36"]
                 [com.cognitect/transit-clj "0.8.285"]

                 [http-kit "2.2.0-alpha1"]
                 [hiccup "1.0.5"]
                 [compojure "1.5.0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [org.clojure/tools.nrepl "0.2.11"]

                 [reagent "0.6.0"]

                 [im.chit/vinyasa "0.2.0"]
                 ]
  :min-lein-version "2.8.1"
  :source-paths ["src/clj"]
  :main backend.core
  :plugins [[lein-cljsbuild "1.1.3"]]
  :clean-targets ^{:protect false} [:target-path "resources/public/cljs"]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel true
                        :compiler {:main frontend.core
                                   :asset-path "cljs/out"
                                   :output-to  "resources/public/cljs/main.js"
                                   :output-dir "resources/public/cljs/out"}}
                       {:id "min"
                        :jar true
                        :source-paths ["src/cljs"]
                        :compiler {:main frontend.core
                                   :output-to "resources/public/cljs/main.js"
                                   :optimizations :advanced
                                   :closure-defines {goog.DEBUG false}
                                   :pretty-print false}}]
              }
  :figwheel {:server-port 5309}
  :profiles {:uberjar {:aot :all
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]}
             :dev {:plugins [[lein-figwheel "0.5.3"]
                             [lein-cljfmt "0.5.7"]
                             [jonase/eastwood "0.2.6"]
                             [lein-kibit "0.1.6"]]}}
  :aliases {"style" ["do" ["cljfmt" "fix"] ["eastwood"] ["kibit"]]}
  )

