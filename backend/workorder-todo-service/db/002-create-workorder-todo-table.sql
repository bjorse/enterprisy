CREATE TABLE workorder_todo(
  id serial PRIMARY KEY,
  workorder_id int NOT NULL,
  workorder_status text NOT NULL,
  client_id int NOT NULL,
  todo_id int NOT NULL,
  added timestamp NOT NULL DEFAULT NOW()
);
