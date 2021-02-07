(ns demo.raw
  (:require
    [cljsjs.react]
    [cljsjs.react.dom]))

(def root (.getElementById js/document "app"))

(defn render! [element]
  (js/ReactDOM.render element root))

(defn div [props & children]
  (js/React.createElement "div"
    (clj->js props)
    (clj->js (vec children))))

(defn ol [props & children]
  (js/React.createElement "ol"
    (clj->js props)
    (clj->js (vec children))))

(defn li [props & children]
  (js/React.createElement "li"
    (clj->js props)
    (clj->js (vec children))))

(comment
  (render! (div {:className "red"} "Hello "))
  (render! (div {:className "red"} "Hello There"))
  (render! (div {:className "red"} "Hello There Tony!"))
  (render! (div {:className "red"} (div {}
                                     (ol {}
                                       (li {} "A")
                                       (li {} "B")
                                       (li {} "A")))))
  )

