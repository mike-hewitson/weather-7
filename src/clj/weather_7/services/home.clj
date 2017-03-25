(ns weather-7.services.home
  (:require ; [weather-4.layout :as layout]
            [clojure.tools.logging :as log]
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
         (assoc reading :wind-direction (get-direction (:wind-bearing reading))))
       readings))

(defn create-map-for-template
  "create the data for the web page template"
  [latest-readings]
  {:readings (filter (fn [x] (some #(= (:location x) %) locations))
                     (add-direction-into-readings (:readings latest-readings)))
   :date (:date latest-readings)})

(defn format-home-page-data []
    (create-map-for-template (first (db/get-latest))))

; TODO change template map to resemble tide list so they can be merged
; and will work for the
; TODO reduce data to service to only whats needed
; TODO create tests for this

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

; TODO merge tides info into api return

; (def bob (create-map-for-template (last (db/get-latest))))
;
; {(:location (first (:readings bob))) (first (:readings bob))}
; (def data (apply merge (map (fn [x] {(:location x) x}) (:readings bob))))
;
; (select-keys (first (:readings bob)) fields-needed)
;
; (def tides (create-next-tide-list (db/get-tides)))
;
; (merge-with merge data tides)
