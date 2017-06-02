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

(defonce chart-config-static
  {:chart {:type "spline"}
   :yAxis {:min 0
           :title {:text "Degrees C"
                   :align "high"}
           :labels {:overflow "justify"}}
   :tooltip {:valueSuffix " °C"}
   :plotOptions {:spline {:dataLabels {:enabled false}
                          :marker {:enabled false}}}
   :exporting {:enabled false}
   :credits {:enabled false}})

(defn build-series [summary-data]
  {:series
   [{:name "Maximum" :data (map
                            (fn [x]
                              [(c/to-long (t/to-default-time-zone (:date x)))
                               (round (:max-temp x))])
                            (:summary summary-data))}
    {:name "Average" :data (map
                            (fn [x]
                              [(c/to-long (t/to-default-time-zone (:date x)))
                               (round (:avg-temp x))])
                            (:summary summary-data))}
    {:name "Minimum" :data (map
                            (fn [x]
                              [(c/to-long (t/to-default-time-zone (:date x)))
                               (round (:min-temp x))])
                            (:summary summary-data))}]})

(defn build-xaxis [summary-data]
  {:xAxis {:type "datetime"
           :dateTimeLabelFormats
            {:month "%e %b"
             :year "%b"}
           :title {:text "Date"}}})

(defn build-title [summary-data]
  {:title {:text (str (:location summary-data) " Temperature Summary")}})


(defn build-chart-config [summary-data]
  (merge (build-xaxis summary-data)
         (build-series summary-data)
         (build-title summary-data)
         chart-config-static))

(defn home-render []
  [:div {:style {:min-width "310px" :max-width "800px"
                 :height "400px" :margin "0 auto"}}])

(defn extract-data [location]
  (first (filter #(= location (:location %)) @(rf/subscribe [:summary]))))

(defn home-did-mount [location this]
  (js/Highcharts.Chart. (r/dom-node this) (clj->js
                                           (build-chart-config
                                            (extract-data location)))))

(defn chart [location]
   (r/create-class {:reagent-render home-render
                    :component-did-mount (partial home-did-mount location)}))

(defn summary-page []
  [:div
    [chart "Paradise Beach"]
    [chart "Sandton"]
    [chart "Salt River"]])

; TODO get locations to show from database
