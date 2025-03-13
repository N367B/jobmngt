package fr.atlantique.imt.inf211.jobmngt.service;

import fr.atlantique.imt.inf211.jobmngt.entity.Candidat;
import java.util.List;

public interface CandidatService {
    List<Candidat> listCandidats();
    Candidat getCandidatById(int id);
    long countCandidats();
    Candidat saveCandidat(Candidat candidat);
    boolean deleteCandidat(int id);
}