(ns demo.app
  (:require
    [com.fulcrologic.fulcro.dom :as dom :refer [div input label h3 i]]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [taoensso.timbre :as log]))

(def mount-point (.getElementById js/document "app"))
(defn raf! [f] (js/requestAnimationFrame f))
(defn render! [element] (js/ReactDOM.render element mount-point))

(defonce app-state (atom {}))

(defn ui-root [{:keys [render-frame] :as props}]
  (div (str "TODO")))

(defn start! []
  (log/info "Rendering started")
  (raf!
    (fn next-frame* []
      ;(swap! app-state update :render-frame (fnil inc 0))
      (render! (ui-root @app-state))
      (raf! next-frame*))))

(comment

  )

