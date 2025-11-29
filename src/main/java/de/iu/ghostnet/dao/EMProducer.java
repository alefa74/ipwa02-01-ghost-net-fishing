package de.iu.ghostnet.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public class EMProducer {

	// Erzeugt die EntityManagerFactory für die gesamte Anwendung
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("GhostNetPU");

    @Produces
    @ApplicationScoped
    public EntityManager createEntityManager() {
        // Stellt einen vom CDI verwalteten EntityManager bereit
        return emf.createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager em) {
        // Schließt den EntityManager beim Beenden des Kontextes
        if (em.isOpen()) {
            em.close();
        }
    }
}
