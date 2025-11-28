package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class NetDAO {

    @Inject
    private EntityManager em;

    public void save(Net net) {
        // Persistiert ein neues Geisternetz
    	em.getTransaction().begin();
    	em.persist(net);
    	em.getTransaction().commit();
    }

    public List<Net> findAll() {
        // Liefert alle gespeicherten Netze zurück
        return em.createQuery("SELECT n FROM Net n", Net.class).getResultList();
    }
    
    public Status findStatusByName(String name) {
        // Status anhand seines Namens finden
		TypedQuery<Status> query = em.createQuery(
				"SELECT s FROM Status s WHERE s.name = :name", Status.class);
		query.setParameter("name", name);
		return query.getSingleResult();    	
    }

    public List<Net> findByStatusName(String statusName) {
        // Sucht Netze mit einem bestimmten Statusnamen
        return em.createQuery(
                "SELECT n FROM Net n WHERE n.status.name = :name", Net.class)
                .setParameter("name", statusName)
                .getResultList();
    }
 
    public List<Net> findByStatusName(String... statusNames) {
        // Sucht Netze, deren Status in einer angegebenen Liste enthalten ist
        return em.createQuery(
                "SELECT n FROM Net n WHERE n.status.name IN :name", Net.class)
                .setParameter("name", Arrays.asList(statusNames))
                .getResultList();
    }
 
    public List<Net> findByStatusAndPerson(String statusName, Long recovererId) {
        // Filtert Netze nach Status und zugewiesener Bergungsperson
        return em.createQuery(
                "SELECT n FROM Net n WHERE n.status.name = :name AND n.recoverer.id = :recovererId", Net.class)
                .setParameter("name", statusName)
                .setParameter("recovererId", recovererId)
                .getResultList();
    }

    public Net findById(Long id) {
        // Gibt ein Netz anhand der ID zurück oder null, wenn nicht gefunden
        List<Net> result = em.createQuery(
                "SELECT n FROM Net n WHERE n.id = :id", Net.class)
                .setParameter("id", id)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}
