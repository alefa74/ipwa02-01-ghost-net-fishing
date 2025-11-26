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

import org.primefaces.model.LazyDataModel;

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
    
    private LazyDataModel<Net> lazyNets;
    private LazyDataModel<Net> gemeldetLazyNets;
    private LazyDataModel<Net> angemeldetLazyNets;
    
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
		loadLazyModel();
		loadGemeldetLazyModel();
		loadAngemeldetLazyModel();
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


	public LazyDataModel<Net> getLazyNets() {
		return lazyNets;
	}

	public LazyDataModel<Net> getGemeldetLazyNets() {
		return gemeldetLazyNets;
	}

    public LazyDataModel<Net> getAngemeldetLazyNets() {
		return angemeldetLazyNets;
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
	
	private void loadLazyModel() {

        lazyNets = new LazyDataModel<Net>() {

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                // Anzahl aller verfügbaren Netze.
                return netService.getAll().size();
            }

            @Override
            public List<Net> load(int first, int pageSize,
                                  Map<String, SortMeta> sortBy,
                                  Map<String, FilterMeta> filterBy) {
                // Gesamte Liste aller Netze aus Service laden
                List<Net> list = netService.getAll();

                // SORTIERUNG
                sortAndPaginate(list, first, pageSize, sortBy);

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
	
	private void loadGemeldetLazyModel() {
		gemeldetLazyNets = new LazyDataModel<Net>() {
			
			private List<Net> page;

			@Override
	        public String getRowKey(Net net) {
	            return net != null && net.getId() != null
	                    ? net.getId().toString()
	                    : null;
	        }
			
			@Override
	        public int count(Map<String, FilterMeta> filterBy) {
	            return netService.getAllByStatus("GEMELDET").size();
	        }
			
	        @Override
	        public Net getRowData(String rowKey) {        	
	            if (rowKey == null || page == null)
	                return null;

	            for (Net n : page) {
	                if (n.getId() != null && n.getId().toString().equals(rowKey)) {
	                    return n;
	                }
	            }
	            return null;
	        }


	        @Override
	        public List<Net> load(int first, int pageSize,
	                              Map<String, SortMeta> sortBy,
	                              Map<String, FilterMeta> filterBy) {
	        	// Nur Netze mit Status Gemeldet
	            List<Net> list = netService.getAllByStatus("GEMELDET");
	            
                // SORTIERUNG
                sortAndPaginate(list, first, pageSize, sortBy);

                // PAGINIERUNG auf Basis von first + pageSize
                int end = Math.min(first + pageSize, list.size());

                // PrimeFaces mitteilen, wie viele Gesamtzeilen existieren.
                this.setRowCount(list.size());

                // Wenn die Seite leer wäre:
                if (first > end) {
                    page = Collections.emptyList();
                } else {
	                // Nur den relevanten Abschnitt zurückgeben.
	                page =  list.subList(first, end);
                }
                
                return page;
	        }
	        
	    };
	}
	
	private void loadAngemeldetLazyModel() {
		angemeldetLazyNets = new LazyDataModel<Net>() {
			
			private List<Net> page;

			@Override
	        public String getRowKey(Net net) {
	            return net != null && net.getId() != null
	                    ? net.getId().toString()
	                    : null;
	        }
			
			@Override
	        public int count(Map<String, FilterMeta> filterBy) {
	            return netService.getAllAssigned("BERGUNG_BEVORSTEHEND",loginBean.getPerson().getId()).size();
	        }
			
	        @Override
	        public Net getRowData(String rowKey) {        	
	            if (rowKey == null || page == null)
	                return null;

	            for (Net n : page) {
	                if (n.getId() != null && n.getId().toString().equals(rowKey)) {
	                    return n;
	                }
	            }
	            return null;
	        }


	        @Override
	        public List<Net> load(int first, int pageSize,
	                              Map<String, SortMeta> sortBy,
	                              Map<String, FilterMeta> filterBy) {
	        	// Nur Netze mit Status Bergun_bevorstehend und eingetragen als logged in Person
	            List<Net> list = netService.getAllAssigned("BERGUNG_BEVORSTEHEND",loginBean.getPerson().getId());
	            
                // SORTIERUNG
                sortAndPaginate(list, first, pageSize, sortBy);

                // PAGINIERUNG auf Basis von first + pageSize
                int end = Math.min(first + pageSize, list.size());

                // PrimeFaces mitteilen, wie viele Gesamtzeilen existieren.
                this.setRowCount(list.size());

                // Wenn die Seite leer wäre:
                if (first > end) {
                    page = Collections.emptyList();
                } else {
	                // Nur den relevanten Abschnitt zurückgeben.
	                page =  list.subList(first, end);
                }
                
                return page;
	        }
	        
	    };
	}
	
	private void sortAndPaginate(List<Net> list, int first, int pageSize, Map<String, SortMeta> sortBy) {
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
	        loadLazyModel();
	        
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
        gemeldetLazyNets.load(0, 10, null, null);  
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
        gemeldetLazyNets.load(0, 10, null, null);  
    }
}
