(ns app.ui
  (:require [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [untangled.i18n :refer-macros [tr trf]]
            [untangled.client.core :refer [InitialAppState initial-state]]
            [app.css :as css :refer [css-merge local-class local-kw] :refer-macros [apply-css]]
            [garden.core :as g]
            [garden.units :refer [px]]
            [garden.stylesheet :as gs]
            yahoo.intl-messageformat-with-locales))

(declare Root)

(def color 'black)

(defrecord MyCss []
  css/CSS
  (css [this] [[(local-kw MyCss :a) {:color 'blue}]]))

(defui ^:once Child
  static css/CSS
  (css [this]
    (let [p (local-kw Child :p)]
      (css-merge
        [p {:font-weight 'bold}]
        [(gs/at-media {:min-width (px 700)} [p {:color 'red}])])))
  static InitialAppState
  (initial-state [cls params] {:id 0 :label (:label params)})
  static om/IQuery
  (query [this] [:id :label])
  static om/Ident
  (ident [this props] [:child/by-id (:id props)])
  Object
  (render [this]
    (let [{:keys [id label]} (om/props this)]
      (css/apply-css Child
        (dom/p #js {:class [:p :$r]} label)))))

(def ui-child (om/factory Child))

(defui ^:once Root
  static css/CSS
  (css [this] (css-merge
                MyCss
                Child))
  static InitialAppState
  (initial-state [cls params]
    {:child (initial-state Child {:label "Constructed Label"})})
  static om/IQuery
  (query [this] [:ui/react-key {:child (om/get-query Child)}])
  Object
  (render [this]
    (let [{:keys [child ui/react-key]} (om/props this)]
      (dom/div #js {:key react-key}
        (dom/style nil (g/css (css/css Root)))
        (ui-child child)))))

