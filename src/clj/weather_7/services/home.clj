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
  ["Northerly" "North-easterly" "Easterly" "South-easterly" "Southerly" "South-westerley" "Westerly" "North-westerly"])

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
                                    (:extremes (:tides x)))}) locations))))

(defn prepare-home-page-data []
  "bring together all of the home page data components"
  (let [weather-data (first (db/get-latest))
        tides-data (first (db/get-tides))
        reading-date (:date weather-data)
        now (c/from-date (new java.util.Date))]
   {:date reading-date
    :readings
     (vals
      (select-keys
       (merge-with merge
                   (format-readings-for-merge weather-data)
                   (create-directions-for-merge weather-data)
                   (create-next-tide-list tides-data))
       locations))}))
