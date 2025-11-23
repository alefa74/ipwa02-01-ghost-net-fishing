package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Size;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class SizeDAO {

    @Inject
    private EntityManager em;
    
    public void save(Size size) {
    	em.getTransaction().begin();
    	em.persist(size);
    	em.getTransaction().commit();
    }

    public List<Size> findAll() {
        return em.createQuery("SELECT s FROM Size s", Size.class).getResultList();
    }
    
    public Size findById(Long id) {
        return em.find(Size.class, id);
    }

}
