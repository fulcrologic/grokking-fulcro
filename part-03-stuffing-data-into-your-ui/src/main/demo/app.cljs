(ns demo.app
  (:require
    [com.fulcrologic.fulcro.dom :as dom :refer [div label h3 i]]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.algorithms.denormalize :refer [db->tree]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [taoensso.timbre :as log]))

;; Not using Fulcro's wrapped input for now...
(defn input [props] (dom/create-element "input" (clj->js props)))
(def mount-point (.getElementById js/document "app"))
(defn raf! [f] (js/requestAnimationFrame f))
(defn render! [element] (js/ReactDOM.render element mount-point))

(defonce app-state (atom
                     {:root         {:router [:component/id :router]}
                      :component/id {:router      {:active-page :people
                                                   :people-page [:component/id :people-page]}
                                     :people-page {:person-list [:component/id :person-list]
                                                   :person-form [:person/id 1]}
                                     :person-list {:people [[:person/id 1]
                                                            [:person/id 2]]}}
                      :person/id    {1 {:person/id 1 :person/first-name "Tony"}
                                     2 {:person/id 2 :person/first-name "Emily"}}}))

(comment

  (swap! app-state assoc-in [:address/id 11] {:address/id 11 :address/street "111 Main"})
  (swap! app-state assoc-in [:person/id 3] {:person/id 3 :person/first-name "Sally" :person/address [:address/id 11]})
  (swap! app-state update-in [:component/id :person-list :people] conj [:person/id 3]))

(defn ui-person-list [{:keys [people]}]
  (comp/fragment
    (h3 :.ui.header "People")
    (div :.ui.middle.aligned.selection.list
      (map-indexed
        (fn [idx {:person/keys [id first-name]}]
          (div :.item {:key idx}
            (i :.user.icon)
            (div :.content (div :.header first-name))))
        people))))

(defn ui-person-form
  {:query [:person/id :person/first-name :person/address]}
  [{:person/keys [id first-name address]}]
  (div :.ui.form
    (h3 :.ui.header "Person")
    (div :.field
      (label "First Name")
      (input {:value (or first-name "")}))))

(defn ui-people-page [{:keys [person-list person-form]}]
  (div :.ui.two.column.grid
    (div :.column
      (ui-person-list person-list))
    (div :.column
      (ui-person-form person-form))))

(defn ui-router
  {:query [:active-page {:people-page (:query (meta #'ui-people-page))}]}
  [{:keys [active-page people-page]}]
  (div :.ui.container
    (div :.ui.pointing.menu
      (div :.item {:classes [(when (= active-page :home) "active")]} (str "Home"))
      (div :.item {:classes [(when (= active-page :addresses) "active")]} "Addresses")
      (div :.item {:classes [(when (= active-page :people) "active")]} "People"))
    (div :.ui.segment
      (case active-page
        :people (ui-people-page people-page)
        (div "UNKNOWN ROUTE")))))

(defn ui-root
  {:query [{:router (:query (meta #'ui-router))}]}
  [{:keys [router]}]
  (ui-router router))

(comment
  (:query (meta #'ui-router))
  (:query (meta #'ui-root))

  )

(defn start! []
  (log/info "Rendering started")
  (raf!
    (fn next-frame* []
      (swap! app-state update :render-frame (fnil inc 0))
      (let [{:keys [root]} (db->tree '[{:root
                                        [{:router
                                          [:active-page
                                           {:people-page
                                            [{:person-list [{:people [:person/id :person/first-name]}]}
                                             {:person-form [:person/id :person/first-name {:person/address [*]}]}]}]}]}] @app-state @app-state)]
        (render! (ui-root root)))
      (raf! next-frame*))))
