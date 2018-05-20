(defproject weather-7 "0.1.0-SNAPSHOT"

  :description "weather website to show minimum of relevant weather information"
  :url "https://aqueous-sierra-27664.herokuapp.com/"

  :dependencies [[cljs-ajax "0.7.3"]
                 [compojure "1.6.1"]
                 [cprop "0.1.11"]
                 [funcool/struct "1.2.0"]
                 [luminus-immutant "0.2.4"]
                 [luminus-nrepl "0.1.4"]
                 [markdown-clj "1.0.2"]
                 [metosin/compojure-api "1.1.12"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.12"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.495" :scope "provided"]
                 [org.clojure/tools.cli "0.3.7"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.webjars.bower/tether "1.4.3"]
                 [org.webjars/bootstrap "4.1.0"]
                 [org.webjars/font-awesome "5.0.13"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [re-frame "0.10.5"]
                 [reagent "0.8.1"]
                 [reagent-utils "0.3.1"]
                 [ring-middleware-format "0.7.2"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [secretary "1.2.3"]
                 [selmer "1.11.7"]
                 [clj-http "3.9.0"]
                 [clj-time "0.14.4"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [com.novemberain/monger "3.1.0"]
                 [day8.re-frame/http-fx "0.1.6"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot weather-7.core

  :plugins [[lein-cprop "1.0.1"]
            [lein-cljsbuild "1.1.5"]
            [lein-immutant "2.1.0"]
            [lein-kibit "0.1.2"]
            [lein-ancient "0.6.10"]
            [lein-midje "3.1.3"]
            [lein-cloverage "1.0.9"]
            [jonase/eastwood "0.2.3"]]
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-to "target/cljsbuild/public/js/app.js"
                 :optimizations :advanced
                 :pretty-print false
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["externs.js"]}}}}
             :aot :all
             :uberjar-name "weather-7.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.6.0"]
                                 [ring/ring-mock "0.3.2"]
                                 [ring/ring-devel "1.6.3"]
                                 [pjstadig/humane-test-output "0.8.3"]
                                 [binaryage/devtools "0.9.10"]
                                 [com.cemerick/piggieback "0.2.2"]
                                 [doo "0.1.10"]
                                 [figwheel-sidecar "0.5.16"]
                                 [midje "1.9.1"]
                                 [proto-repl "0.3.1"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.18.1"]
                                 [lein-doo "0.1.7"]
                                 [lein-figwheel "0.5.9"]
                                 [org.clojure/clojurescript "1.9.495"]
                                 [lein-bikeshed "0.2.0"]
                                 [lein-cljfmt "0.5.6"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :compiler
                     {:main "weather-7.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true}}}}
                  :doo {:build "test"}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "weather-7.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}}

   :profiles/dev {}
   :profiles/test {}}
  :cljfmt {:file-pattern #"[^\.#]*\.clj[s]?$"}
  :eastwood {:exclude-linters [:constant-test]
             :add-linters [:unused-fn-args :unused-locals :unused-namespaces
                           :unused-private-vars]
             :namespaces [:source-paths :test-paths]
             :exclude-namespaces [weather-7.routes.home
                                  weather-7.routes.services]})
