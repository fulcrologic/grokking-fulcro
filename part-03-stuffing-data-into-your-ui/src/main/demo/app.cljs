(ns demo.app
  (:require
    [goog.object :as gobj]
    [com.fulcrologic.fulcro.dom :as dom :refer [div label h3 i]]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.algorithms.denormalize :refer [db->tree]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.application :as app]
    [taoensso.timbre :as log]))

(defonce APP (app/fulcro-app))

(comment
  (swap! app-state assoc-in [:address/id 11] {:address/id 11 :address/street "111 Main"})
  (swap! app-state assoc-in [:person/id 3] {:person/id 3 :person/first-name "Sally" :person/address [:address/id 11]})
  (swap! app-state update-in [:component/id :person-list :people] conj [:person/id 3]))

(defsc Person [this {:person/keys [id first-name]}]
  {:query         [:person/id :person/first-name]
   :ident         :person/id
   :initial-state {:person/id         :param/id
                   :person/first-name :param/name}}
  (div :.item {:key id}
    (i :.user.icon)
    (div :.content (div :.header first-name))))

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:keys [people]}]
  {:query         [{:people (comp/get-query Person)}]
   :ident         (fn [] [:component/id :person-list])
   :initial-state {:people [{:id 1 :name "Tony"}
                            {:id 2 :name "Emily"}]}}
  (comp/fragment
    (h3 :.ui.header "People")
    (div :.ui.middle.aligned.selection.list
      (map ui-person people))))

(def ui-person-list (comp/factory PersonList))

(defsc PersonForm [this {:person/keys [id first-name]}]
  {:query [:person/id :person/first-name]
   :ident :person/id}
  (div :.ui.form
    (h3 :.ui.header "Person")
    (div :.field
      (label "First Name")
      (dom/input {:value (or first-name "")}))))

(def ui-person-form (comp/factory PersonForm {:keyfn :person/id}))

(defsc PeoplePage [this {:keys [person-list person-form]}]
  {:query         [{:person-list (comp/get-query PersonList)}
                   {:person-form (comp/get-query PersonForm)}]
   :ident         (fn [] [:component/id :people-page])
   :initial-state {:person-list {}}}
  (div :.ui.two.column.grid
    (div :.column
      (ui-person-list person-list))
    (div :.column
      (ui-person-form person-form))))

(def ui-people-page (comp/factory PeoplePage))

(defsc HomePage [this {:keys [message-of-the-day]}]
  {:query         [:message-of-the-day]
   :ident         (fn [] [:component/id :home-page])
   :initial-state {:message-of-the-day "Hello world"}}
  (div :.ui.segment
    (h3 :.ui.header
      message-of-the-day)))

(def ui-home-page (comp/factory HomePage))

(defsc Router [this {:keys [active-page home-page people-page]}]
  {:query         [:active-page
                   {:home-page (comp/get-query HomePage)}
                   {:people-page (comp/get-query PeoplePage)}]
   :ident         (fn [] [:component/id :router])
   :initial-state {:active-page :people
                   :home-page   {}
                   :people-page {}}}
  (div :.ui.container
    (div :.ui.pointing.menu
      (div :.item {:classes [(when (= active-page :home) "active")]} (str "Home"))
      (div :.item {:classes [(when (= active-page :addresses) "active")]} "Addresses")
      (div :.item {:classes [(when (= active-page :people) "active")]} "People"))
    (div :.ui.segment
      (case active-page
        :home (ui-home-page home-page)
        :people (ui-people-page people-page)
        (div "UNKNOWN ROUTE")))))

(def ui-router (comp/factory Router))

(defsc Root [this {:keys [router]}]
  {:query         [{:router (comp/get-query Router)}]
   :initial-state {:router {}}}
  (ui-router router))

(defn start! []
  (app/mount! APP Root (js/document.getElementById "app")))

(comment
  (-> APP ::app/state-atom deref)

  ;; Hey server, Give me a list of people!!!
  {:people [:person/id :person/first-name :person/age
            {:person/primary-address [:address/street :address/city]}]}
  ;; EQL server response
  (merge/merge-component! APP PersonList
    {:people [{:person/id         1
               :person/first-name "Bob"}
              {:person/id         2
               :person/first-name "Sam"}
              {:person/id         3
               :person/first-name "Barbara"}]})

  (df/load! APP :friends Person {:target [:component/id :person-list :people]})
  )
