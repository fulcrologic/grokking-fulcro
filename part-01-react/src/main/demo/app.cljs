(ns demo.app
  (:require
    [com.fulcrologic.fulcro.dom :refer [div li ol h1]]
    [cljsjs.react]
    [cljsjs.react.dom]))















(def root (.getElementById js/document "app"))

(defn render! [element]
  (js/ReactDOM.render element root))


(defn items [items]
  (ol
    (map-indexed (fn [idx txt] (li {:key idx} txt)) items)))






(comment


  (def a "Item 1")
  (macroexpand '(div "hello"))
  (render! (.createElement js/React "div" #js {} #js ["Hello "]))
  (render! (.createElement js/React "div" #js {} #js ["Hello There"]))
  (render! (.createElement js/React "div" #js {"className" "red"} #js ["Hello There Bob"]))

  (render! (div
             (h1 "Items")
             (items ["a" "OTHER" "c"])))

  (time
    (dotimes [n 100000]
      (render! (div nil a))))

  )

