DROP ALL OBJECTS DELETE FILES;

CREATE TABLE IF NOT EXISTS PUBLIC.FILMS (
                              ID IDENTITY NOT NULL PRIMARY KEY,
                              NAME CHARACTER VARYING NOT NULL,
                              DESCRIPTION CHARACTER VARYING,
                              RELEASE_DATE DATE,
                              DURATION INTEGER
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES (
                                                 ID IDENTITY NOT NULL PRIMARY KEY,
                                                 NAME CHARACTER VARYING
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_GENRE (
                                                    FILM_ID INTEGER REFERENCES PUBLIC.FILMS (ID),
                                                    GENRE_ID INTEGER REFERENCES PUBLIC.GENRES (ID)
);


CREATE TABLE IF NOT EXISTS PUBLIC.MPA (
                                              ID IDENTITY NOT NULL PRIMARY KEY,
                                              NAME CHARACTER VARYING
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_MPA (
                                                  FILM_ID INTEGER REFERENCES PUBLIC.FILMS (ID),
                                                  MPA_ID INTEGER REFERENCES PUBLIC.MPA (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
                                            ID IDENTITY NOT NULL PRIMARY KEY,
                                            LOGIN CHARACTER VARYING NOT NULL,
                                            NAME CHARACTER VARYING,
                                            EMAIL CHARACTER VARYING NOT NULL,
                                            BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDS (
                                RESPONDER_ID INTEGER REFERENCES PUBLIC.USERS (ID),
                                REQUESTER_ID INTEGER REFERENCES PUBLIC.USERS (ID) ,
                                IS_FRIENDS BOOLEAN
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_LIKE (
                              FILM_ID INTEGER REFERENCES PUBLIC.FILMS (ID),
                              USER_ID INTEGER REFERENCES PUBLIC.USERS (ID)
);
