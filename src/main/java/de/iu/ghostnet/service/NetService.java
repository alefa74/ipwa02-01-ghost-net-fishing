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

    	// Größe setzen
    	if (selectedSizeId != null) {
    		net.setSize(sizeService.findById(selectedSizeId));
    	}

    	// Status setzen
    	Status gemeldet = statusService.findByName("GEMELDET");
    	net.setStatus(gemeldet);
    	
    	// Reporter setzen / validieren
		if (anonymous || (reporter == null) || (isEmpty(reporter.getFirstName()) && isEmpty(reporter.getLastName()) && isEmpty(reporter.getPhone()))) {
			net.setReporter(null); 
		} else {
			Person existing = personService.findByDetails(
								reporter.getFirstName(), 
								reporter.getLastName(), 
								reporter.getPhone());

			if (existing == null) {
    			reporter.setPersonType(personTypeService.findByName("MELDER"));
				personService.save(reporter); 
			} else { 
				reporter = existing; 
			} 
			net.setReporter(reporter);
		}
    	
		// Zeitstempel setzen
    	net.setReportedAt(LocalDateTime.now());
    	
    	// Speichern
        netDAO.save(net);
    }

	private boolean isEmpty(String value) {
	    return value == null || value.trim().isEmpty();
	}

    public void assignNetForRecovery(Net net, Person recoverer) {
        if (recoverer == null) {
            throw new IllegalArgumentException("Es ist kein Benutzer eingeloggt.");
        }

        // Net aus der DB erneut laden, um Race Conditions zu vermeiden
        Net fresh = netDAO.findById(net.getId());

        if (fresh == null) {
            throw new IllegalArgumentException("Das ausgewählte Netz existiert nicht mehr.");
        }

        // Darf nur GEMELDET sein
        if (!"GEMELDET".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits von einer anderen Person zugeordnet."
            );
        }

        // Alles OK? Dann zuordnen
        Status bevorstehend = statusService.findByName("BERGUNG_BEVORSTEHEND");

        fresh.setStatus(bevorstehend);
        fresh.setRecoverer(recoverer);
        fresh.setAssignedAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    public void reportRecovery(Net net, Person recoverer) {
        if (recoverer == null) {
            throw new IllegalArgumentException("Es ist kein Benutzer eingeloggt.");
        }

        // Net aus der DB erneut laden, um Race Conditions zu vermeiden
        Net fresh = netDAO.findById(net.getId());

        if (fresh == null) {
            throw new IllegalArgumentException("Das ausgewählte Netz existiert nicht mehr.");
        }

        // Darf nur BERGUNG_BEVORSTEHEND sein
        if (!"BERGUNG_BEVORSTEHEND".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits von einer anderen Person zugeordnet."
            );
        }
        
        // Darf nur von angemeldete zugeignet sein
        // TODO

        // Alles OK? Dann zuordnen
        Status geborgen = statusService.findByName("GEBORGEN");

        fresh.setStatus(geborgen);
        fresh.setRecoveredAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    public void cancelNet(Net net) {
        // Net aus der DB erneut laden, um Race Conditions zu vermeiden
        Net fresh = netDAO.findById(net.getId());

        if (fresh == null) {
            throw new IllegalArgumentException("Das ausgewählte Netz existiert nicht mehr.");
        }

        // Darf nicht GEBORGEN sein
        if ("GEBORGEN".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits geborgen."
            );
        }
        // Darf nicht VERSCHOLLEN sein
        if ("VERSCHOLLEN".equals(fresh.getStatus().getName())) {
            throw new IllegalArgumentException(
                    "Das Netz wurde bereits als verschollen angemerkt."
            );
        }
        
        // Alles OK? Dann verschollen
        Status verschollen = statusService.findByName("VERSCHOLLEN");

        fresh.setStatus(verschollen);
        fresh.setLostAt(LocalDateTime.now());   
        
    	netDAO.save(net);
    }

    
    public List<Net> getAll() {
        return netDAO.findAll();
    }

    public List<Net> getAllByStatus(String statusName) {
        return netDAO.findByStatusName(statusName);
    }
    
    public List<Net> getAllAssigned(String statusName, Long recovererId) {
    	return netDAO.findByStatusAndPerson(statusName, recovererId);
    }
    
    public List<Net> getAllAvailableNets() {
    	return netDAO.findByStatusName("GEMELDET", "BERGUNG_BEVORSTEHEND");
    }
}
