(ns demo.app
  (:require-macros [demo.app-macros :refer [!]])
  (:require
    [com.fulcrologic.fulcro.dom :refer [div li ol h1]]))

(def root (.getElementById js/document "app"))

(defn render! [element] (js/ReactDOM.render element root))

(def a "Item 1")

(def props {:className "boo"})

(defn items [items]
  (ol
    (map-indexed
      (fn [idx txt]
        (li {:key idx} txt)) items)))

(defn div-basic [props & children]
  (js/React.createElement "div"
    (clj->js props)
    (clj->js (vec children))))

(comment
  (macroexpand '(div :#thing.red "Hello"))
  (render! (.createElement js/React "div" #js {} #js ["Hello There"]))
  (render! (.createElement js/React "div"
             #js {"className" "red"}
             #js ["Hello There Bob"]))

  ;; RAW React speed
  (time
    (dotimes [n 100000]
      (.createElement js/React "div"
        #js {"className" "red"} "Hello")))

  (time
    (dotimes [n 100000]
      (div a)))

  (macroexpand '(div :.red.wide.fat.thing a))

  (def a "")
  (macroexpand '(div a))
  (macroexpand '(div {} a))
  (macroexpand '(div props a))
  (macroexpand '(div :#id.boo props
                  (ol
                    (li "1"))))
  (macroexpand '(div :.boo {:className "other"}
                  (ol
                    (li "1"))))

  (render! (div
             (h1 :.red "Items")
             (ol
               (map-indexed
                 (fn [idx label]
                   (li {:key idx} label))
                 ["A" "B"])
               )
             #_(items ["a" "ER" "c"])))

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

  )

