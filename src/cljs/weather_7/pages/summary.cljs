(ns weather-7.pages.summary
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.string :as gstring]
            [goog.string.format]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]))

(def date-format (tf/formatter "HH:mm"))
(def date-time-format (tf/formatter "yyyy:MM:dd HH:mm"))

; (defn create-reading-element [reading]
;   [:div.row
;    [:h3 (str (:location reading) " ")
;     [:i {:class (str "wi " (:icon reading))}]]
;    [:table.table
;     [:tbody
;      [:tr
;       [:td "forecast"]
;       [:td "week"]
;       [:td (:week-summary reading)]]
;      [:tr
;       [:td]
;       [:td "day"]
;       [:td (:day-summary reading)]]
;      [:tr
;       [:td "sunrise/sunset"]
;       [:td]
;       [:td (tf/unparse date-format (t/to-default-time-zone (:sunrise reading)))
;            " / "
;            (tf/unparse date-format (t/to-default-time-zone (:sunset reading)))]]
;      [:tr
;       [:td "temp"]
;       [:td "max"]
;       [:td (gstring/format "%.1f" (:temperature-max reading)) " °C"]]
;      [:tr
;       [:td]
;       [:td "now"]
;       [:td (gstring/format "%.1f" (:temperature reading)) " °C"]]
;      [:tr
;       [:td "wind"]
;       [:td]
;       [:td (gstring/format "%.1f" (:wind-speed reading)) " km/hr - " (:wind-direction reading)]]
;      (if (:date reading)
;        [:tr
;         [:td "next tide"]
;         [:td]
;         [:td
;          (:type reading) " "
;          (gstring/format "%.1f" (:height reading)) " m at "
;          (tf/unparse date-format (t/to-default-time-zone (:date reading)))]])]]])

; TODO create tests for this stuff

; (defn summary-page []
;   [:div.container-fluid
;    (when-let [latest @(rf/subscribe [:latest])]
     ; [:div.container-fluid
      ; [:div {:class "row row-content"}
      ;   [:div.col-xs-12
          ; [:ul {:class "tab-pane fade in active"}
         ; [:h3 "hello"])
           ; (for [reading  (:readings latest)]
           ;   ^{:key (:location reading)} [create-reading-element reading])]]
        ; [:div.col-xs-12
        ;   [:time "weather info @ " (tf/unparse date-time-format (t/to-default-time-zone (:date latest)))]]])])

(defn summary-page []
   [:div.container-fluid
    (when-let [latest @(rf/subscribe [:latest])]
     ; [:div.container-fluid
      [:div {:class "row row-content"}
        [:div.col-xs-12
          [:h3 "hello, I'm here"]]])])
