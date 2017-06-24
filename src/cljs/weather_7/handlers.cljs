(ns weather-7.handlers
  (:require [weather-7.db      :as db]
            [re-frame.core     :refer [dispatch reg-event-db reg-event-fx]]
            [ajax.core         :as ajax]
            [ajax.edn          :as edn]
            [day8.re-frame.http-fx]))


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
    (assoc db
           :latest latest
           :show-twirly false)))


(reg-event-db
  :set-history
  (fn [db [_ history]]
    (assoc db
           :history history
           :show-twirly false)))


(reg-event-db
  :set-summary
  (fn [db [_ summary]]
    (assoc db
           :summary summary
           :show-twirly false)))


(reg-event-fx
  :get-latest                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "/api/latest"
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (edn/edn-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:set-latest]
                  :on-failure      [:bad-http-result]}}))


(reg-event-fx
  :get-history                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "/api/history"
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (edn/edn-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:set-history]
                  :on-failure      [:bad-http-result]}}))


(reg-event-fx
  :get-summary                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "/api/summary"
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (edn/edn-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:set-summary]
                  :on-failure      [:bad-http-result]}}))
