package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Status;
import de.iu.ghostnet.model.Status.StatusType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class StatusDAO {

	// Vom CDI bereitgestellter EntityManager für Datenbankoperationen
    @Inject
    private EntityManager em;

    @Transactional
    public void save(Status status) {
        // Speichert einen neuen Status in der Datenbank
    	em.getTransaction().begin();
    	em.persist(status);
    	em.getTransaction().commit();    	
    }

    public List<Status> findAll() {
        // Liefert alle verfügbaren Status-Einträge zurück
        return em.createQuery("SELECT s FROM Status s", Status.class).getResultList();
    }

    public Status findById(Long id) {
        // Sucht einen Status anhand seiner ID
        return em.find(Status.class, id);
    }

    public Status findByName(StatusType name) {
        // Sucht einen Status anhand seines eindeutigen Namens
       return em.createQuery("SELECT s FROM Status s WHERE s.name = :name", Status.class)
                                 .setParameter("name", name)
                                 .getSingleResult();
    }
}
