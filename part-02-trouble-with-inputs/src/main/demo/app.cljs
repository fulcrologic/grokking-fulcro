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

(defn Form [^js js-props]
  (let [{:keys [first-name]} (.-myprops js-props)]
    (div :.ui.container
      (div :.ui.form
        (h3 "My Form")
        (div :.field
          (label (str "First Name"))
          (dom/input
            {:value    first-name
             :onChange (fn [evt]
                         (let [new-value (evt/target-value evt)]
                           (swap! app-state assoc :first-name new-value)))}))))))

(defn ui-form [props]
  (dom/create-element Form #js {:myprops props}))

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

