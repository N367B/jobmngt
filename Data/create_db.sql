-- =================================================================================
-- Title :             jobmngt_db_creation.sql
-- Description :       Script de création des tables pour l'application Job Management
-- Auteur :            BODIN Noé
-- Date de création :  2025-03-06
-- =================================================================================

-- =========================
-- 1) Suppression si existe
-- =========================

DROP TABLE IF EXISTS candidatureSecteur;
DROP TABLE IF EXISTS emploiSecteur;
DROP TABLE IF EXISTS messageCandidature;
DROP TABLE IF EXISTS messageOffre;
DROP TABLE IF EXISTS candidature;
DROP TABLE IF EXISTS offreEmploi;
DROP TABLE IF EXISTS candidat;
DROP TABLE IF EXISTS entreprise;
DROP TABLE IF EXISTS appuser;
DROP TABLE IF EXISTS qualificationlevel;
DROP TABLE IF EXISTS sector;
DROP TABLE IF EXISTS company;

-- =========================
-- 2) Création des tables
-- =========================

-- Table des secteurs d'activité
CREATE TABLE sector (
    idSecteur       SERIAL PRIMARY KEY,
    labelSecteur    VARCHAR(50) NOT NULL UNIQUE
);

-- Table des niveaux de qualification
CREATE TABLE qualificationlevel (
    idQualification     SERIAL PRIMARY KEY,
    labelQualification  VARCHAR(50) NOT NULL UNIQUE
);

-- Table des utilisateurs génériques
CREATE TABLE appuser (
    idUser      SERIAL PRIMARY KEY,
    mail        VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(50) NOT NULL CHECK (LENGTH(password) >= 4),
    usertype    VARCHAR(10) CHECK (usertype IN ('entreprise', 'candidat')),
    city        VARCHAR(50)
);

-- Contrôle du format de l'email
ALTER TABLE appuser 
    ADD CONSTRAINT mailformat 
    CHECK (mail ~* '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$');

-- Table entreprise
CREATE TABLE entreprise (
    idEntreprise    INTEGER PRIMARY KEY REFERENCES appuser(idUser) ON DELETE CASCADE,
    denomination    VARCHAR(100) NOT NULL,
    description     TEXT
);

-- Table candidat
CREATE TABLE candidat (
    idCandidat      INTEGER PRIMARY KEY REFERENCES appuser(idUser) ON DELETE CASCADE,
    firstname       VARCHAR(50) NOT NULL,
    lastname        VARCHAR(50) NOT NULL
);

-- Table des offres d'emploi
CREATE TABLE offreEmploi (
    idOffreEmploi       SERIAL PRIMARY KEY,
    title               VARCHAR(100) NOT NULL,
    taskDescription     TEXT NOT NULL,
    publicationDate     DATE NOT NULL DEFAULT CURRENT_DATE,
    -- Référence au niveau de qualification (1..1)
    idQualification     INTEGER NOT NULL REFERENCES qualificationlevel(idQualification),
    -- Entreprise propriétaire (1..N)
    idEntreprise        INTEGER NOT NULL REFERENCES entreprise(idEntreprise) ON DELETE CASCADE
);

-- Table des candidatures
CREATE TABLE candidature (
    idCandidature       SERIAL PRIMARY KEY,
    cv                  TEXT NOT NULL,
    appDate             DATE NOT NULL DEFAULT CURRENT_DATE,
    -- Référence au niveau de qualification (1..1)
    idQualification     INTEGER NOT NULL REFERENCES qualificationlevel(idQualification),
    -- Candidat propriétaire (1..N)
    idCandidat          INTEGER NOT NULL REFERENCES candidat(idCandidat) ON DELETE CASCADE
);

-- Table de messages liés à une offre
CREATE TABLE messageOffre (
    idMessageOffre      SERIAL PRIMARY KEY,
    message             TEXT NOT NULL,
    publicationDate     DATE NOT NULL DEFAULT CURRENT_DATE,
    -- L'offre concernée
    idOffreEmploi       INTEGER NOT NULL REFERENCES offreEmploi(idOffreEmploi) ON DELETE CASCADE,
    -- La candidature concernée (si on souhaite lier à une candidature précise)
    idCandidature       INTEGER REFERENCES candidature(idCandidature) ON DELETE CASCADE
);

-- Table de messages liés à une candidature
CREATE TABLE messageCandidature (
    idMessageCandidature   SERIAL PRIMARY KEY,
    message                TEXT NOT NULL,
    publicationDate        DATE NOT NULL DEFAULT CURRENT_DATE,
    -- La candidature concernée
    idCandidature          INTEGER NOT NULL REFERENCES candidature(idCandidature) ON DELETE CASCADE,
    -- L'offre concernée (si on souhaite lier à une offre précise)
    idOffreEmploi          INTEGER REFERENCES offreEmploi(idOffreEmploi) ON DELETE CASCADE
);

-- Table d'association entre offreEmploi et sector (relation N..N)
CREATE TABLE emploiSecteur (
    idOffreEmploi   INTEGER NOT NULL REFERENCES offreEmploi(idOffreEmploi) ON DELETE CASCADE,
    idSecteur       INTEGER NOT NULL REFERENCES sector(idSecteur) ON DELETE CASCADE,
    PRIMARY KEY (idOffreEmploi, idSecteur)
);

-- Table d'association entre candidature et sector (relation N..N)
CREATE TABLE candidatureSecteur (
    idCandidature   INTEGER NOT NULL REFERENCES candidature(idCandidature) ON DELETE CASCADE,
    idSecteur       INTEGER NOT NULL REFERENCES sector(idSecteur) ON DELETE CASCADE,
    PRIMARY KEY (idCandidature, idSecteur)
);

-- =========================
-- 3) Insertion de données
-- =========================

-- Quelques enregistrements de test pour appuser
INSERT INTO appuser(mail, password, usertype, city)
VALUES
    ('ceo@google.fr', 'abcd', 'entreprise', 'Brest'),
    ('john.doe@imt.fr', 'pass1', 'candidat', 'Nantes'),
    ('jane.smith@imt.fr', 'pass2', 'candidat', 'Rennes');

-- Entreprise associée à l'usertype 'entreprise'
INSERT INTO entreprise(idEntreprise, denomination, description)
SELECT idUser, 'Google', 'Développement de logiciels'
FROM appuser
WHERE mail = 'ceo@google.fr';

-- Candidats
INSERT INTO candidat(idCandidat, firstname, lastname)
SELECT idUser, 'John', 'Doe'
FROM appuser
WHERE mail = 'john.doe@imt.fr';

INSERT INTO candidat(idCandidat, firstname, lastname)
SELECT idUser, 'Jane', 'Smith'
FROM appuser
WHERE mail = 'jane.smith@imt.fr';

-- Secteurs
INSERT INTO sector(labelSecteur) VALUES
('Computer science'),
('Marketing'),
('Finance/Bank'),
('Administration');

-- Niveaux de qualification
INSERT INTO qualificationlevel(labelQualification) VALUES
('Professional level'),
('A-diploma'),
('Licence'),
('Master'),
('PhD');

-- Exemple d'offre d'emploi
INSERT INTO offreEmploi(title, taskDescription, idQualification, idEntreprise)
VALUES 
('Chef de projet IT', 'Gestion de projets informatiques', 1, (SELECT idEntreprise FROM entreprise WHERE idEntreprise = (SELECT idUser FROM appuser WHERE mail = 'ceo@google.fr')));

-- Exemple de candidature
INSERT INTO candidature(cv, idQualification, idCandidat)
VALUES
('cv_john_doe.pdf', 2, (SELECT idCandidat FROM candidat WHERE idCandidat = (SELECT idUser FROM appuser WHERE mail = 'john.doe@imt.fr')));

-- Association offre-secteur
INSERT INTO emploiSecteur(idOffreEmploi, idSecteur)
VALUES
(
  (SELECT idOffreEmploi FROM offreEmploi WHERE title = 'Chef de projet IT'),
  (SELECT idSecteur FROM sector WHERE labelSecteur = 'Computer science')
);

-- Association candidature-secteur
INSERT INTO candidatureSecteur(idCandidature, idSecteur)
VALUES
(
  (SELECT idCandidature FROM candidature WHERE cv = 'cv_john_doe.pdf'),
  (SELECT idSecteur FROM sector WHERE labelSecteur = 'Computer science')
);

-- Exemple de messageOffre
INSERT INTO messageOffre(message, idOffreEmploi, idCandidature)
VALUES
(
  'Une nouvelle offre pouvant vous intéresser...', 
  (SELECT idOffreEmploi FROM offreEmploi WHERE title = 'Chef de projet IT'),
  (SELECT idCandidature FROM candidature WHERE cv = 'cv_john_doe.pdf')
);

-- Exemple de messageCandidature
INSERT INTO messageCandidature(message, idCandidature, idOffreEmploi)
VALUES
(
  'Ma candidature pourrait correspondre à votre offre...', 
  (SELECT idCandidature FROM candidature WHERE cv = 'cv_john_doe.pdf'),
  (SELECT idOffreEmploi FROM offreEmploi WHERE title = 'Chef de projet IT')
);

-- Fin du script
COMMIT;
