(ns weather-7.services.history
  (:require [clojure.tools.logging :as log]
            [clojure.math.numeric-tower :as m]
            [weather-7.db.core :as db]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clj-time.periodic :as p]))

; TODO adjust readings resolution when graphs are visual
; TODO make resolution a parameter
; TODO currently set for one day back. change to 14 as a deault
; TODO days back to be a parameter on the from-end

(def locations
  ["Sandton" "Paradise Beach"])

(def fields-needed
  [:location
   :wind-speed
   :temperature])

(defn create-history-seq
  "create a sequence of 50 dates between a date and today"
  [days-back end-point]
  (let [interval (int (/ (* days-back 24  3600) 49))
        from-date (t/minus end-point (t/days days-back))]
   (map c/to-date (take 50 (p/periodic-seq from-date (t/seconds interval))))))

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

(defn history-page []
    {:readings (->
                (create-history-seq 1 (t/now))
                create-readings-list
                create-display-list
                dedupe)})

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

(defn prepare-history-data []
  "bring together all of the home page data components"
  (->> (create-history-seq 1 (t/now))
       create-readings-list
       (map extract-fields-for-one-reading)
       flatten
       rebuild-data-per-location))

; TODO remove sandton from detail
; TODO refactor code
; TODO create tests
