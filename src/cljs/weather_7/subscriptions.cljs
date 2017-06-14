(ns weather-7.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))


(reg-sub
  :page
  (fn [db _]
    (:page db)))


(reg-sub
  :latest
  (fn [db _]
    (:latest db)))


(reg-sub
  :summary
  (fn [db _]
    (:summary db)))


(reg-sub
 :history
 (fn [db _]
   (:history db)))


(reg-sub
 :show-twirly
 (fn [db _]
   (:show-twirly db)))
