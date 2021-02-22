(ns demo.app
  (:require
    [goog.object :as gobj]
    [com.fulcrologic.fulcro.dom :as dom :refer [div label h3 i]]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.algorithms.denormalize :refer [db->tree]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.application :as app]
    [taoensso.timbre :as log]))

(defonce APP (app/fulcro-app))

(defmutation increment [{:counter/keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in [:counter/id id :counter/count] inc)))

(defsc Counter [this {:counter/keys [id count]}]
  {:query         [:counter/id :counter/count]
   :ident         :counter/id
   :initial-state {:counter/id    :param/id
                   :counter/count :param/start}}
  (div :.item
    (dom/button :.ui.button {:key     id
                             :onClick (fn [] (comp/transact! this [(increment {:counter/id 222})]))}
      (str count))))

(def ui-counter (comp/factory Counter {:keyfn :counter/id}))

(defsc Sample [this {:keys [message]}]
  {:x 1}
  (div {} message))

(def ui-sample (comp/factory Sample))

(defsc Root [this {:keys [counters]}]
  {:query         [{:counters (comp/get-query Counter)}]
   :initial-state {:counters [{:id 1 :start 100}
                              {:id 2 :start 50}
                              {:id 3 :start 25}
                              {:id 4 :start 12}]}}
  (div :.ui.list
    (ui-sample {:message "Hello"})
    #_(map #(ui-counter %) counters)))

(defn start! []
  (app/mount! APP Root (js/document.getElementById "app")))
