(defproject ring-mock "0.1.4-SNAPSHOT"
  :description "A library for creating mock Ring request maps"
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :profiles {:dev {:dependencies [[com.cemerick/clojurescript.test "0.0.1"]]}}
  :plugins [; [codox "0.6.1"]
            [lein-cljsbuild "0.3.0"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds
              [{:compiler {:output-to "target/ring-mock-debug.js"
                           :optimizations :whitespace
                           :pretty-print true}
                :source-paths ["src"]}
               {:compiler {:output-to "target/ring-mock-test.js"
                           :optimizations :whitespace
                           :pretty-print true}
                :source-paths ["test"]}
               {:compiler {:output-to "target/ring-mock.js"
                           :optimizations :advanced
                           :pretty-print false}
                :source-paths ["src"]}]
              :test-commands {"unit-tests" ["runners/phantomjs.js" "target/ring-mock-test.js"]}})
