package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.PersonType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class PersonTypeDAO {

	// EntityManager für CRUD-Operationen auf PersonType
    @Inject
    private EntityManager em;

    public void save(PersonType type) {
        // Speichert einen neuen Personentyp
        em.getTransaction().begin();
        em.persist(type);
        em.getTransaction().commit();
    }

    public PersonType findByName(String name) {
        // Sucht einen Personentyp anhand seines Namens
        return em.createQuery("SELECT pt FROM PersonType pt WHERE pt.name = :name", PersonType.class)
                 .setParameter("name", name)
                 .getSingleResult();
    }

    public List<PersonType> findAll() {
        // Gibt alle vorhandenen Personentypen zurück
        return em.createQuery("SELECT pt FROM PersonType pt", PersonType.class).getResultList();
    }

    public PersonType findById(Long id) {
        // Findet einen Personentyp über die ID
        return em.find(PersonType.class, id);
    }
}
