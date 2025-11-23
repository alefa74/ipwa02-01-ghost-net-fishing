package de.iu.ghostnet.web.bean;

import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Size;
import de.iu.ghostnet.model.Status;
import de.iu.ghostnet.service.NetService;
import de.iu.ghostnet.service.SizeService;
import de.iu.ghostnet.service.StatusService;

import javax.annotation.PostConstruct;
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
    @Inject
    private SizeService sizeService;
    @Inject
    private StatusService statusService;
    
    
    private Long selectedSizeId;    
    private Net net = new Net();
    private List<Status> allStatuses;
	
	@PostConstruct
	public void init() {
		allStatuses = statusService.getAllStatuses();
	}
	
	// getter & setter
    public Net getNet() {
    	return net; 
    }
    public void setNet(Net net) {
    	this.net = net; 
    }
    public Long getSelectedSizeId() {
		return selectedSizeId;
	}
	public void setSelectedSizeId(Long selectedSizeId) {
		this.selectedSizeId = selectedSizeId;
	}

	public String save() {
    	System.out.println("save called. Id = " + selectedSizeId);
    	
    	if (selectedSizeId != null) {
    		net.setSize(sizeService.findById(selectedSizeId));
    	} else {
    		System.out.println("No size selected");
    	}
    	
    	net.setStatus(statusService.findByName("GEMELDET"));
    	
        netService.save(net);
        
        net = new Net();
        selectedSizeId = null;
        return null;
    }

    public List<Net> getAllNets() {
        return netService.getAll();
    }
    
    public List<Size> getAllSizes() {
    	System.out.println("getAllSizes called");
    	return sizeService.getAllSizes();
    }
}
