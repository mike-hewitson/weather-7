(ns weather-7.app
  (:require [weather-7.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
