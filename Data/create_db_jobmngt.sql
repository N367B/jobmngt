-- Title :             SQL script to create the DB of the jobmngt project
-- Version :           0.1
-- Creation date :     24/08/2023
-- Last modification date : 26/02/2025
-- Author :            Grégory SMITS
-- Description :       Script used to create the DB of the job management app.
--                     Tested on PostgreSQL 10

-- +----------------------------------------------------------------------------------------------+
-- | Suppress tables if they exist                                                                |
-- +----------------------------------------------------------------------------------------------+


drop table if exists sector;
drop table if exists qualificationlevel;
drop table if exists userapp;
drop table if exists appuser;
drop table if exists company;

-- +----------------------------------------------------------------------------------------------+
-- | Tables creation                                                                              |
-- +----------------------------------------------------------------------------------------------+


create table sector
(
  id       serial primary key,
  label varchar(50) not null unique
);

create table qualificationlevel
(
  id       serial primary key,
  label varchar(50) not null unique
);

create table appuser
(
  id              serial primary key,
  mail           varchar(50) not null unique,
  password        varchar(50) not null check (length(password) >= 4),
  usertype        varchar(50) check (usertype in ('company', 'candidate'))
);

alter table appuser add constraint mailformat check (mail ~* '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$');

-- +----------------------------------------------------------------------------------------------+
-- | Insert some data                                                                             |
-- +----------------------------------------------------------------------------------------------+

-- Some users to test the login form
insert into appuser(mail, password, usertype) values ('g.a@imt.fr', '1234', 'candidate');
insert into appuser(mail, password, usertype) values ('ceo@google.fr', 'abcd','company');

-- Some sectors
insert into sector(label) values ('Purchase/Logistic');                  --  1
insert into sector(label) values ('Administration');             --  2
insert into sector(label) values ('Agriculture');                        --  3
insert into sector(label) values ('Agrofood');                    --  4
insert into sector(label) values ('Insurance');                          --  5
insert into sector(label) values ('Audit/Advise/Expertise');           --  6
insert into sector(label) values ('Public works/Real estate');                     --  7
insert into sector(label) values ('Trade');                         --  8
insert into sector(label) values ('Communication/Art/Media/Fashion');       --  9
insert into sector(label) values ('Accounting');                       -- 10
insert into sector(label) values ('Direction/Execution');       -- 11
insert into sector(label) values ('Distribution/Sale');              -- 12
insert into sector(label) values ('Electronic/Microelectronic');     -- 13
insert into sector(label) values ('Environment');                      -- 14
insert into sector(label) values ('Finance/Bank');                     -- 15
insert into sector(label) values ('Training/Teaching');             -- 16
insert into sector(label) values ('Hotel/Restaurant/Tourism');   -- 17
insert into sector(label) values ('Industry/Engineering/Production');    -- 18
insert into sector(label) values ('Computer science');                       -- 19
insert into sector(label) values ('Juridique/Fiscal/Droit');             -- 20
insert into sector(label) values ('Marketing');                          -- 21
insert into sector(label) values ('Public/Parapublic');                  -- 22
insert into sector(label) values ('Human resources');                -- 23
insert into sector(label) values ('Health/Social/Biology/HHumanitarian');  -- 24
insert into sector(label) values ('Telecom/Networking');                    -- 25

-- Some qualification levels
insert into qualificationlevel(label) values ('Professional level');   --  1
insert into qualificationlevel(label) values ('A-diploma');       --  2
insert into qualificationlevel(label) values ('Licence');     --  3
insert into qualificationlevel(label) values ('Master');     --  4
insert into qualificationlevel(label) values ('PhD');  --  5

