package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.StatusDAO;
import de.iu.ghostnet.model.Status;
import de.iu.ghostnet.model.Status.StatusType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class StatusService {

    @Inject
    private StatusDAO statusDAO;

    public List<Status> getAllStatuses() {
        // Holt alle Statuswerte aus der DB und initialisiert sie beim ersten Start
        List<Status> statuses = statusDAO.findAll();

        // INIT
        if (statuses.isEmpty()) {
            // Initialbefüllung der Status-Tabelle
            statusDAO.save(new Status(StatusType.GEMELDET));
            statusDAO.save(new Status(StatusType.BERGUNG_BEVORSTEHEND));
            statusDAO.save(new Status(StatusType.GEBORGEN));
            statusDAO.save(new Status(StatusType.VERSCHOLLEN));
            
            System.out.println("[INIT] Status angelegt: GEMELDET, BERGUNG_BEVORSTEHEND, GEBORGEN, VERSCHOLLEN");
            statuses = statusDAO.findAll();
        }

        return statuses;
    }

    public Status findById(Long id) {
        return statusDAO.findById(id);
    }

    public Status findByName(StatusType name) {
        return statusDAO.findByName(name);
    }
}
