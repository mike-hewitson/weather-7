(ns weather-7.db.core
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.query :as mq]
            [mount.core :refer [defstate]]
            [weather-7.config :refer [env]]
            [clojure.tools.logging :as log]))

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

; TODO remove old code
; (defn create-user [user]
;   (mc/insert db "users" user))
;
; (defn update-user [id first-name last-name email]
;   (mc/update db "users" {:_id id}
;              {$set {:first_name first-name
;                     :last_name last-name
;                     :email email}}))
;
; (defn get-user [id]
;   (mc/find-one-as-map db "users" {:_id id}))

(defn get-latest []
  "return the most recent reading"
  (mq/with-collection db "readings"
    (mq/find {})
    (mq/sort (sorted-map $natural -1))
    (mq/limit 1)))

(defn get-tides []
  "return the most recent set of tide data"
  (mq/with-collection db "tides"
    (mq/find {})
    (mq/sort (sorted-map $natural -1))
    (mq/limit 1)))

(defn get-reading-at-time [date-time]
  "return the reading just before to the supplied date/time"
  (log/debug "date-time:" date-time)
  (mq/with-collection db "readings"
    (mq/find {:date {$lte date-time}})
    (mq/sort (array-map :date -1))
    (mq/limit 1)))

; TODO put filter in place for the date range

(defn get-summary [location]
  "retrieve summary data for all days that we have data for"
  (mc/aggregate
   db
   "readings"
   [{$unwind "$readings"}
    {$match {"readings.location" {"$eq" location}}}
    {$project {"readings" 1
               :yearMonthDay {"$dateToString" {:format "%Y-%m-%d"
                                               :date "$date"}}}}
    {$group {"_id" {"date" "$yearMonthDay"}
             :count {"$sum" 1}
             :avg-temp {"$avg" "$readings.temperature"}
             :max-temp {"$max" "$readings.temperature"}
             :min-temp {"$min" "$readings.temperature"}
             :avg-wind {"$avg" "$readings.wind-speed"}
             :max-wind {"$max" "$readings.wind-speed"}
             :min-wind {"$min" "$readings.wind-speed"}}}
    {$sort {"_id.date" 1}}]))

(defn get-moonphases []
  "return the most recent moon phase data"
  (mq/with-collection db "moon"
    (mq/find {})
    (mq/sort (sorted-map $natural -1))
    (mq/limit 1)))
