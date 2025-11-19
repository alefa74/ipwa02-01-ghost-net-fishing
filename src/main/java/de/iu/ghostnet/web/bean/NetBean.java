package de.iu.ghostnet.web.bean;

import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.service.NetService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class NetBean implements Serializable {

    @Inject
    private NetService netService;

    private Net net = new Net();

    public Net getNet() { return net; }
    public void setNet(Net net) { this.net = net; }

    public String save() {
        netService.save(net);
        net = new Net();
        return null;
    }

    public List<Net> getAllNets() {
        return netService.getAll();
    }
}
