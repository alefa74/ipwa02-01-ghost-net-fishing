package de.iu.ghostnet.web.bean;

import de.iu.ghostnet.model.Person;
import de.iu.ghostnet.service.PersonService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class PersonBean implements Serializable {
	private static final long serialVersionUID = 1L;

    @Inject
    private PersonService personService;

    private Person person = new Person();

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public String save() {
        personService.save(person);
        person = new Person();
        return null;
    }

    public List<Person> getAllPersons() {
        return personService.getAll();
    }
}
