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
        // Persistiert eine neue Netzgröße
    	em.getTransaction().begin();
    	em.persist(size);
    	em.getTransaction().commit();
    }

    public List<Size> findAll() {
        // Gibt alle definierten Netzgrößen zurück
        return em.createQuery("SELECT s FROM Size s", Size.class).getResultList();
    }
    
    public Size findById(Long id) {
        // Findet eine Größe anhand der ID
        return em.find(Size.class, id);
    }

}
