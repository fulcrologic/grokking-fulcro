(ns demo.app
  (:require
    [com.fulcrologic.fulcro.dom :refer [div li ol h1]]
    [cljsjs.react]
    [cljsjs.react.dom]))

(def root (.getElementById js/document "app"))

(defn render! [element] (js/ReactDOM.render element root))

(def a "Item 1")

(def props {:className "boo"})

(defn items [items]
  (ol
    (map-indexed (fn [idx txt] (li {:key idx} txt)) items)))

(comment
  (render! (.createElement js/React "div" #js {} #js ["Hello "]))
  (render! (.createElement js/React "div" #js {} #js ["Hello There"]))
  (render! (.createElement js/React "div" #js {"className" "red"} #js ["Hello There Bob"]))

  (macroexpand '(div "hello"))
  (macroexpand '(div a))
  (macroexpand '(div {} a))
  (macroexpand '(div props a))
  (macroexpand '(div :.boo props
                  (ol
                    (li "1"))))
  (macroexpand '(div :.boo {:className "other"}
                  (ol
                    (li "1"))))

  (render! (div
             (h1 "Items")
             (items ["a" "OTHER" "c"])))

  ;; Forces function call and runtime interpolation of CLJ -> js props
  (let [props {:className "red"}]
    (time
      (dotimes [n 100000]
        (render! (div props "hello")))))

  ;; Function-based, but with js props already
  (let [props #js {:className "red"}]
    (time
      (dotimes [n 100000]
        (render! (div props a)))))

  ;; Macro expanded speed
  (time
    (dotimes [n 100000]
      (render! (div {:className "red"} "hello"))))

  ;; RAW React speed
  (time
    (dotimes [n 100000]
      (render! (.createElement js/React "div" #js {"className" "red"} "Hello"))))

  )

