(defproject ring-mock "0.1.3"
  :description "A library for creating mock Ring request maps"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [ring/ring-codec "1.0.0"]]
  :plugins [[codox "0.6.4"]]
  :profiles {
    :1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
    :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
    :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}})
