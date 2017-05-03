(ns weather-7.services.home
  (:require [clojure.math.numeric-tower :as m]
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
   :temperature-min
   :location
   :temperature
   :week-summary])

; TODO get locations from database

(def locations
  ["Paradise Beach" "Sandton" "London"])

(def wind-directions
  ["Northerly" "North-easterly" "Easterly" "South-easterly"
   "Southerly" "South-westerley" "Westerly" "North-westerly"])

(def moon-icons-transform
  ["wi-moon-alt-waxing-crescent-1"
   "wi-moon-alt-waxing-crescent-2"
   "wi-moon-alt-waxing-crescent-3"
   "wi-moon-alt-waxing-crescent-4"
   "wi-moon-alt-waxing-crescent-5"
   "wi-moon-alt-waxing-crescent-6"
   "wi-moon-alt-first-quarter"
   "wi-moon-alt-waxing-gibbous-1"
   "wi-moon-alt-waxing-gibbous-2"
   "wi-moon-alt-waxing-gibbous-3"
   "wi-moon-alt-waxing-gibbous-4"
   "wi-moon-alt-waxing-gibbous-5"
   "wi-moon-alt-waxing-gibbous-6"
   "wi-moon-alt-full"
   "wi-moon-alt-waning-gibbous-1"
   "wi-moon-alt-waning-gibbous-2"
   "wi-moon-alt-waning-gibbous-3"
   "wi-moon-alt-waning-gibbous-4"
   "wi-moon-alt-waning-gibbous-5"
   "wi-moon-alt-waning-gibbous-6"
   "wi-moon-alt-first-quarter"
   "wi-moon-alt-waning-crescent-1"
   "wi-moon-alt-waning-crescent-2"
   "wi-moon-alt-waning-crescent-3"
   "wi-moon-alt-waning-crescent-4"
   "wi-moon-alt-waning-crescent-5"
   "wi-moon-alt-waning-crescent-6"
   "wi-moon-alt-new"])

(defn get-direction
  "translate wind bearing to direction in text"
  [bearing]
  (wind-directions (mod (m/round (/ bearing 45)) 8)))

(defn format-readings-for-merge
  "create map of selected reading data for merge"
  [readings]
  (apply merge
         (map (fn [x] {(:location x) (select-keys x fields-needed)})
              (:readings readings))))

(defn create-directions-for-merge
  "create wind directions for merging"
  [readings]
  (apply merge
         (map (fn [reading]
                {(:location reading) {:wind-direction
                                      (get-direction
                                       (:wind-bearing reading))}})
              (:readings readings))))

(defn create-tide-for-merge
  "Create a map with the next tide with key of location"
  [tide]
  (let [now (c/from-date (new java.util.Date))
        locations (:locations tide)]
    (apply merge (map (fn [x] {(:location x)
                               (some #(if (t/after?
                                           (c/from-date (c/to-date (:date %)))
                                           now)
                                        %)
                                     (:extremes (:tides x)))})
                      locations))))

; TODO change tides function name to be consistent
; TODO create test for moon phases
; TODO check the the icon is in line with the numeric phase

(defn create-moonphase-for-merge
  "strip out and transform age of moon to icon, age and phase"
  [moon-data]
  (let [locations (:locations moon-data)]
    (apply merge (map (fn [x]
                        (let [moon-phase (:moon_phase (:phases x))]
                          {(:location x)
                           {:moon-phase-icon
                            (-> moon-phase
                                :ageOfMoon
                                Integer/parseInt
                                dec
                                dec
                                moon-icons-transform)
                            :age-of-moon
                            (-> moon-phase
                                :ageOfMoon
                                Integer/parseInt
                                dec
                                dec)
                            :phase-of-moon
                            (:phaseofMoon moon-phase)}}))
                      locations))))

(defn prepare-home-page-data
  "bring together all of the home page data components"
  []
  (let [weather-data (first (db/get-latest))
        tides-data (first (db/get-tides))
        moon-data (first (db/get-moonphases))
        reading-date (:date weather-data)]
    {:date reading-date
     :readings
     (vals
      (select-keys
       (merge-with merge
                   (format-readings-for-merge weather-data)
                   (create-directions-for-merge weather-data)
                   (create-tide-for-merge tides-data)
                   (create-moonphase-for-merge moon-data))
       locations))}))
