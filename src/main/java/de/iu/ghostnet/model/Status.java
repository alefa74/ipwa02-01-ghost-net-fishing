package de.iu.ghostnet.model;

import javax.persistence.*;

@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name des Status, muss eindeutig sein
    @Column(nullable = false, unique = true)
    private String name;

    public Status() {}

    public Status(String name) {
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