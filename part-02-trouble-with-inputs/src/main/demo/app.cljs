(ns demo.app
  (:require
    [com.fulcrologic.fulcro.dom :as dom :refer [div input label h3]]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.react.hooks :as hooks]
    [goog.object :as gobj]
    [taoensso.timbre :as log]))

(def mount-point (.getElementById js/document "app"))
(defn raf! [f] (js/requestAnimationFrame f))
(defn render! [element] (js/ReactDOM.render element mount-point))

(defonce app-state (atom {:first-name "Tony"}))

(defn ui-form [{:keys [first-name]}]
  (div :.ui.container
    (div :.ui.form
      (h3 "My Form")
      (div :.field
        (label (str "First Name"))
        (dom/create-element "input"
          #js {:value   first-name
               :onInput (fn [evt]
                          (let [new-value (evt/target-value evt)]
                            (swap! app-state assoc :first-name new-value)))})))))

(defn ui-root [props]
  (ui-form props))

(defn start! []
  (raf!
    (fn next-frame* []
      (render! (ui-root @app-state))
      (raf! next-frame*))))


(comment
  (render! (ui-root @app-state))
  (start!)
  (swap! app-state assoc :first-name "Bob")

  )

