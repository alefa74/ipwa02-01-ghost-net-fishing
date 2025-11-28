package de.iu.ghostnet.web.bean;

import de.iu.ghostnet.dao.PersonDAO;
import de.iu.ghostnet.model.Person;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {
	// Session-Bean für Login-Status und Benutzeridentität (Berger)
	private static final long serialVersionUID = 1L;

    @Inject
    private PersonDAO personDAO;
    
    private String firstName;
    private String lastName;
    private String phone;
    
    private Person person;
    private boolean loggedIn = false;
    private String errorMessage;
    
    private String redirectTo;

    
    // getters & setters
 	public Person getPerson() {
 		return person;
 	}
 	
    public boolean isLoggedIn() {
        return loggedIn;
    } 

    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}

	public String login() {
        try {
        	// Benutzer anhand von Vorname, Nachname und Telefon suchen
           person = personDAO.findByDetails(firstName, lastName, phone);

           // Prüfen, ob gefundene Person die Rolle BERGER besitzt
           if (person != null && person.getPersonType().getName().equals("BERGER")) {
        	   loggedIn = true;
        	// Weiterleitung nach Login, entweder zur angefragten Seite oder zur Startseite
        	   if (redirectTo == null || redirectTo.isEmpty()) {
        		   return "/index?faces-redirect=true";
        	   }
        	   return redirectTo + "?faces-redirect=true";
           	}
    	} catch (Exception e) {
            // ignorieren, Fehlermeldung wird unten angezeigt
        }

        // Fehlversuch: Fehlermeldung für den Benutzer anzeigen
        loggedIn = false;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Login fehlgeschlagen", "Ungültige Daten oder kein bergender Benutzer"));
        return null;
    }

    public String logout() {
    	// Sitzung vollständig invalidieren und Login-Daten zurücksetzen
        loggedIn = false;
        person = null;
        firstName = null;
        lastName = null;
        phone = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index?faces-redirect=true";
    }
    
    public String getFullName() {
    	// Hilfsmethode zur Anzeige des vollständigen Namens in der UI
        if (person != null) {
            return person.getFirstName() + " " + person.getLastName();
        }
        return "";    	
    }

}
