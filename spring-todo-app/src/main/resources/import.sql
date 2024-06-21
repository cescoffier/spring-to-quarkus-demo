INSERT INTO todo(id, title, completed, ordering) VALUES (1, 'Introduction to Quarkus', true, 0);
INSERT INTO todo(id, title, completed, ordering) VALUES (2, 'Hibernate with Panache', false, 1);
INSERT INTO todo(id, title, completed, ordering) VALUES (3, 'Visit Quarkus web site', false, 2);
INSERT INTO todo(id, title, completed, ordering) VALUES (4, 'Star Quarkus project', false, 3);

ALTER SEQUENCE todo_id_seq RESTART WITH 5;