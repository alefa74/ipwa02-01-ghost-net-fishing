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

        // PERSON TYPE
        if (em.createQuery("SELECT COUNT(p) FROM PersonType p", Long.class).getSingleResult() == 0) {
            em.persist(new PersonType("MELDER"));
            em.persist(new PersonType("BERGER"));
        }
    }
}

