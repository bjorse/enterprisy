CREATE TABLE workorder_comments(
  id serial PRIMARY KEY,
  workorder_id int NOT NULL,
  description text NOT NULL,
  added timestamp NOT NULL DEFAULT NOW()
);
