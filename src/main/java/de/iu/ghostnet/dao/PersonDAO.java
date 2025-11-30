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
        // Persistiert eine neue Person (Melder oder Bergungspersonal)
        em.getTransaction().begin();
        em.persist(person);
        em.getTransaction().commit();
    }

    public List<Person> findAll() {
        // Liefert alle Personen aus der Datenbank
        return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }

    public Person findById(Long id) {
        // Findet eine Person über ihre ID
        return em.find(Person.class, id);
    }

    public Person findByDetails(String firstName, String lastName, String phone) {
        // Sucht eine Person anhand von Vorname, Nachname und Telefonnummer
        return em.createQuery(
                "SELECT p FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName AND p.phone = :phone", 
                Person.class)
            .setParameter("firstName", firstName)
            .setParameter("lastName", lastName)
            .setParameter("phone", phone)
            .getSingleResult();
    }

    public List<Person> findAllRecoverers() {
        // Sucht Personen anhand von Type
        return em.createQuery(
        		"SELECT p FROM Person p WHERE p.personType.name = :name", Person.class)
        	.setParameter("name", "BERGER")
        	.getResultList();
    }
}
