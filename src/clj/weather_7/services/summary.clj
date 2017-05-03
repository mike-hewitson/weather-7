(ns weather-7.services.summary
  (:require [weather-7.db.core :as db]))

; TODO get locations from database

(def locations
  ["Sandton" "Paradise Beach" "London"])

(defn build-for-one-location [data]
  (map (fn [x] (dissoc (merge (:_id x) x) :_id)) data))

(defn prepare-summary-data []
  (map (fn [x] {:location x
                :summary (build-for-one-location (db/get-summary x))})
       locations))

; TODO add tests
