package de.iu.ghostnet.web.bean;

import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Size;
import de.iu.ghostnet.model.Status;
import de.iu.ghostnet.service.NetService;
import de.iu.ghostnet.service.SizeService;
import de.iu.ghostnet.service.StatusService;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class NetBean implements Serializable {

    @Inject
    private NetService netService;
    @Inject
    private SizeService sizeService;
    @Inject
    private StatusService statusService;
    
    private LazyDataModel<Net> lazyNets;
    
    private Long selectedSizeId;    
    private Net net = new Net();
    private List<Status> allStatuses;
    private List<Size> allSizes;
    
	@PostConstruct
	public void init() {
		allStatuses = statusService.getAllStatuses();
		loadLazyModel();
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

    public LazyDataModel<Net> getLazyNets() {
		return lazyNets;
	}

    private void loadLazyModel() {

        lazyNets = new LazyDataModel<Net>() {

            @Override
            public int count(Map<String, org.primefaces.model.FilterMeta> filterBy) {
                // Anzahl aller verfügbaren Netze.
                return netService.getAll().size();
            }

            @Override
            public List<Net> load(int first, int pageSize,
                                  Map<String, org.primefaces.model.SortMeta> sortBy,
                                  Map<String, org.primefaces.model.FilterMeta> filterBy) {
                // Gesamte Liste aller Netze aus Service laden
                List<Net> list = netService.getAll();

                // SORTIERUNG
                if (sortBy != null && !sortBy.isEmpty()) {

                    org.primefaces.model.SortMeta meta = sortBy.values().iterator().next();

                    String field = meta.getField();
                    Comparator<Net> comparator = getComparator(field);

                    // Sortierreihenfolge bestimmen
                    if (meta.getOrder() == org.primefaces.model.SortOrder.DESCENDING) {
                        comparator = comparator.reversed();
                    }

                    list.sort(comparator);
                }

                // PAGINIERUNG auf Basis von first + pageSize
                int end = Math.min(first + pageSize, list.size());

                // PrimeFaces mitteilen, wie viele Gesamtzeilen existieren.
                this.setRowCount(list.size());

                // Wenn die Seite leer wäre:
                if (first > end) {
                    return Collections.emptyList();
                }

                // Nur den relevanten Abschnitt zurückgeben.
                return list.subList(first, end);
            }
        };
    }

    private Comparator<Net> getComparator(String field) {
        // Liefert je nach Feldnamen den passenden Comparator.
        switch (field) {
            case "id":
                return Comparator.comparing(
                        Net::getId,
                        Comparator.nullsLast(Long::compareTo)
                );

            case "latitude":
                return Comparator.comparing(
                        Net::getLatitude,
                        Comparator.nullsLast(Double::compareTo)
                );

            case "longitude":
                return Comparator.comparing(
                        Net::getLongitude,
                        Comparator.nullsLast(Double::compareTo)
                );

            case "size.name":
                return Comparator.comparing(
                        n -> n.getSize() != null ? n.getSize().getName() : "",
                        String::compareToIgnoreCase
                );

            case "status.name":
                return Comparator.comparing(
                        n -> n.getStatus() != null ? n.getStatus().getName() : "",
                        String::compareToIgnoreCase
                );

            case "reportedAt":
                return Comparator.comparing(
                        Net::getReportedAt,
                        Comparator.nullsLast(LocalDateTime::compareTo)
                );

            default:
                return Comparator.comparing(Net::getId);
        }
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
        
    	// Erfolgsmeldung anzeigen
        FacesContext.getCurrentInstance().addMessage(null, 
        		new FacesMessage(
    				FacesMessage.SEVERITY_INFO, 
    				"Erfolgreich gespeichert", 
    				"Das Geisternetz wurde erfolgreich erfasst. Danke für Ihre Meldung!"
        		)
        	);
        
        net = new Net();
        selectedSizeId = null;
        
        loadLazyModel();

        return null;
    }

    public List<Net> getAllNets() {
        return netService.getAll();
    }
    
    public List<Size> getAllSizes() {
    	if (allSizes == null) {
    		allSizes = sizeService.getAllSizes();
    		Collections.sort(allSizes, Comparator.comparing(Size::getId));
    	}
    	return allSizes;
    }
}
