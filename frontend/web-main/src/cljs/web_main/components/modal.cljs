(ns web-main.components)

(defn close-modal [id]
  (let [target (str "#" id)]
    (.modal (js/$ target) "hide")))

(defn modal [{:keys [id title body footer]}]
  [:div.modal.fade {:id id :role "dialog" :tabIndex "-1"}
    [:div.modal-dialog
      [:div.modal-content
        [:div.modal-header
          [:button.close {:type "button" :data-dismiss "modal"}]
          [:h4.modal-title title]]
        [:div.modal-body
         (body)]
        [:div.modal-footer
         (footer)]]]])

(defn modal-save-footer [on-save]
  [:div [:button.btn.btn-default {:data-dismiss "modal"} [:span.glyphicon.glyphicon-remove] " Close"]
        [:button.btn.btn-success {:on-click on-save} [:span.glyphicon.glyphicon-ok] " Save"]])

(defn modal-save [{:keys [id title body on-save]}]
  (modal {:id id
          :title title
          :body body
          :footer #(modal-save-footer on-save)}))
