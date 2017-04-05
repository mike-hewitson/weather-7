(ns weather-7.handlers
  (:require [weather-7.db :as db]
            [re-frame.core :refer [dispatch reg-event-db]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-latest
  (fn [db [_ latest]]
    (assoc db :latest latest)))

(reg-event-db
  :set-summary
  (fn [db [_ summary]]
    (assoc db :summary summary)))
