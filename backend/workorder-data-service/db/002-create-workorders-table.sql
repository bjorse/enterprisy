CREATE TABLE workorders(
  id serial PRIMARY KEY,
  title text NOT NULL,
  description text NOT NULL,
  client_id int NOT NULL,
  estimated_time int NOT NULL,
  status text NOT NULL,
  added timestamp NOT NULL DEFAULT NOW(),
  changed timestamp NOT NULL DEFAULT NOW()
);
