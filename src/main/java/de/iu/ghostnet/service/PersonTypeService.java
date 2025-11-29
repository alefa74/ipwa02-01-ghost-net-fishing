package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.PersonTypeDAO;
import de.iu.ghostnet.model.PersonType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PersonTypeService {

    @Inject
    private PersonTypeDAO personTypeDAO;

    public List<PersonType> getAllPersonTypes() {
        // Holt alle Personentypen; legt Standardtypen an, falls Tabelle leer ist
        List<PersonType> types = personTypeDAO.findAll();
        if (types.isEmpty()) {
        	// Standardrollen für Personen
            personTypeDAO.save(new PersonType("MELDER"));
            personTypeDAO.save(new PersonType("BERGER"));
            types = personTypeDAO.findAll();
        }
        return types;
    }

    public PersonType findByName(String name) {
        return personTypeDAO.findByName(name);
    }

    public PersonType findById(Long id) {
        return personTypeDAO.findById(id);
    }

    public void save(PersonType type) {
        // Speichert einen neuen Personentyp
        personTypeDAO.save(type);
    }
}
