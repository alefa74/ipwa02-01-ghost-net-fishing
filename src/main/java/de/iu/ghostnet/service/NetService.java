package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.NetDAO;
import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Person;
import de.iu.ghostnet.model.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class NetService {

    @Inject
    private NetDAO netDAO;
    @Inject
    private StatusService statusService;
    @Inject
    private SizeService sizeService;
    @Inject
    private PersonService personService;
    @Inject
    private PersonTypeService personTypeService;
    
    public void reportNewNet(Net net, Person reporter, boolean anonymous, Long selectedSizeId) {
        // Meldet ein neues Geisternetz und setzt Größe, Status, Reporter und Zeitstempel

    	// Größe setzen, falls ausgewählt
    	if (selectedSizeId != null) {
    		net.setSize(sizeService.findById(selectedSizeId));
    	}

    	// Initialstatus "GEMELDET" für neu eingegangene Meldungen
    	Status gemeldet = statusService.findByName("GEMELDET");
    	net.setStatus(gemeldet);
    	
    	// Reporter prüfen: anonym, leer oder bereits existierend?
		if (anonymous || (reporter == null) || (isEmpty(reporter.getFirstName()) && isEmpty(reporter.getLastName()) && isEmpty(reporter.getPhone()))) {
			net.setReporter(null); 
		} else {
			Person existing = personService.findByDetails(
								reporter.getFirstName(), 
								reporter.getLastName(), 
								reporter.getPhone());

			if (existing == null) {
			    // Neue Person anlegen, wenn noch nicht vorhanden
    			reporter.setPersonType(personTypeService.findByName("MELDER"));
				personService.save(reporter); 
			} else { 
				reporter = existing; 
			} 
			net.setReporter(reporter);
		}
    	
		// Zeitpunkt der Meldung setzen
    	net.setReportedAt(LocalDateTime.now());
    	
    	// Speichern
        netDAO.save(net);
    }

	private boolean isEmpty(String value) {
	    return value == null || value.trim().isEmpty();
	}

    public void assignNetForRecovery(Net net, Person recoverer) {
    	// Prüfen, ob ein Benutzer eingeloggt ist
        if (recoverer == null) {
            throw new IllegalArgumentException("Es ist kein Benutzer eingeloggt.");
        }

        // Net erneut laden, um parallele Änderungen zu vermeiden
        Net fresh = netDAO.findById(net.getId());

        if (fresh == null) {
            throw new IllegalArgumentException("Das ausgewählte Netz existiert nicht mehr.");
        }

        // Nur Netze im Status GEMELDET dürfen zugeordnet werden
        if (!"GEMELDET".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits von einer anderen Person zugeordnet."
            );
        }

        // Status auf BERGUNG_BEVORSTEHEND setzen und Person zuordnen
        Status bevorstehend = statusService.findByName("BERGUNG_BEVORSTEHEND");

        fresh.setStatus(bevorstehend);
        fresh.setRecoverer(recoverer);
        fresh.setAssignedAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    public void reportRecovery(Net net, Person recoverer) {
    	// Prüfen, ob ein Benutzer eingeloggt ist
        if (recoverer == null) {
            throw new IllegalArgumentException("Es ist kein Benutzer eingeloggt.");
        }

        // Net erneut laden, um parallele Änderungen zu vermeiden
        Net fresh = netDAO.findById(net.getId());

        if (fresh == null) {
            throw new IllegalArgumentException("Das ausgewählte Netz existiert nicht mehr.");
        }

        // Nur Netze im Status BERGUNG_BEVORSTEHEND können geborgen werden
        if (!"BERGUNG_BEVORSTEHEND".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits von einer anderen Person zugeordnet."
            );
        }
        
        // Sicherstellen, dass das Netz der aktuellen Person zugeordnet ist
        if (fresh.getRecoverer() == null || !fresh.getRecoverer().getId().equals(recoverer.getId())) {
            throw new IllegalArgumentException(
                    "Sie dürfen dieses Netz nicht bergen, da es Ihnen nicht zugeordnet wurde."
            );
        }

        // Status auf GEBORGEN setzen und Zeitpunkt erfassen
        Status geborgen = statusService.findByName("GEBORGEN");

        fresh.setStatus(geborgen);
        fresh.setRecoveredAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    public void cancelNet(Net net) {
        // Netz Netz erneut laden, um parallele Änderungen zu vermeiden
        Net fresh = netDAO.findById(net.getId());

        if (fresh == null) {
            throw new IllegalArgumentException("Das ausgewählte Netz existiert nicht mehr.");
        }

        // Prüfen, ob Netz noch stornierbar ist (nicht geborgen, nicht verschollen)
        if ("GEBORGEN".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits geborgen."
            );
        }
        if ("VERSCHOLLEN".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits als verschollen angemerkt."
            );
        }
        
        // Status auf VERSCHOLLEN setzen und Zeitpunkt erfassen
        Status verschollen = statusService.findByName("VERSCHOLLEN");

        fresh.setStatus(verschollen);
        fresh.setLostAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    
    public List<Net> getAll() {
        // Holt alle Netzen
        return netDAO.findAll();
    }

    public List<Net> getAllByStatus(String statusName) {
        return netDAO.findByStatusName(statusName);
    }
    
    public List<Net> getAllAssigned(String statusName, Long recovererId) {
    	return netDAO.findByStatusAndPerson(statusName, recovererId);
    }
    
    public List<Net> getAllAvailableNets() {
        // Liefert alle Netze, die entweder gemeldet oder bereits zur Bergung vorgesehen sind
    	return netDAO.findByStatusName("GEMELDET", "BERGUNG_BEVORSTEHEND");
    }
}
