INSERT INTO PUBLIC.CATEGORIES
    (NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO PUBLIC.RATINGS
    (NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

/*
INSERT INTO PUBLIC.FILMS
    (NAME, DESCRIPTION, RELEASE_DATE, DURATION)
VALUES ('nisi eiusmod', 'adipisicing', '1967-03-25', 100), --"mpa": { "id": 1}
       ('New film', 'New film about friends', '1999-04-30', 120) ; --"rate": 4, "mpa": { "id": 3}, "genres": [{ "id": 1}]

INSERT INTO PUBLIC.FILM_CATEGORY
    (FILM_ID, CATEGORY_ID)
VALUES (2, 1);



INSERT INTO PUBLIC.USERS
(LOGIN, NAME, EMAIL, BIRTHDAY)
VALUES('dolore', 'Nick Name', 'mail@mail.ru', '1946-08-20'),
      ('friend', 'friend adipisicing', 'friend@mail.ru', '1976-08-20'),
      ('common', '', 'friend@common.ru', '2000-08-20');


INSERT INTO PUBLIC.FRIENDS
(RESPONDER_ID, REQUESTER_ID, IS_FRIENDS)
VALUES(1, 2, false),
      (1, 3, false),
      (2, 3, false);


INSERT INTO PUBLIC.LIKES
(FILM_ID, USER_ID)
VALUES(2, 1);
*/
