package de.iu.ghostnet.web.bean;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("phoneValidator")
public class PhoneValidator implements Validator<String> {
    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        if (value != null && !value.isEmpty()) {
            if (!value.matches("\\+?[0-9\\- ]{5,15}")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ungültige Telefonnummer", "Bitte geben Sie eine gültige Telefonnummer ein."));
            }
        }
    }
}
