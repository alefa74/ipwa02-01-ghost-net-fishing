package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.NetDAO;
import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Person;
import de.iu.ghostnet.model.Status;
import de.iu.ghostnet.model.Status.StatusType;

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

    	// Initialstatus GEMELDET für neu eingegangene Meldungen
    	Status gemeldet = statusService.findByName(StatusType.GEMELDET);
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
        if (Status.StatusType.GEMELDET != fresh.getStatus().getName()) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits von einer anderen Person zugeordnet."
            );
        }

        // Status auf BERGUNG_BEVORSTEHEND setzen und Person zuordnen
        Status bevorstehend = statusService.findByName(StatusType.BERGUNG_BEVORSTEHEND);

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
        if (Status.StatusType.BERGUNG_BEVORSTEHEND != fresh.getStatus().getName()) {
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
        Status geborgen = statusService.findByName(StatusType.GEBORGEN);

        fresh.setStatus(geborgen);
        fresh.setRecoveredAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    public void cancelNet(Net net, Person recoverer) {
    	// Prüfen, ob ein Benutzer eingeloggt ist
        if ((recoverer == null) || (isEmpty(recoverer.getFirstName()) && isEmpty(recoverer.getLastName()) && isEmpty(recoverer.getPhone()))) {
            throw new IllegalArgumentException("Es ist kein Benutzer eingeloggt.");
        }

        // Netz Netz erneut laden, um parallele Änderungen zu vermeiden
        Net fresh = netDAO.findById(net.getId());

        if (fresh == null) {
            throw new IllegalArgumentException("Das ausgewählte Netz existiert nicht mehr.");
        }

        // Prüfen, ob Netz noch stornierbar ist (nicht geborgen, nicht verschollen)
        if (StatusType.GEBORGEN == fresh.getStatus().getName()) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits geborgen."
            );
        }
        if (StatusType.VERSCHOLLEN == fresh.getStatus().getName()) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits als verschollen angemerkt."
            );
        }
        
        // Status auf VERSCHOLLEN setzen und Zeitpunkt erfassen
        Status verschollen = statusService.findByName(StatusType.VERSCHOLLEN);

    	// Reporter prüfen: bereits existierend?
		Person existing = personService.findByDetails(
							recoverer.getFirstName(), 
							recoverer.getLastName(), 
							recoverer.getPhone());

		if (existing == null) {
			// Neue Person anlegen, wenn noch nicht vorhanden
			recoverer.setPersonType(personTypeService.findByName("WEGMELDER"));
			personService.save(recoverer); 
		} else { 
			recoverer = existing; 
		} 
		net.setMissingReporter(recoverer);

		fresh.setStatus(verschollen);
        fresh.setLostAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    
    public List<Net> getAll() {
        // Holt alle Netzen
        return netDAO.findAll();
    }

    public List<Net> getAllByStatus(StatusType statusName) {
        return netDAO.findByStatusName(statusName);
    }
    
    public List<Net> getAllAssigned(StatusType statusName, Long recovererId) {
    	return netDAO.findByStatusAndPerson(statusName, recovererId);
    }
    
    public List<Net> getAllAvailableNets() {
        // Liefert alle Netze, die entweder gemeldet oder bereits zur Bergung vorgesehen sind
    	return netDAO.findByStatusName(StatusType.GEMELDET);
    }
}
