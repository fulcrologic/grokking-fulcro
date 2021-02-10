(ns demo.app
  (:require
    [com.fulcrologic.fulcro.dom :as dom :refer [div label h3 i]]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [taoensso.timbre :as log]))

;; Not using Fulcro's wrapped input for now...
(defn input [props] (dom/create-element "input" (clj->js props)))
(def mount-point (.getElementById js/document "app"))
(defn raf! [f] (js/requestAnimationFrame f))
(defn render! [element] (js/ReactDOM.render element mount-point))

(defonce app-state (atom {}))

(defn ui-root [props]
  (div :.ui.container
    (div :.ui.pointing.menu
      (div :.item "Home")
      (div :.item "Addresses")
      (div :.item {:classes ["active"]} "People"))
    (div :.ui.segment
      (div :.ui.two.column.grid
        (div :.column
          (h3 :.ui.header "People")
          (div :.ui.middle.aligned.selection.list
            (div :.item
              (i :.user.icon)
              (div :.content (div :.header "Tony")))
            (div :.item
              (i :.user.icon)
              (div :.content (div :.header "Emily")))))
        (div :.column
          (div :.ui.form
            (h3 :.ui.header "Person")
            (div :.field
              (label "First Name")
              (input {}))))))))

(defn start! []
  (log/info "Rendering started")
  (raf!
    (fn next-frame* []
      ;(swap! app-state update :render-frame (fnil inc 0))
      (render! (ui-root #js {:fulcro$props @app-state}))
      (raf! next-frame*))))

(comment

  )

