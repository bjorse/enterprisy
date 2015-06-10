CREATE TABLE workorder_process(
  id serial PRIMARY KEY,
  workorder_id int NOT NULL,
  client_id int NOT NULL,
  status text NOT NULL,
  added timestamp NOT NULL DEFAULT NOW()
);
