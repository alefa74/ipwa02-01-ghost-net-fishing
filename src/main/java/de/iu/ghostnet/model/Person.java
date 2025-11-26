package de.iu.ghostnet.model;

import javax.persistence.*;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String phone;
    @ManyToOne
    @JoinColumn(name = "person_type_id")
    private PersonType personType;


    public Person() {}


	public Person(String firstName, String lastName, String phone, PersonType personType) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.personType = personType;
	}

	// getters & setters
	public Long getId() {
		return id;
	}
	
	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
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


	public PersonType getPersonType() {
		return personType;
	}


	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}

	@Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }


}
