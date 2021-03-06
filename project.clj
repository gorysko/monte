(defproject monte "0.0.0-SNAPSHOT"
  :description "Monte is a software visualisation tool created as 
            	a part of Andrew Sernyak master degree' thesis"
  :url "https://github.com/ndrew/monte"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/core.logic "0.7.5"]
                 
                 [org.clojure/data.json "0.2.2"]
                 
                 [datalog               "0.1.1"]
                 [fs                    "1.2.0"]
                 [compojure             "1.1.3"]
                 [lib-noir              "0.3.3"]
                 [org.clojure/tools.cli "0.2.2"]
                 [crate                 "0.2.4"]
                 ; avaiting better times
                 ;[prismatic/dommy       "0.0.2"]
                 [jayq                  "2.0.0"]
                 [shoreleave            "0.2.2"]
                 [com.cemerick/shoreleave-remote-ring "0.0.2" ] ; 0.0.3
                 [myguidingstar/clansi "1.3.0"]
                 [sandbar/sandbar "0.4.0-SNAPSHOT"]
                 [cheshire "5.1.1"]
                 
                 [org.clojure/tools.namespace "0.2.3"]
                 [criterium "0.4.0"]
                 
                 [org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 ]
  :plugins [[lein-cljsbuild   "0.2.10"]
            [lein-ring        "0.7.1"]]
  :hooks [leiningen.cljsbuild]
  
  :cljsbuild {
    :builds [{
        :incremental false
        :jar true
		    :source-path "src-cljs"
        :compiler {
            :output-to "resources/public/js/js.js"
            :optimizations :whitespace
            :externs ["resources/public/extern/raphael-min.js"
                      "resources/public/extern/dracula_graph.js"
                      "resources/public/extern/dracula_graffle.js"
                      "resources/public/extern/dracula_algorithms.js"]
            :pretty-print true}
    }]}

  :repl-options { 
    :init-ns monte.launcher 
    :init (do
      ;; <!> add some hacks later
    )}

  :ring         {:handler monte.backend.server/monte-routing
                 :auto-reload? true
                 :auto-refresh true}

  :main monte.launcher
  :javac-options     ["-target" "1.6" "-source" "1.6"]
  :jvm-opts ["-Xms1024m", "-Xmx2048m", 
             "-XX:-UseGCOverheadLimit", "-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode",
             "-XX:+DoEscapeAnalysis", "-XX:+UseBiasedLocking",
             "-XX:PermSize=64M", "-XX:MaxPermSize=256M"]
  
  :profiles {
    :dev { :main ^{:skip-aot true} monte.launcher}
    :deploy {:main ^{:skip-aot false} monte.launcher :aot [monte.launcher]}}
)