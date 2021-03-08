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
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]))

(defonce APP (app/fulcro-app))

(defmutation increment [{:counter/keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in [:counter/id id :counter/count] inc))
  (refresh [env] [:counter/count]))

(defsc Counter [this {:counter/keys [id count] :as props} cprops]
  {:query          [:counter/id :counter/count {[:counters '_] [:counter/count]}]
   :ident          :counter/id
   :initLocalState (fn [] {:color "positive"})
   :initial-state  {:counter/id    :param/id
                    :counter/count :param/start}}
  (js/console.log "PROPS" props)
  (js/console.log "COMPUTED PROPS" cprops)
  (div :.item
    (dom/button :.ui.button {:classes [(str (comp/get-state this :color))]
                             :key     id
                             :onClick (fn [] (comp/transact! this [(increment {:counter/id id})
                                                                   :counters]))}
      (str count))))

(def ui-counter (comp/factory Counter {:keyfn :counter/id}))

(defsc Root [this {:keys [counters]}]
  {:query         [:counter/count
                   {:counters (comp/get-query Counter)}]
   :initial-state {:counters [{:id 1 :start 100}
                              {:id 2 :start 50}
                              {:id 3 :start 25}
                              {:id 4 :start 12}]}}
  (js/console.log "ROOT RENDER")
  (let [total (reduce + 0 (map :counter/count counters))]
    (div :.ui.list
      (div total)
      (map #(ui-counter (comp/computed % {:x 1})) counters))))

(defn start! []
  (app/mount! APP Root (js/document.getElementById "app")))

(def c (comp/ident->any APP [:counter/id 1]))

(comment
  (def c (first (comp/ident->components APP [:counter/id 1])))
  (comp/react-type c)
  (fdn/db->tree [{(comp/get-ident c)
                  (comp/get-query c (app/current-state c))}])
  (comp/props c)

  (comp/prop->classes APP :counters)
  (comp/registry-key->class :demo.app/Root)
  (comp/class->all APP demo.app/Root)

  (comp/get-ident (second (comp/class->all APP demo.app/Counter)))
  (comp/set-state! c {:color "negative"})
  (app/basis-t APP)
  (comp/tunnel-props! c (fdn/with-time
                          {:counter/id 1 :counter/count 66}
                          (app/basis-t APP)))

  )