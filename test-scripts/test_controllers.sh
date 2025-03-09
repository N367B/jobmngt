#!/bin/bash

# Vérifier si jq est installé
if ! command -v jq &> /dev/null; then
    echo "jq n'est pas installé. Veuillez l'installer avec : sudo apt install jq"
    exit 1
fi

# URL de base de l'API
BASE_URL="http://localhost:8080"

# Fonction pour afficher les en-têtes de section
header() {
    echo -e "\n\n================ $1 ================\n"
}

# Fonction pour attendre entre les requêtes (évite les problèmes de concurrence)
wait_a_bit() {
    sleep 0.5
}

# ===========================================================
# Test du contrôleur Entreprise
# ===========================================================
header "TESTS DU CONTRÔLEUR ENTREPRISE"

echo "Test 1: Lister toutes les entreprises"
curl -s -X GET $BASE_URL/api/entreprises | jq '.'
wait_a_bit

echo -e "\nTest 2: Créer une entreprise de test (données codées en dur)"
response=$(curl -s -X POST $BASE_URL/api/entreprises/test)
echo "$response" | jq '.'
wait_a_bit

# Tenter d'extraire l'ID de l'entreprise
ENTREPRISE_ID=$(echo "$response" | jq -r '.idEntreprise')
if [ "$ENTREPRISE_ID" = "null" ] || [ -z "$ENTREPRISE_ID" ]; then
    echo -e "\nAucun ID d'entreprise n'a été récupéré. Test de création alternatif..."
    
    echo -e "\nTest 3: Créer une entreprise personnalisée"
    response=$(curl -s -X POST -H "Content-Type: application/json" -d '{
      "appUser": {
        "mail": "contact@newcompany.fr",
        "password": "password123",
        "userType": "entreprise",
        "city": "Paris"
      },
      "denomination": "Nouvelle Entreprise",
      "description": "Description de la nouvelle entreprise"
    }' $BASE_URL/api/entreprises)
    echo "$response" | jq '.'
    wait_a_bit
    
    # Nouveau test pour extraire l'ID
    ENTREPRISE_ID=$(echo "$response" | jq -r '.idEntreprise')
fi

echo -e "\nID de l'entreprise créée: $ENTREPRISE_ID"

if [ "$ENTREPRISE_ID" != "null" ] && [ ! -z "$ENTREPRISE_ID" ]; then
    echo -e "\nTest 4: Récupérer l'entreprise par ID ($ENTREPRISE_ID)"
    curl -s -X GET "$BASE_URL/api/entreprises/$ENTREPRISE_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 5: Mettre à jour l'entreprise"
    curl -s -X PUT -H "Content-Type: application/json" -d '{
      "appUser": {
        "mail": "contact@newcompany.fr",
        "password": "newpassword456"
      },
      "denomination": "Nouvelle Entreprise Modifiée",
      "description": "Description modifiée de l'"'"'entreprise"
    }' "$BASE_URL/api/entreprises/$ENTREPRISE_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 6: Supprimer l'entreprise"
    curl -s -X DELETE "$BASE_URL/api/entreprises/$ENTREPRISE_ID" -v
    wait_a_bit
else
    echo -e "\nAttention: Impossible de récupérer un ID d'entreprise valide, les tests suivants sont ignorés."
fi

# ===========================================================
# Test du contrôleur Candidat
# ===========================================================
header "TESTS DU CONTRÔLEUR CANDIDAT"

echo "Test 1: Lister tous les candidats"
curl -s -X GET $BASE_URL/api/candidats | jq '.'
wait_a_bit

echo -e "\nTest 2: Créer un candidat de test (données codées en dur)"
response=$(curl -s -X POST $BASE_URL/api/candidats/test)
echo "$response" | jq '.'
wait_a_bit

# Tenter d'extraire l'ID du candidat
CANDIDAT_ID=$(echo "$response" | jq -r '.idCandidat')
if [ "$CANDIDAT_ID" = "null" ] || [ -z "$CANDIDAT_ID" ]; then
    echo -e "\nAucun ID de candidat n'a été récupéré. Test de création alternatif..."
    
    echo -e "\nTest 3: Créer un candidat personnalisé"
    response=$(curl -s -X POST -H "Content-Type: application/json" -d '{
      "appUser": {
        "mail": "jean.martin@example.com",
        "password": "password123",
        "userType": "candidat",
        "city": "Lyon"
      },
      "firstName": "Jean",
      "lastName": "Martin"
    }' $BASE_URL/api/candidats)
    echo "$response" | jq '.'
    wait_a_bit
    
    # Nouveau test pour extraire l'ID
    CANDIDAT_ID=$(echo "$response" | jq -r '.idCandidat')
fi

echo -e "\nID du candidat créé: $CANDIDAT_ID"

if [ "$CANDIDAT_ID" != "null" ] && [ ! -z "$CANDIDAT_ID" ]; then
    echo -e "\nTest 4: Récupérer le candidat par ID ($CANDIDAT_ID)"
    curl -s -X GET "$BASE_URL/api/candidats/$CANDIDAT_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 5: Mettre à jour le candidat"
    curl -s -X PUT -H "Content-Type: application/json" -d '{
      "appUser": {
        "mail": "jean.martin@example.com",
        "password": "newpassword456",
        "city": "Marseille"
      },
      "firstName": "Jean-Pierre",
      "lastName": "Martin"
    }' "$BASE_URL/api/candidats/$CANDIDAT_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 6: Supprimer le candidat"
    curl -s -X DELETE "$BASE_URL/api/candidats/$CANDIDAT_ID" -v
    wait_a_bit
else
    echo -e "\nAttention: Impossible de récupérer un ID de candidat valide, les tests suivants sont ignorés."
fi

# ===========================================================
# Test du contrôleur Offre d'Emploi
# ===========================================================
header "TESTS DU CONTRÔLEUR OFFRE D'EMPLOI"

# Supposons que les ID secteur et qualification existent
SECTEUR_ID=1
QUALIFICATION_ID=1

echo "Test 1: Lister toutes les offres d'emploi"
curl -s -X GET $BASE_URL/api/offres | jq '.'
wait_a_bit

echo -e "\nTest 2: Créer une offre d'emploi de test (données codées en dur)"
response=$(curl -s -X POST $BASE_URL/api/offres/test)
echo "$response" | jq '.'
wait_a_bit

# Tenter d'extraire l'ID de l'offre
OFFRE_ID=$(echo "$response" | jq -r '.idOffreEmploi')
if [ "$OFFRE_ID" = "null" ] || [ -z "$OFFRE_ID" ]; then
    echo -e "\nAucun ID d'offre n'a été récupéré. Test de création alternatif..."
    
    echo -e "\nTest 3: Créer une offre personnalisée"
    response=$(curl -s -X POST -H "Content-Type: application/json" -d '{
      "entreprise": {"idEntreprise": 1},
      "qualificationLevel": {"idQualification": 1},
      "title": "Développeur Full Stack",
      "taskDescription": "Développement d'"'"'applications web modernes",
      "publicationDate": "2025-03-15",
      "sectors": [{"idSecteur": 1}]
    }' $BASE_URL/api/offres)
    echo "$response" | jq '.'
    wait_a_bit
    
    # Nouveau test pour extraire l'ID
    OFFRE_ID=$(echo "$response" | jq -r '.idOffreEmploi')
fi

echo -e "\nID de l'offre créée: $OFFRE_ID"

if [ "$OFFRE_ID" != "null" ] && [ ! -z "$OFFRE_ID" ]; then
    echo -e "\nTest 4: Récupérer l'offre par ID ($OFFRE_ID)"
    curl -s -X GET "$BASE_URL/api/offres/$OFFRE_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 5: Rechercher des offres par secteur et qualification"
    curl -s -X GET "$BASE_URL/api/offres/search?secteur=$SECTEUR_ID&qualification=$QUALIFICATION_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 6: Mettre à jour l'offre"
    curl -s -X PUT -H "Content-Type: application/json" -d '{
      "title": "Développeur Full Stack Senior",
      "taskDescription": "Développement et architecture d'"'"'applications web modernes",
      "publicationDate": "2025-03-16"
    }' "$BASE_URL/api/offres/$OFFRE_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 7: Supprimer l'offre"
    curl -s -X DELETE "$BASE_URL/api/offres/$OFFRE_ID" -v
    wait_a_bit
else
    echo -e "\nAttention: Impossible de récupérer un ID d'offre valide, les tests suivants sont ignorés."
fi

# ===========================================================
# Test du contrôleur Candidature
# ===========================================================
header "TESTS DU CONTRÔLEUR CANDIDATURE"

echo "Test 1: Lister toutes les candidatures"
curl -s -X GET $BASE_URL/api/candidatures | jq '.'
wait_a_bit

echo -e "\nTest 2: Créer une candidature de test (données codées en dur)"
response=$(curl -s -X POST $BASE_URL/api/candidatures/test)
echo "$response" | jq '.'
wait_a_bit

# Tenter d'extraire l'ID de la candidature
CANDIDATURE_ID=$(echo "$response" | jq -r '.idCandidature')
if [ "$CANDIDATURE_ID" = "null" ] || [ -z "$CANDIDATURE_ID" ]; then
    echo -e "\nAucun ID de candidature n'a été récupéré. Test de création alternatif..."
    
    echo -e "\nTest 3: Créer une candidature personnalisée"
    response=$(curl -s -X POST -H "Content-Type: application/json" -d '{
      "candidat": {"idCandidat": 2},
      "qualificationLevel": {"idQualification": 2},
      "cv": "mon_cv.pdf",
      "appDate": "2025-03-15",
      "sectors": [{"idSecteur": 1}]
    }' $BASE_URL/api/candidatures)
    echo "$response" | jq '.'
    wait_a_bit
    
    # Nouveau test pour extraire l'ID
    CANDIDATURE_ID=$(echo "$response" | jq -r '.idCandidature')
fi

echo -e "\nID de la candidature créée: $CANDIDATURE_ID"

if [ "$CANDIDATURE_ID" != "null" ] && [ ! -z "$CANDIDATURE_ID" ]; then
    echo -e "\nTest 4: Récupérer la candidature par ID ($CANDIDATURE_ID)"
    curl -s -X GET "$BASE_URL/api/candidatures/$CANDIDATURE_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 5: Rechercher des candidatures par secteur et qualification"
    curl -s -X GET "$BASE_URL/api/candidatures/search?secteur=$SECTEUR_ID&qualification=$QUALIFICATION_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 6: Mettre à jour la candidature"
    curl -s -X PUT -H "Content-Type: application/json" -d '{
      "cv": "mon_cv_mis_a_jour.pdf",
      "qualificationLevel": {"idQualification": 2}
    }' "$BASE_URL/api/candidatures/$CANDIDATURE_ID" | jq '.'
    wait_a_bit
    
    echo -e "\nTest 7: Supprimer la candidature"
    curl -s -X DELETE "$BASE_URL/api/candidatures/$CANDIDATURE_ID" -v
    wait_a_bit
else
    echo -e "\nAttention: Impossible de récupérer un ID de candidature valide, les tests suivants sont ignorés."
fi

echo -e "\n\nTests terminés!"
