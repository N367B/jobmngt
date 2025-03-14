package fr.atlantique.imt.inf211.jobmngt.config;

import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthenticationService authService;
    
    // URLs accessibles sans authentification
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/", "/login", "/logout", "/register", "/error", 
        "/css/", "/js/", "/img/", "/webjars/", "/favicon.ico",
        "/companies", "/candidates", "/jobs", "/applications",
        "/companies/create", "/candidates/create"
    );
    
    // URLs réservées aux entreprises
    private static final List<Pattern> COMPANY_URL_PATTERNS = Arrays.asList(
        Pattern.compile("^/companies/\\d+/edit$"),
        Pattern.compile("^/companies/\\d+/delete$"),
        Pattern.compile("^/jobs/create$"),
        Pattern.compile("^/jobs/\\d+/edit$"),
        Pattern.compile("^/jobs/\\d+/delete$"),
        Pattern.compile("^/messages/company.*$")
    );
    
    // URLs réservées aux candidats
    private static final List<Pattern> CANDIDATE_URL_PATTERNS = Arrays.asList(
        Pattern.compile("^/candidates/\\d+/edit$"),
        Pattern.compile("^/candidates/\\d+/delete$"),
        Pattern.compile("^/applications/create$"),
        Pattern.compile("^/applications/\\d+/edit$"),
        Pattern.compile("^/applications/\\d+/delete$"),
        Pattern.compile("^/messages/candidate.*$")
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        
        // Vérifier si l'URL est publique
        for (String publicUrl : PUBLIC_URLS) {
            if (requestUri.equals(publicUrl) || requestUri.startsWith(publicUrl) && publicUrl.endsWith("/")) {
                return true;
            }
        }
        
        HttpSession session = request.getSession();
        
        // Redirection vers login si non authentifié
        if (!authService.isAuthenticated(session)) {
            response.sendRedirect("/login?redirect=" + requestUri);
            return false;
        }
        
        // Vérification des permissions spécifiques pour les entreprises
        if (authService.isEntreprise(session)) {
            for (Pattern pattern : CANDIDATE_URL_PATTERNS) {
                if (pattern.matcher(requestUri).matches()) {
                    response.sendRedirect("/error/403");
                    return false;
                }
            }
            
            // Vérification supplémentaire pour les URLs d'entreprise
            if (requestUri.matches("/companies/\\d+/.*")) {
                int companyId = extractId(requestUri, "/companies/");
                if (!authService.ownsResource(session, companyId)) {
                    response.sendRedirect("/error/403");
                    return false;
                }
            }
        }
        
        // Vérification des permissions spécifiques pour les candidats
        if (authService.isCandidat(session)) {
            for (Pattern pattern : COMPANY_URL_PATTERNS) {
                if (pattern.matcher(requestUri).matches()) {
                    response.sendRedirect("/error/403");
                    return false;
                }
            }
            
            // Vérification supplémentaire pour les URLs de candidat
            if (requestUri.matches("/candidates/\\d+/.*")) {
                int candidateId = extractId(requestUri, "/candidates/");
                if (!authService.ownsResource(session, candidateId)) {
                    response.sendRedirect("/error/403");
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private int extractId(String uri, String prefix) {
        String idPart = uri.substring(prefix.length());
        int slashPos = idPart.indexOf('/');
        if (slashPos > 0) {
            idPart = idPart.substring(0, slashPos);
        }
        try {
            return Integer.parseInt(idPart);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}