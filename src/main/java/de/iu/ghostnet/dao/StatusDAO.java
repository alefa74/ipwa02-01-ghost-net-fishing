package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class StatusDAO {

    @Inject
    private EntityManager em;

    @Transactional
    public void save(Status status) {
    	em.getTransaction().begin();
    	em.persist(status);
    	em.getTransaction().commit();    	
    }

    public List<Status> findAll() {
        return em.createQuery("SELECT s FROM Status s", Status.class).getResultList();
    }

    public Status findById(Long id) {
        return em.find(Status.class, id);
    }

    public Status findByName(String name) {
        return em.createQuery("SELECT s FROM Status s WHERE s.name = :name", Status.class)
                                 .setParameter("name", name)
                                 .getSingleResult();
    }
}
