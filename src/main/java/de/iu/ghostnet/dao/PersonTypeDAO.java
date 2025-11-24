package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.PersonType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class PersonTypeDAO {

    @Inject
    private EntityManager em;

    public void save(PersonType type) {
        em.getTransaction().begin();
        em.persist(type);
        em.getTransaction().commit();
    }

    public PersonType findByName(String name) {
        return em.createQuery("SELECT pt FROM PersonType pt WHERE pt.name = :name", PersonType.class)
                 .setParameter("name", name)
                 .getSingleResult();
    }

    public List<PersonType> findAll() {
        return em.createQuery("SELECT pt FROM PersonType pt", PersonType.class).getResultList();
    }

    public PersonType findById(Long id) {
        return em.find(PersonType.class, id);
    }
}
