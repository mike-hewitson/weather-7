(ns weather-7.services.history
  (:require [weather-7.db.core :as db]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clj-time.periodic :as p]))

; TODO adjust readings resolution when graphs are visual
; TODO make resolution a parameter
; TODO days back to be a parameter on the from-end (default 7)

(def locations
  ["Sandton" "Paradise Beach" "London"])

(def fields-needed
  [:location
   :wind-speed
   :temperature])

(def points-to-plot 200)

(defn create-history-seq
  "create a sequence of 50 dates between a date and today"
  [days-back end-point]
  (let [interval (int (/ (* days-back 24  3600) (dec points-to-plot)))
        from-date (t/minus end-point (t/days days-back))]
    (map c/to-date
         (take points-to-plot (p/periodic-seq from-date
                                              (t/seconds interval))))))

(defn create-readings-list
  "create a list of readings, on for each date "
  [dates]
  (map #(first (db/get-reading-at-time %))
       dates))

(defn create-display-list
  "format the a reeading list for the html template "
  [readings-list]
  (map (fn [x]
         (let [reading (first (:readings x))]
           {:date (:date x)
            :location (:location reading)}))
       readings-list))

(defn extract-fields-for-one-reading
  [reading]
  (let [date (:date reading)]
    (map (fn [x] (merge {:date date} (select-keys x fields-needed)))
         (:readings reading))))

(defn rebuild-data-per-location
  [all-data]
  (map (fn [x] {:location x
                :history (filter #(= (:location %) x)
                                 all-data)})
       locations))

(defn prepare-history-data
  "bring together all of the home page data components"
  []
  (->> (create-history-seq 7 (t/now))
       create-readings-list
       dedupe
       (map extract-fields-for-one-reading)
       flatten
       rebuild-data-per-location))

; TODO refactor code
; TODO create tests
