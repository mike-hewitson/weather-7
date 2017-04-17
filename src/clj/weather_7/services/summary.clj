(ns weather-7.services.summary
  (:require [clojure.tools.logging :as log]
            [clojure.math.numeric-tower :as m]
            [weather-7.db.core :as db]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(def locations
  ["Sandton" "Paradise Beach"])

(defn build-for-one-location [location data]
  (map (fn [x] (dissoc (merge (:_id x) x) :_id)) data))

(defn prepare-summary-data []
  (map (fn [x] {:location x
                :summary (build-for-one-location x (db/get-summary x))})
       locations))

; TODO add tests
