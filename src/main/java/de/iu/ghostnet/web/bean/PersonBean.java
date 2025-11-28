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
	// View-Scoped Managed Bean zur Verwaltung von Personendaten in der UI
	private static final long serialVersionUID = 1L;

	// Service für Datenbankoperationen zu Personen
    @Inject
    private PersonService personService;

    // Aktuelle Person, die über das Formular erstellt oder bearbeitet wird
    private Person person = new Person();

    // getter & setter
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public String save() {
    	// Speichert die aktuelle Person in der Datenbank
        personService.save(person);
        // Nach erfolgreicher Speicherung wird das Formular zurückgesetzt
        person = new Person();
        return null;
    }

    public List<Person> getAllPersons() {
    	// Liefert alle Personen für Tabellenansichten oder Auswahllisten
        return personService.getAll();
    }
}
