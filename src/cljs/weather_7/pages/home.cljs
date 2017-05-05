(ns weather-7.pages.home
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.string :as gstring]
            [goog.string.format]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]))


; TODO autorefresh data if older than 10 mins

(def date-format (tf/formatter "HH:mm"))
(def date-time-format (tf/formatter "yyyy:MM:dd HH:mm"))

(defn days-to-next-spring
  [age-of-moon]
  (- 14 (mod age-of-moon 14)))

(defn format-moon-phase-text
  "create text for moon phase"
  [age-of-moon]
  (let [next-moon-type (if (= (quot age-of-moon 14) 0)
                         "full moon"
                         "new moon")
        days-to-next-spring (days-to-next-spring age-of-moon)]
    (if (= days-to-next-spring 14)
      next-moon-type
      (str days-to-next-spring
           (if (= days-to-next-spring 1)
             " day to "
             " days to ")
           next-moon-type))))

(defn create-reading-element
  [reading]
  [:div.row
   [:h4 (str (:location reading) " ")
    [:i {:class (str "wi " (:icon reading))}]
    " - "
    [:i {:class (str "wi " (:moon-phase-icon reading))}]]
   [:table.table
    [:tbody
     [:tr
      [:td "week"]
      [:td (:week-summary reading)]]
     [:tr
      [:td "day"]
      [:td (:day-summary reading)]]
     [:tr
      [:td "moon"]
      [:td
       (:phase-of-moon reading)
       ": "
       (format-moon-phase-text (inc (:age-of-moon reading)))]]
     (if (:date reading)
       [:tr
        [:td "next tide"]
        [:td
         (:type reading) " "
         (gstring/format "%.1f" (:height reading)) " m at "
         (tf/unparse date-format (t/to-default-time-zone (:date reading)))
         (if (= (days-to-next-spring (inc (:age-of-moon reading))) 14)
           ": spring tide")]])
     [:tr
      [:td "sunrise/set"]
      [:td (tf/unparse date-format (t/to-default-time-zone (:sunrise reading)))
           " / "
           (tf/unparse date-format (t/to-default-time-zone (:sunset reading)))]]
     [:tr
      [:td "temp"]
      [:td
       (gstring/format "%.1f" (:temperature-min reading))
       " / "
       [:strong
        (gstring/format "%.1f" (:temperature reading))]
       " / "
       (gstring/format "%.1f" (:temperature-max reading)) " °C"]]
     [:tr
      [:td "wind"]
      [:td (gstring/format "%.1f" (:wind-speed reading))
       " km/hr - "
       (:wind-direction reading)]]]]])

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
          [:time "weather info @ "
           (tf/unparse date-time-format (t/to-default-time-zone
                                         (:date latest)))]]])])

; TODO only show required locations as per database config
