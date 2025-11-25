package de.iu.ghostnet.dao;

import de.iu.ghostnet.model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
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

    public Person findById(Long id) {
        return em.find(Person.class, id);
    }

    public Person findByDetails(String firstName, String lastName, String phone) {
        return em.createQuery(
                "SELECT p FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName AND p.phone = :phone", 
                Person.class)
            .setParameter("firstName", firstName)
            .setParameter("lastName", lastName)
            .setParameter("phone", phone)
            .getSingleResult();
    }

}
