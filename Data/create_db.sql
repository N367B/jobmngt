-- Suppress tables if they exist
drop table if exists candidatureSecteur;
drop table if exists emploiSecteur;
drop table if exists candidature;
drop table if exists offreEmploi;
drop table if exists messageCandidature;
drop table if exists messageOffre;
drop table if exists candidate;
drop table if exists entreprise;
drop table if exists appuser;
drop table if exists qualificationlevel;
drop table if exists sector;

-- Tables creation
create table sector
(
  idSecteur     serial primary key,
  labelSecteur  varchar(50) not null unique
);

create table qualificationlevel
(
  idQualification     serial primary key,
  labelQualification  varchar(50) not null unique
);

create table appuser
(
  idUser          serial primary key,
  mail            varchar(50) not null unique,
  password        varchar(50) not null check (length(password) >= 4),
  usertype        varchar(50) check (usertype in ('company', 'candidate')),
  city           varchar(50)
);

create table entreprise
(
  idEntreprise    integer primary key references appuser(idUser) not null,
  mail            varchar(50) not null unique references appuser(mail),
  denomination    text,
  description     text
);

create table candidate
(
  idCandidate     integer primary key references appuser(idUser) not null,
  mail            varchar(50) not null unique references appuser(mail),
  firstname       varchar(50) not null,
  lastname        varchar(50) not null
);

create table offreEmploi
(
  idOffreEmploi     serial primary key,
  title           varchar(50) not null,
  taskDescription text not null,
  publicationDate date not null,
  idLabelQualification integer references qualificationlevel(idQualification) not null,
  mail          integer references appuser(idUser) not null
);




create table candidature 
(
  idCandidature   serial primary key,
  CV              text not null,
  appdat          date not null,
  labelQualification integer references qualificationlevel(idQualification) not null,
  mail          integer references appuser(idUser) not null
);

create table messageOffre
(
  idMessageOffre  serial primary key,
  message        text not null,
  publicationDate date not null,
  idOffreEmploi integer references offreEmploi(idOffreEmploi) not null,
  idCandidature integer references candidature(idCandidature) not null
);

create table messageCandidature
(
  idMessageCandidature  serial primary key,
  message        text not null,
  publicationDate date not null,
  idCandidature integer references candidature(idCandidature) not null,
  idOffreEmploi integer references offreEmploi(idOffreEmploi) not null
);

create table indexEmploiSecteur
(
  labelSecteur  integer references sector(idSecteur) not null,
  idOffreEmploi integer references offreEmploi(idOffreEmploi) not null,
  primary key(labelSecteur, idOffreEmploi) 
);

create table indexCandidatureSecteur
(
  labelSecteur  integer references sector(idSecteur) not null,
  idCandidature integer references candidature(idCandidature) not null,
  primary key(labelSecteur, idCandidature) 
);

alter table appuser add constraint mailformat check (mail ~* '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$');

-- Insert some data
-- Some users to test the login form
insert into appuser(mail, password, usertype) values ('g.a@imt.fr', '1234', 'candidate');
insert into appuser(mail, password, usertype) values ('ceo@google.fr', 'abcd','company');

-- Some sectors
insert into sector(labelSecteur) values ('Purchase/Logistic');                  --  1
insert into sector(labelSecteur) values ('Administration');             --  2
insert into sector(labelSecteur) values ('Agriculture');                        --  3
insert into sector(labelSecteur) values ('Agrofood');                    --  4
insert into sector(labelSecteur) values ('Insurance');                          --  5
insert into sector(labelSecteur) values ('Audit/Advise/Expertise');           --  6
insert into sector(labelSecteur) values ('Public works/Real estate');                     --  7
insert into sector(labelSecteur) values ('Trade');                         --  8
insert into sector(labelSecteur) values ('Communication/Art/Media/Fashion');       --  9
insert into sector(labelSecteur) values ('Accounting');                       -- 10
insert into sector(labelSecteur) values ('Direction/Execution');       -- 11
insert into sector(labelSecteur) values ('Distribution/Sale');              -- 12
insert into sector(labelSecteur) values ('Electronic/Microelectronic');     -- 13
insert into sector(labelSecteur) values ('Environment');                      -- 14
insert into sector(labelSecteur) values ('Finance/Bank');                     -- 15
insert into sector(labelSecteur) values ('Training/Teaching');             -- 16
insert into sector(labelSecteur) values ('Hotel/Restaurant/Tourism');   -- 17
insert into sector(labelSecteur) values ('Industry/Engineering/Production');    -- 18
insert into sector(labelSecteur) values ('Computer science');                       -- 19
insert into sector(labelSecteur) values ('Juridique/Fiscal/Droit');             -- 20
insert into sector(labelSecteur) values ('Marketing');                          -- 21
insert into sector(labelSecteur) values ('Public/Parapublic');                  -- 22
insert into sector(labelSecteur) values ('Human resources');                -- 23
insert into sector(labelSecteur) values ('Health/Social/Biology/HHumanitarian');  -- 24
insert into sector(labelSecteur) values ('Telecom/Networking');                    -- 25

-- Some qualification levels
insert into qualificationlevel(labelQualification) values ('Professional level');   --  1
insert into qualificationlevel(labelQualification) values ('A-diploma');       --  2
insert into qualificationlevel(labelQualification) values ('Licence');     --  3
insert into qualificationlevel(labelQualification) values ('Master');     --  4
insert into qualificationlevel(labelQualification) values ('PhD');  --  5