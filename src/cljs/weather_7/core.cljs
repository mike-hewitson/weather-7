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
            [goog.string :as gstring]
            [goog.string.format]
            [cljs-time.core :as t]
            [cljs-time.format :as tf])
  (:import goog.History))

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
      [:a.navbar-brand {:href "#/"} "weather-7"]
      [:ul.nav.navbar-nav
       [nav-link "#/" "Home" :home collapsed?]
       [nav-link "#/about" "About" :about collapsed?]]]]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src (str js/context "/img/warning_clojure.png")}]]]])

(def date-format (tf/formatter "HH:mm"))
(def date-time-format (tf/formatter "yyyy:MM:dd HH:mm"))

(defn create-reading-element [reading]
  [:div.row
   [:h3 (:location reading)
    [:i {:class (str "wi " (:icon reading))}]]
   [:table.table
    [:tbody
     [:tr
      [:td "forecast"]
      [:td "week"]
      [:td (:week-summary reading)]]
     [:tr
      [:td]
      [:td "day"]
      [:td (:day-summary reading)]]
     [:tr
      [:td "sunrise/sunset"]
      [:td]
      [:td (tf/unparse date-format (t/to-default-time-zone (:sunrise reading)))
           " / "
           (tf/unparse date-format (t/to-default-time-zone (:sunset reading)))]]
     [:tr
      [:td "temp"]
      [:td "max"]
      [:td (gstring/format "%.1f" (:temperature-max reading)) " °C"]]
     [:tr
      [:td]
      [:td "now"]
      [:td (gstring/format "%.1f" (:temperature reading)) " °C"]]
     [:tr
      [:td "wind"]
      [:td]
      [:td (gstring/format "%.1f" (:wind-speed reading)) " km/hr - " (:wind-direction reading)]]]]])

; TODO create tests for this stuff
; TODO move homepage out to seperate module

(defn home-page []
  [:div.container-fluid
   (when-let [latest @(rf/subscribe [:latest])]
     ; [:div.container-fluid
      [:div {:class "row row-content"}
        [:div.col-xs-12
          [:ul {:class "tab-pane fade in active"}
           (for [reading  (:readings latest)]
             ^{:key (:location reading)} [create-reading-element reading])]]
        [:div.col-xs-12
          [:time "weather info @ " (tf/unparse date-time-format (t/to-default-time-zone (:date latest)))]]])])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page :about]))

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

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-latest!)
  (hook-browser-navigation!)
  (mount-components))
