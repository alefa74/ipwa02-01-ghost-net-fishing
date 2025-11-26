package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.StatusDAO;
import de.iu.ghostnet.model.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class StatusService {

    @Inject
    private StatusDAO statusDAO;

    public List<Status> getAllStatuses() {
        List<Status> statuses = statusDAO.findAll();

        // INIT
        if (statuses.isEmpty()) {
            statusDAO.save(new Status("GEMELDET"));
            statusDAO.save(new Status("BERGUNG_BEVORSTEHEND"));
            statusDAO.save(new Status("GEBORGEN"));
            statusDAO.save(new Status("VERSCHOLLEN"));
            statuses = statusDAO.findAll();
        }

        return statuses;
    }

    public Status findById(Long id) {
        return statusDAO.findById(id);
    }

    public Status findByName(String name) {
        return statusDAO.findByName(name);
    }
}
