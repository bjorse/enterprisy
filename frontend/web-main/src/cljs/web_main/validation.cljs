(ns web-main.validation)

(defn any-validation-errors? [validation-errors]
  (not-empty (seq validation-errors)))

(defn has-validation-error? [param validation-errors]
  (let [name (name param)]
    (= true (some #(= name (:key %)) validation-errors))))

(defn get-validation-errors [validation-errors]
  (map #(:text %) validation-errors))

(defn validation-error-row [error]
  [:div
    [:span.glyphicon.glyphicon-exclamation-sign] (str " " error)
    [:br]])

(defn render-errors [validation-errors]
  (when (any-validation-errors? validation-errors)
    (let [errors (get-validation-errors validation-errors)]
      [:div.row
        [:div.alert.alert-danger
          (for [error errors]
            ^{:key error} [validation-error-row error])]])))
