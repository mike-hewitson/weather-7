(ns weather-7.services.home
  (:require ; [weather-4.layout :as layout]
            [clojure.tools.logging :as log]
            [clojure.math.numeric-tower :as m]
            [weather-7.db.core :as db]))

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
  [latest-reading]
  {:readings (filter (fn [x] (some #(= (:location x) %) locations))
                     (add-direction-into-readings (:readings latest-reading)))
   :date (:date latest-reading)})

(defn format-home-page-data []
    (create-map-for-template (first (db/get-latest))))

; TODO reduce data to service to only whats needed
; TODO crate tests for this
