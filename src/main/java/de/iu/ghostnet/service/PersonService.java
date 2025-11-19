package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.PersonDAO;
import de.iu.ghostnet.model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PersonService {

    @Inject
    private PersonDAO personDAO;

    public void save(Person person) {
        personDAO.save(person);
    }

    public List<Person> getAll() {
        return personDAO.findAll();
    }
}
