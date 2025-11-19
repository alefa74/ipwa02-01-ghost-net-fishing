package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.NetDAO;
import de.iu.ghostnet.model.Net;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class NetService {

    @Inject
    private NetDAO netDAO;

    public void save(Net net) {
        netDAO.save(net);
    }

    public List<Net> getAll() {
        return netDAO.findAll();
    }
}
