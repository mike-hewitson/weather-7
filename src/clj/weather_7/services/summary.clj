(ns weather-7.services.summary
  (:require [weather-7.db.core :as db]))

(def locations
  ["Sandton" "Paradise Beach"])

(defn build-for-one-location [data]
  (map (fn [x] (dissoc (merge (:_id x) x) :_id)) data))

(defn prepare-summary-data []
  (map (fn [x] {:location x
                :summary (build-for-one-location (db/get-summary x))})
       locations))

; TODO add tests
