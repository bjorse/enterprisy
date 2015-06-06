(ns web-main.static-data)

(def workorder-status-list
  ["New" "Approved" "Rejected" "In progress" "Finished" "Closed"])

(def workorders-data
  [{:id 1 :client-id 1 :title "This is a work order" :description "This is some description" :estimated-time 20 :status "New" :comments [] :created "2015-05-31 12:34" :updated "2015-05-31 12:34"}
   {:id 2 :client-id 1 :title "This is another work order" :description "This is some description" :estimated-time 40 :status "Approved" :comments ["Approval is appreciated as quick as possible!"] :created "2015-06-01 10:02" :updated "2015-06-01 10:02"}
   {:id 3 :client-id 27 :title "This is a new work order" :description "This is some description" :estimated-time 40 :status "New" :comments ["Approval is appreciated as quick as possible!"] :created "2015-06-01 10:02" :updated "2015-06-01 10:02"}
   {:id 4 :client-id 27 :title "This is a accepted work order" :description "This is some description" :estimated-time 40 :status "Approved" :comments [] :created "2015-06-01 12:34" :updated "2015-06-01 12:34"}
   {:id 5 :client-id 27 :title "This is a rejected work order" :description "This is some description" :estimated-time 40 :status "Rejected" :comments [] :created "2015-06-02 10:23" :updated "2015-06-02 10:23"}
   {:id 6 :client-id 27 :title "This is a work order in progress" :description "This is some description" :estimated-time 40 :status "In progress" :comments [] :created "2015-06-03 11:12" :updated "2015-06-03 11:12"}
   {:id 7 :client-id 27 :title "This is a finished work order" :description "This is some description" :estimated-time 40 :status "Finished" :comments [] :created "2015-06-04 22:22" :updated "2015-06-04 22:22"}
   {:id 8 :client-id 27 :title "This is a closed work order" :description "This is some description" :estimated-time 40 :status "Closed" :comments [] :created "2015-06-01 20:00" :updated "2015-06-01 20:00"}])
