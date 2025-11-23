package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
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
    
    public Status findStatusByName(String name) {
		TypedQuery<Status> query = em.createQuery(
				"SELECT s FROM Status s WHERE s.name = :name", Status.class);
		query.setParameter("name", name);
		return query.getSingleResult();    	
    }
}
