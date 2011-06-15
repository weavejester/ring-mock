(defproject ring-mock "0.1.2-SNAPSHOT"
  :description "A library for creating mock Ring request maps"
  :dependencies [[org.clojure/clojure "1.2.0"]]
  :dev-dependencies
    [[org.clojure/clojure-contrib "1.2.0"]
     [autodoc "0.7.1" :exclusions [org.clojure/clojure
                                   org.clojure/clojure-contrib]]]
  :autodoc
    {:name "Ring-Mock"
     :description "A library for creating mock Ring request maps"
     :copyright "Copyright 2010 James Reeves"
     :root "."
     :source-path "src"
     :web-src-dir "http://github.com/weavejester/ring-mock/blob/"
     :web-home "http://weavejester.github.com/ring-mock"
     :output-path "autodoc"
     :namespaces-to-document ["ring.mock"]
     :load-except-list [#"/test/" #"project.clj"]})
