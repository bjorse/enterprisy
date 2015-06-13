CREATE TABLE todo(
  id serial PRIMARY KEY,
  title text NOT NULL,
  type text NOT NULL,
  type_id int NOT NULL,
  description text NOT NULL,
  priority int NOT NULL,
  added timestamp NOT NULL DEFAULT NOW()
);
