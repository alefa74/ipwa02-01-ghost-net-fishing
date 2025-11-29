package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.PersonDAO;
import de.iu.ghostnet.model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import java.util.List;

@ApplicationScoped
public class PersonService {

    @Inject
    private PersonDAO personDAO;

    public void save(Person person) {
        // Speichert eine neue Person (Melder oder Berger)
        personDAO.save(person);
    }

    public List<Person> getAll() {
        // Holt alle Personen
        return personDAO.findAll();
    }
    
    public Person findById(Long id) {
        return personDAO.findById(id);
    }
    
    public Person findByDetails(String firstName, String lastName, String phone) {
        // Sucht eine Person eindeutig anhand ihrer Kontaktdaten
        try {
            return personDAO.findByDetails(firstName, lastName, phone);
        } catch (NoResultException e) {
            // Falls keine passende Person gefunden wurde
            return null;
        }
    }

}
