(ns weather-7.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [weather-7.ajax :refer [load-interceptors!]]
            [weather-7.handlers]
            [weather-7.subscriptions]
            [weather-7.pages.home :refer [home-page]]
            [weather-7.pages.summary :refer [summary-page]]
            [weather-7.pages.history :refer [history-page]])
  (:import goog.History))

(enable-console-print!)

(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))

(defn navbar []
  (r/with-let [collapsed? (r/atom true)]
    [:nav.navbar.navbar-dark.bg-primary
     [:button.navbar-toggler.hidden-sm-up
      {:on-click #(swap! collapsed? not)} "☰"]
     [:div.collapse.navbar-toggleable-xs
      (when-not @collapsed? {:class "in"})
      [:a.navbar-brand {:href "#/"} "weather"]
      [:ul.nav.navbar-nav
       [nav-link "#/" "home" :home collapsed?]
       [nav-link "#/summary" "summary" :summary collapsed?]
       [nav-link "#/history" "history" :history collapsed?]]]]))

; TODO create tests for this stuff

(def pages
  {:home #'home-page
   :summary #'summary-page
   :history #'history-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/summary" []
  (rf/dispatch [:set-active-page :summary]))

(secretary/defroute "/history" []
  (rf/dispatch [:set-active-page :history]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-latest! []
  (GET "/api/latest" {:handler #(rf/dispatch [:set-latest %])}))

(defn fetch-summary! []
  (GET "/api/summary" {:handler #(rf/dispatch [:set-summary %])}))

(defn fetch-history! []
  ; (prn (GET "/api/history"))
  (GET "/api/history" {:handler #(rf/dispatch [:set-history %])}))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-latest!)
  (fetch-summary!)
  (fetch-history!)
  (hook-browser-navigation!)
  (mount-components))
