(ns weather-7.pages.summary
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as c]))

; TODO create tests for this stuff

(def date-format (tf/formatter "yyyy:MM:dd"))

(defn round [x]
  (/ (Math/round (* x 10)) 10))

(enable-console-print!)

; (prn "bob" @(rf/subscribe [:summary]))

(def test-data
  (last @(rf/subscribe [:summary])))
    ; [ { "location" "Sandton",
    ;     "summary"
    ;      [ {"date" "2017-03-19T00:00:00.000Z",
    ;         "max-temp" 25.299999237060547,
    ;         "avg-temp" 25.049999237060547,
    ;         "max-wind" 3.200000047683716,
    ;         "min-temp" 24.799999237060547,
    ;         "count" 2,
    ;         "min-wind" 2.4000000953674316,
    ;         "avg-wind" 2.8000000715255737}
    ;       {"date" "2017-03-24T00:00:00.000Z",
    ;         "max-temp" 26,
    ;         "avg-temp" 25.899999618530273,
    ;         "max-wind" 5.099999904632568,
    ;         "min-temp" 25.799999237060547,
    ;         "count" 2,
    ;         "min-wind" 3,
    ;         "avg-wind" 4.049999952316284}
    ;       { "date" "2017-03-26T00:00:00.000Z",
    ;         "max-temp" 22,
    ;         "avg-temp" 16.666666666666668,
    ;         "max-wind" 6.900000095367432,
    ;         "min-temp" 13.300000190734863,
    ;         "count" 3,
    ;         "min-wind" 1.899999976158142,
    ;         "avg-wind" 4.99999996026357}
    ;       { "date" "2017-03-28T00:00:00.000Z",
    ;         "max-temp" 20.100000381469727,
    ;         "avg-temp" 20.100000381469727,
    ;         "max-wind" 0.6000000238418579,
    ;         "min-temp" 20.100000381469727,
    ;         "count" 1,
    ;         "min-wind" 0.6000000238418579,
    ;         "avg-wind" 0.6000000238418579}]}
    ;   { "location" "Paradise Beach",
    ;       "summary"
    ;          [{"date" "2017-03-19T00:00:00.000Z",
    ;            "max-temp" 22.799999237060547,
    ;            "avg-temp" 22.399999618530273,
    ;            "max-wind" 31.600000381469727,
    ;            "min-temp" 22,
    ;            "count" 2,
    ;            "min-wind" 30.100000381469727,
    ;            "avg-wind" 30.850000381469727}
    ;           {"date" "2017-03-24T00:00:00.000Z",
    ;            "max-temp" 19.100000381469727,
    ;            "avg-temp" 19.050000190734863,
    ;            "max-wind" 25.299999237060547,
    ;            "min-temp" 19,
    ;            "count" 2,
    ;            "min-wind" 24.899999618530273,
    ;            "avg-wind" 25.09999942779541}
    ;           {"date" "2017-03-26T00:00:00.000Z",
    ;            "max-temp" 25.700000762939453,
    ;            "avg-temp" 18.666666666666668,
    ;            "max-wind" 10.899999618530273,
    ;            "min-temp" 13.899999618530273,
    ;            "count" 3,
    ;            "min-wind" 2.4000000953674316,
    ;            "avg-wind" 6.733333269755046}
    ;           {"date" "2017-03-28T00:00:00.000Z",
    ;            "max-temp" 18.5,
    ;            "avg-temp" 18.5,
    ;            "max-wind" 16.600000381469727,
    ;            "min-temp" 18.5,
    ;            "count" 1,
    ;            "min-wind" 16.600000381469727,
    ;            "avg-wind" 16.600000381469727}]}]))

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
   :legend {:layout "vertical"
            :align "right"
            :verticalAlign "top"
            :x -40
            :y 100
            :floating true
            :borderWidth 1
            :shadow true}
   :credits {:enabled false}})
   ; :series [{:name "Maximum"
   ;           :data [107 31 635 203 2]}
   ;          {:name "Average"
   ;           :data [133 156 947 408 6]}
   ;          {:name "Minimum"
   ;           :data [973 914 4054 732 34]}]})

(defn get-series [summary]
  {:series
   [{:name "Maximum" :data (map #(:max-temp %)
                                (:summary summary))}
    {:name "Average" :data (map #(:avg-temp %)
                                (:summary summary))}
    {:name "Minimum" :data (map #(:min-temp %)
                                (:summary summary))}]})

; (tf/unparse date-format (t/to-default-time-zone (get % "date")))

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
; (prn "full" (clj->js (load-data test-data)))
;

(defn home-render []
  [:div {:style {:min-width "310px" :max-width "800px"
                 :height "400px" :margin "0 auto"}}])

(defn home-did-mount [this]
  (js/Highcharts.Chart. (r/dom-node this) (clj->js (load-data test-data))))


(defn summary-page []
  (r/create-class {:reagent-render home-render
                   :component-did-mount home-did-mount}))
