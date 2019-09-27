CREATE TABLE Player (
  username varchar(32) NOT NULL,
  password varchar(32) NOT NULL,
  gamesPlayed int(12) unsigned,
  gamesWon int(12) unsigned,
  winTimeSum double(6, 2),
  isOnline boolean,
  PRIMARY KEY username (username)
);