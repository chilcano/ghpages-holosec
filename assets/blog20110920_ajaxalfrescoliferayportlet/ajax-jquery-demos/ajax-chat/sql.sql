-- Run these SQL queries on a MySQL database and configure backend.php

CREATE TABLE `messages` (
  `id` int(7) NOT NULL auto_increment,
  `user` varchar(255) NOT NULL,
  `msg` text NOT NULL,
  `time` int(9) NOT NULL,
  PRIMARY KEY  (`id`)
);