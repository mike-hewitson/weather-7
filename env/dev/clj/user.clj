(ns user
  (:require [mount.core :as mount]
            [weather-7.figwheel :refer [start-fw stop-fw cljs]]
            weather-7.core))

(defn start []
  (mount/start-without #'weather-7.core/http-server
                       #'weather-7.core/repl-server))

(defn stop []
  (mount/stop-except #'weather-7.core/http-server
                     #'weather-7.core/repl-server))

(defn restart []
  (stop)
  (start))
