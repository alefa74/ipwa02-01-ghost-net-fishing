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
    	List<Size> sizes = sizeDAO.findAll();
    	
    	if (sizes.isEmpty()) {
            sizeDAO.save(new Size("XS"));
            sizeDAO.save(new Size("S"));
            sizeDAO.save(new Size("M"));
            sizeDAO.save(new Size("L"));
            sizeDAO.save(new Size("XL"));
            sizes = sizeDAO.findAll(); 
    	}
    	
        return sizes;
    }

    public Size findById(Long id) {
        return sizeDAO.findById(id);
    }
}
