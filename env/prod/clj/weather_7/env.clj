(ns weather-7.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[weather-7 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[weather-7 has shut down successfully]=-"))
   :middleware identity})
