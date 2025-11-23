package de.iu.ghostnet.init;

import de.iu.ghostnet.model.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class InitData {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {

        // STATUS
        if (em.createQuery("SELECT COUNT(s) FROM Status s", Long.class).getSingleResult() == 0) {
            em.persist(new Status("GEMELDET"));
            em.persist(new Status("BERGUNG_BEVORSTEHEND"));
            em.persist(new Status("GEBORGEN"));
            em.persist(new Status("VERSCHOLLEN"));
        }

        // PERSON TYPE
        if (em.createQuery("SELECT COUNT(p) FROM PersonType p", Long.class).getSingleResult() == 0) {
            em.persist(new PersonType("MELDER"));
            em.persist(new PersonType("BERGER"));
        }
    }
}

