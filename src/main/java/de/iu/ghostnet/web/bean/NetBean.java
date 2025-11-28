package de.iu.ghostnet.web.bean;

import de.iu.ghostnet.model.Net;
import de.iu.ghostnet.model.Person;
import de.iu.ghostnet.model.Size;
import de.iu.ghostnet.model.Status;
import de.iu.ghostnet.model.PersonType;
import de.iu.ghostnet.service.NetService;
import de.iu.ghostnet.service.SizeService;
import de.iu.ghostnet.service.StatusService;
import de.iu.ghostnet.service.PersonTypeService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.primefaces.model.*;

@Named
@ViewScoped
public class NetBean implements Serializable {
	private static final long serialVersionUID = 1L;

    @Inject
    private NetService netService;
    @Inject
    private SizeService sizeService;
    @Inject
    private StatusService statusService;
    @Inject
    private PersonTypeService personTypeService;
    @Inject
    private LoginBean loginBean;
    
    private LazyDataModel<Net> allNets;
    private LazyDataModel<Net> reportedNets;
    private LazyDataModel<Net> myAssignedNets;
    private LazyDataModel<Net> availableNets;
    
    private Long selectedSizeId;    
    private Net net = new Net();
    private List<Status> allStatuses;
    private List<Size> allSizes;
    private List<PersonType> allPersonTypes;
    private List<Net> selectedNets = new ArrayList<>();
    
    private Person reporter = new Person();
    private boolean anonymous;

    
	@PostConstruct
	public void init() {
		allStatuses = statusService.getAllStatuses();
		allPersonTypes = personTypeService.getAllPersonTypes();
		allNets = NetView.ALL.createModel(netService);
		reportedNets = NetView.REPORTED.createModel(netService);
		myAssignedNets = NetView.MY_ASSIGNED.createModel(netService, loginBean);
		availableNets = NetView.AVAILABLE.createModel(netService);
	}

	private enum NetView {
        ALL("All Ghost Nets", 
        		(ns, lb) -> ns.getAll()),
        REPORTED("Reported Nets", 
        		(ns, lb) -> ns.getAllByStatus("GEMELDET")),
        MY_ASSIGNED("My Assigned Recoveries", 
        		(ns, lb) -> lb != null && lb.getPerson() != null
        		? ns.getAllAssigned("BERGUNG_BEVORSTEHEND", lb.getPerson().getId())
        		: Collections.emptyList()),
        AVAILABLE("Available Nets", 
        		(ns, lb) -> ns.getAllAvailableNets());

        private final String title;
        private final DataSupplier supplier;

        NetView(String title, DataSupplier supplier) {
            this.title = title;
            this.supplier = supplier;
        }

        public LazyDataModel<Net> createModel(NetService netService) {
            return createLazyModel(() -> supplier.get(netService, null));
        }

        public LazyDataModel<Net> createModel(NetService netService, LoginBean loginBean) {
            return createLazyModel(() -> supplier.get(netService, loginBean));
        }
    }
	
	@FunctionalInterface
	private interface DataSupplier {
        List<Net> get(NetService netService, LoginBean loginBean);
    }	
	
	// getter & setter
    public Net getNet() {
    	return net; 
    }
    
    public void setNet(Net net) {
    	this.net = net; 
    }
    
    public List<Status> getAllStatuses() {
		return allStatuses;
	}

	public Long getSelectedSizeId() {
		return selectedSizeId;
	}

	public void setSelectedSizeId(Long selectedSizeId) {
		this.selectedSizeId = selectedSizeId;
	}

	public LazyDataModel<Net> getReportedNets() {
		return reportedNets;
	}

	public LazyDataModel<Net> getMyAssignedNets() {
		return myAssignedNets;
	}

	public LazyDataModel<Net> getAvailableNets() {
		return availableNets;
	}

	public LazyDataModel<Net> getAllNets() {
		return allNets;
	}

	public Person getReporter() {
		return reporter;
	}

	public void setReporter(Person reporter) {
		this.reporter = reporter;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

    public List<Size> getAllSizes() {
    	if (allSizes == null) {
    		allSizes = sizeService.getAllSizes();
    		Collections.sort(allSizes, Comparator.comparing(Size::getId));
    	}
    	return allSizes;
    }

	public List<PersonType> getAllPersonTypes() {
		return allPersonTypes;
	}
	
	public List<Net> getSelectedNets() {
		return selectedNets;
	}

	public void setSelectedNets(List<Net> selectedNets) {
		this.selectedNets = selectedNets;
	}

	public void clearPersonalIfAnonym() {
	    if (anonymous) {
	        reporter.setFirstName(null);
	        reporter.setLastName(null);
	        reporter.setPhone(null);
	    }
	}
	
	private static LazyDataModel<Net> createLazyModel(Supplier<List<Net>> dataSupplier) {

        return new LazyDataModel<>() {
        	private List<Net> currentPage;
        	
        	@Override
        	public String getRowKey(Net net) {
        		return net!= null && net.getId() != null ? net.getId().toString() : null;
        	}

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                // Anzahl aller verfügbaren Netze.
                return dataSupplier.get().size();
            }

            @Override
            public Net getRowData(String rowKey) {
                if (currentPage == null || rowKey == null) return null;
                return currentPage.stream()
                        .filter(n -> n.getId() != null && n.getId().toString().equals(rowKey))
                        .findFirst()
                        .orElse(null);
            }
            
            @Override
            public List<Net> load(int first, int pageSize,
                                  Map<String, SortMeta> sortBy,
                                  Map<String, FilterMeta> filterBy) {
                // Gesamte Liste aller Netze aus Service laden
                List<Net> fullList = dataSupplier.get();

                // SORTIERUNG
                sortAndPaginate(fullList, sortBy);

                // PAGINIERUNG auf Basis von first + pageSize
                int end = Math.min(first + pageSize, fullList.size());

                // Wenn die Seite leer wäre:
                if (first > end) {
                	currentPage = Collections.emptyList();
                } else {
                	currentPage = fullList.subList(first, end);
                }

                // PrimeFaces mitteilen, wie viele Gesamtzeilen existieren.
                setRowCount(fullList.size());

                // Nur den relevanten Abschnitt zurückgeben.
                return currentPage;
            }
        };
    }
	
	private static void sortAndPaginate(List<Net> list, Map<String, SortMeta> sortBy) {
        if (sortBy != null && !sortBy.isEmpty()) {

            SortMeta meta = sortBy.values().iterator().next();

            String field = meta.getField();
            Comparator<Net> comparator = getComparator(field);

            // Sortierreihenfolge bestimmen
            if (meta.getOrder() == SortOrder.DESCENDING) {
                comparator = comparator.reversed();
            }

            list.sort(comparator);
        }	
	}

    private static Comparator<Net> getComparator(String field) {
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
    
	public String reportNewNet() {
		try {
			netService.reportNewNet(net, reporter, anonymous, selectedSizeId);

	    	// Erfolgsmeldung anzeigen
	        FacesContext.getCurrentInstance().addMessage(null, 
	        		new FacesMessage(
	    				FacesMessage.SEVERITY_INFO, 
	    				"Erfolgreich gespeichert", 
	    				"Das Geisternetz wurde erfolgreich erfasst. Danke für Ihre Meldung!"
	        		)
	        	);
	        
	        resetForm();
	        
	        allNets = NetView.ALL.createModel(netService);
	        reportedNets = NetView.REPORTED.createModel(netService);
		} catch (IllegalArgumentException ex) {
	        FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", ex.getMessage())
	            );
		}
        return null;
    }
	
	private void resetForm() {
	    net = new Net();
	    reporter = new Person();
	    anonymous = false;
	    selectedSizeId = null;
	}
	
	public void assignNetForRecovery() {
        if (selectedNets == null || selectedNets.isEmpty()) {
            FacesMessage msg = new FacesMessage(
            		FacesMessage.SEVERITY_WARN,
                    "Keine Netze ausgewählt", 
                    "Bitte wählen Sie mindestens ein Netz aus.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
    	
    	if (loginBean.getPerson() == null) {
    		FacesMessage msg = new FacesMessage(
    				FacesMessage.SEVERITY_ERROR,
    				"Fehler", 
    				"Sie sind nicht eingeloggt");
    		FacesContext.getCurrentInstance().addMessage(null, msg);
    		return;
    	}
    	
    	int successCount = 0;
    	Person recoverer = loginBean.getPerson();
    	
        for (Net net: selectedNets) {
        	try {
        		netService.assignNetForRecovery(net, recoverer);
        		successCount ++;
        	} catch (IllegalArgumentException ex) {
                // Fehler für dieses Netz anzeigen
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Fehler bei Netz " + net.getId(),
                                ex.getMessage()));
        	}
        }
        if (successCount > 0) {
	        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
	                "Erfolgreich gespeichert",
	                selectedNets.size() + " Geisternetze wurden zur Bergung vorgemerkt.");
	        FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        
        selectedNets.clear();
        allNets = NetView.ALL.createModel(netService);
        myAssignedNets = NetView.MY_ASSIGNED.createModel(netService, loginBean);
    }

	public void reportRecovery() {
        if (selectedNets == null || selectedNets.isEmpty()) {
            FacesMessage msg = new FacesMessage(
            		FacesMessage.SEVERITY_WARN,
                    "Keine Netze ausgewählt", 
                    "Bitte wählen Sie mindestens ein Netz aus.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
    	
    	if (loginBean.getPerson() == null) {
    		FacesMessage msg = new FacesMessage(
    				FacesMessage.SEVERITY_ERROR,
    				"Fehler", 
    				"Sie sind nicht eingeloggt");
    		FacesContext.getCurrentInstance().addMessage(null, msg);
    		return;
    	}
    	
    	int successCount = 0;
    	Person recoverer = loginBean.getPerson();
    	
        for (Net net: selectedNets) {
        	try {
        		netService.reportRecovery(net, recoverer);
        		successCount ++;
        	} catch (IllegalArgumentException ex) {
                // Fehler für dieses Netz anzeigen
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Fehler bei Netz " + net.getId(),
                                ex.getMessage()));
        	}
        }
        if (successCount > 0) {
	        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
	                "Erfolgreich gespeichert",
	                selectedNets.size() + " Geisternetze wurden als geborgen gemerkt.");
	        FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        
        selectedNets.clear();
        
        allNets = NetView.ALL.createModel(netService);
        myAssignedNets = NetView.MY_ASSIGNED.createModel(netService, loginBean);
    }
	
	public void cancelNet() {
        if (selectedNets == null || selectedNets.isEmpty()) {
            FacesMessage msg = new FacesMessage(
            		FacesMessage.SEVERITY_WARN,
                    "Keine Netze ausgewählt", 
                    "Bitte wählen Sie mindestens ein Netz aus.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
    	    	
    	int successCount = 0;
    	
        for (Net net: selectedNets) {
        	try {
        		netService.cancelNet(net);
        		successCount ++;
        	} catch (IllegalArgumentException ex) {
                // Fehler für dieses Netz anzeigen
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Fehler bei Netz " + net.getId(),
                                ex.getMessage()));
        	}
        }
        if (successCount > 0) {
	        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
	                "Erfolgreich gespeichert",
	                selectedNets.size() + " Geisternetze wurden als verschollen gemerkt.");
	        FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        
        selectedNets.clear();

        allNets = NetView.ALL.createModel(netService);
        reportedNets = NetView.REPORTED.createModel(netService);
    }
}
