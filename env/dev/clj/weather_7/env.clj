(ns weather-7.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [weather-7.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[weather-7 started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[weather-7 has shut down successfully]=-"))
   :middleware wrap-dev})
