package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Person;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class PersonDAO {

    @Inject
    private EntityManager em;

    public void save(Person person) {
        em.getTransaction().begin();
        em.persist(person);
        em.getTransaction().commit();
    }

    public List<Person> findAll() {
        return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }
}
