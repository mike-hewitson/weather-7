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
  ["Paradise Beach" "Sandton"])

(def wind-directions
  ["Northerly" "North-easterly" "Easterly" "South-easterly" "Southerly" "South-westerley" "Westerly" "North-westerly"])

(def moon-icons-transform
  ["wi-moon-new"
   "wi-moon-waxing-crescent-1"
   "wi-moon-waxing-crescent-2"
   "wi-moon-waxing-crescent-3"
   "wi-moon-waxing-crescent-4"
   "wi-moon-waxing-crescent-5"
   "wi-moon-waxing-crescent-6"
   "wi-moon-first-quarter"
   "wi-moon-waxing-gibbous-1"
   "wi-moon-waxing-gibbous-2"
   "wi-moon-waxing-gibbous-3"
   "wi-moon-waxing-gibbous-4"
   "wi-moon-waxing-gibbous-5"
   "wi-moon-waxing-gibbous-6"
   "wi-moon-full"
   "wi-moon-waning-gibbous-1"
   "wi-moon-waning-gibbous-2"
   "wi-moon-waning-gibbous-3"
   "wi-moon-waning-gibbous-4"
   "wi-moon-waning-gibbous-5"
   "wi-moon-waning-gibbous-6"
   "wi-moon-first-quarter"
   "wi-moon-waning-crescent-1"
   "wi-moon-waning-crescent-2"
   "wi-moon-waning-crescent-3"
   "wi-moon-waning-crescent-4"
   "wi-moon-waning-crescent-5"
   "wi-moon-waning-crescent-6"])


(defn get-direction
  "translate wind bearing to direction in text"
  [bearing]
  (wind-directions (mod (m/round (/ bearing 45)) 8)))

(defn format-readings-for-merge
  "create map of selected reading data for merge"
  [readings]
  (apply merge
         (map (fn [x] { (:location x) (select-keys x fields-needed)})
              (:readings readings))))

(defn create-directions-for-merge
  "create wind directions for merging"
  [readings]
  (apply merge
    (map (fn [reading]
             { (:location reading) {:wind-direction (get-direction (:wind-bearing reading))}})
         (:readings readings))))

(defn create-next-tide-list
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

(defn create-moonphase-for-merge
  "strip out and transform age of moon to icon"
  [moon-data]
  (let [locations (:locations moon-data)]
   (apply merge (map (fn [x] {(:location x)
                              {:moon-phase-icon
                               (-> x
                                   :phases
                                   :moon_phase
                                   :ageOfMoon
                                   Integer/parseInt
                                   moon-icons-transform)}})
                     locations))))

(defn prepare-home-page-data []
  "bring together all of the home page data components"
  (let [weather-data (first (db/get-latest))
        tides-data (first (db/get-tides))
        moon-data (first (db/get-moonphases))
        reading-date (:date weather-data)
        now (c/from-date (new java.util.Date))]
   {:date reading-date
    :readings
     (vals
      (select-keys
       (merge-with merge
                   (format-readings-for-merge weather-data)
                   (create-directions-for-merge weather-data)
                   (create-next-tide-list tides-data)
                   (create-moonphase-for-merge moon-data))
       locations))}))
