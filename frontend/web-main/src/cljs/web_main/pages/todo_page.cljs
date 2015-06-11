(ns web-main.pages.todo-page)

(defn create-link [{:keys [type type-id]}]
  (case type
    "workorder" (str "#/workorders/" type-id)
    nil))

(defn render-todo-item [todo-item]
  (let [todo-link (create-link todo-item)]
    [:div.well.well-sm
      [:span (str (:added todo-item) " ")
        [:a {:href todo-link} (:title todo-item)]]]))

(defn render [todo-items]
  (let [any-todo-items? (pos? (count @todo-items))]
    [:div
      (when any-todo-items?
        (for [todo-item (reverse (sort-by :added @todo-items))]
          ^{:key (:id todo-item)} [render-todo-item todo-item]))
      (when (not any-todo-items?)
        [:h3 "This is where the todo items will go (now in a separate component)!"])]))
