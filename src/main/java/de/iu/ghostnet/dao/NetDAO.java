package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Net;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class NetDAO {

    @Inject
    private EntityManager em;

    public void save(Net net) {
        em.getTransaction().begin();
        em.persist(net);
        em.getTransaction().commit();
    }

    public List<Net> findAll() {
        return em.createQuery("SELECT n FROM Net n", Net.class).getResultList();
    }
}
