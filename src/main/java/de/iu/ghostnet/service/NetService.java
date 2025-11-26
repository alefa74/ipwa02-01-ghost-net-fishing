package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.NetDAO;
import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class NetService {

    @Inject
    private NetDAO netDAO;

    public void save(Net net) {
    	Status gemeldet = netDAO.findStatusByName("GEMELDET");
    	net.setStatus(gemeldet);
    	
    	net.setReportedAt(LocalDateTime.now());
    	
        netDAO.save(net);
    }
    
    public void updateNet(Net net) {
    	netDAO.save(net);
    }

    public List<Net> getAll() {
        return netDAO.findAll();
    }

    public List<Net> getAllByStatus(String statusName) {
        return netDAO.findByStatusName(statusName);
    }
}
