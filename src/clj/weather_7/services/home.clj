(ns weather-7.services.home
  (:require [clojure.tools.logging :as log]
            [clojure.math.numeric-tower :as m]
            [weather-7.db.core :as db]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(def fields-needed
  [:sunset
   :day-summary
   :wind-speed
   :sunrise
   :icon
   :wind-bearing
   :wind-direction
   :temperature-max
   :location
   :temperature
   :week-summary])

(def locations
  ["Sandton" "Paradise Beach"])

(def wind-directions
  ["N" "NE" "E" "SE" "S" "SW" "W" "NW"])

(defn get-direction
  "translate wind bearing to direction in text"
  [bearing]
  (wind-directions (mod (m/round (/ bearing 45)) 8)))

(defn add-direction-into-readings
  "include the direction element into the reading"
  [readings]
  (map (fn [reading]
         (assoc (select-keys reading fields-needed) :wind-direction (get-direction (:wind-bearing reading))))
       readings))

(defn create-map-for-template
  "create the data for the web page template"
  [latest-readings]
  ; {:readings (filter (fn [x] (some #(= (:location x) %) locations)))}
  {:readings (add-direction-into-readings (:readings latest-readings))
   :date (:date latest-readings)})

(defn prepare-latest-readings
  [readings]
  (apply merge
         (map (fn [x] { (:location x) x})
              (:readings readings))))

(defn create-next-tide-list
  "Create a map with the next tide with key of location"
  [tide]
  (let [now (c/from-date (new java.util.Date))
        locations (:locations (first tide))]
   (apply merge (map (fn [x] {(:location x)
                              (some #(if (t/after?
                                          (c/from-date (c/to-date (:date %)))
                                          now)
                                         %)
                                    (:extremes (:tides x)))}) locations))))

(defn format-home-page-data []
  (let [weather-data (first (db/get-latest))
        tides-data (db/get-tides)
        reading-date (:date weather-data)]
   {:date reading-date
    :readings
     (vals
      (select-keys
       (merge-with merge
                   (prepare-latest-readings (create-map-for-template weather-data))
                   (create-next-tide-list tides-data))
       locations))}))

; TODO reduce data to service to only whats needed for tides
; TODO create tests for this

; TODO refactor this crap

; store date somewhere?
; prepare weather and tides from raw for merging
; merge
; select only locations required
; strip out values
; add wind direction (maybe at the end)
; add back date
