package de.iu.ghostnet.model;

import javax.persistence.*;

@Entity
@Table(name = "person_type")
public class PersonType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public PersonType() {}

    public PersonType(String name) {
        this.name = name;
    }

    // getters & setters
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}