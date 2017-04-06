(ns weather-7.pages.summary
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as c]))

; TODO create tests for this stuff

(def date-format (tf/formatter "yyyy/MM/dd"))

(defn round [x]
  (/ (Math/round (* x 10)) 10))

(enable-console-print!)

(def chart-config
  {:chart {:type "spline"}
   :title {:text "Paradise Beach Temperature History"}
   ; :subtitle {:text "Source: Wikipedia.org"}
   ; :xAxis {:categories ["Africa" "America" "Asia" "Europe" "Oceania"]
   ;         :title {:text nil}}
   :yAxis {:min 0
           :title {:text "Degrees C"
                   :align "high"}
           :labels {:overflow "justify"}}
   :tooltip {:valueSuffix " deg C"}
   :plotOptions {:spline {:dataLabels {:enabled false}
                          :marker {:enabled false}}}
   ; :legend {:layout "vertical"
   ;          :align "right"
   ;          :verticalAlign "top"
   ;          :x -40
   ;          :y 100
   ;          :floating true
   ;          :borderWidth 1
   ;          :shadow true}
   :credits {:enabled false}})

(defn get-series [summary]
  {:series
   [{:name "Maximum" :data (map #(round (:max-temp %))
                                (:summary summary))}
    {:name "Average" :data (map #(round (:avg-temp %))
                                (:summary summary))}
    {:name "Minimum" :data (map #(round (:min-temp %))
                                (:summary summary))}]})

; (tf/unparse date-format (t/to-default-time-zone (get % "date")))
; TODO sort out time axis

(defn get-xaxis [summary]
  {:xAxis {:categories
            (into [] (map #(tf/unparse date-format (t/to-default-time-zone (:date %)))
                          (:summary summary)))}})
           ; :dateTimeLabelFormats
           ;  {:month "%e. %b"
           ;   :year "%b"}
           ; :title {:text "Date"}}})

(defn load-data [summary-data]
  (merge (get-xaxis summary-data)
         (get-series summary-data)
         chart-config))

; (prn "xaxis" (get-xaxis test-data))
; (prn "series" (get-series test-data))
; (prn "full" (clj->js (load-data (last @(rf/subscribe [:summary])))))


(defn home-render []
  [:div {:style {:min-width "310px" :max-width "800px"
                 :height "400px" :margin "0 auto"}}])

(defn home-did-mount1 [this]
  (js/Highcharts.Chart. (r/dom-node this) (clj->js (load-data (last @(rf/subscribe [:summary]))))))

(defn home-did-mount2 [this]
  (js/Highcharts.Chart. (r/dom-node this) (clj->js (load-data (first @(rf/subscribe [:summary]))))))

(defn chart [location]
  (let [data (load-data (last @(rf/subscribe [:summary])))]
   (r/create-class {:reagent-render home-render
                    :display-name "chart1"
                    :component-did-mount home-did-mount2})))

(defn chart2 []
  (r/create-class {:reagent-render home-render
                   :display-name "chart2"
                   :component-did-mount home-did-mount2}))

(defn summary-page []
  [:div
    [chart "Sandton"]
    [chart2]])

; TODO make these charts generic
