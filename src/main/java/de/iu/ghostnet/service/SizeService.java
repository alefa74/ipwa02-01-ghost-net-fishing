package de.iu.ghostnet.service;

import de.iu.ghostnet.dao.SizeDAO;
import de.iu.ghostnet.model.Size;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SizeService {

    @Inject
    private SizeDAO sizeDAO;

    public List<Size> getAllSizes() {
        // Holt alle Netzgrößen; legt Standardgrößen an, falls Tabelle leer ist
    	List<Size> sizes = sizeDAO.findAll();
    	
    	if (sizes.isEmpty()) {
    		// Standardgrößen zur Erstinitialisierung
            sizeDAO.save(new Size("XS"));
            sizeDAO.save(new Size("S"));
            sizeDAO.save(new Size("M"));
            sizeDAO.save(new Size("L"));
            sizeDAO.save(new Size("XL"));
            
            System.out.println("[INIT] Größen angelegt: XS, S, M, L, XL");
            sizes = sizeDAO.findAll(); 
    	}
    	
        return sizes;
    }

    public Size findById(Long id) {
        return sizeDAO.findById(id);
    }
}
