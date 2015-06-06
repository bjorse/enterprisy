CREATE DATABASE clients;
GRANT ALL PRIVILEGES ON DATABASE clients TO postgres;

USE DATABASE clients;

CREATE TABLE clients(
  id serial PRIMARY KEY,
  firstname text NOT NULL,
  lastname text NOT NULL,
  email text NOT NULL,
  birthdate date NOT NULL,
  gender text NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  added date NOT NULL DEFAULT CURRENT_DATE
);
