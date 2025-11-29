package de.iu.ghostnet.model;

import javax.persistence.*;

@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Das Enum, das den tatsächlichen Status repräsentiert
    public enum StatusType {
        GEMELDET,
        BERGUNG_BEVORSTEHEND,
        GEBORGEN,
        VERSCHOLLEN
    }

    // Name des Status, muss eindeutig sein
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private StatusType name;

    public Status() {}

    public Status(StatusType name) {
        this.name = name;
    }

    // getters & setters
	public Long getId() {
		return id;
	}

	public StatusType getName() {
		return name;
	}

	public void setName(StatusType name) {
		this.name = name;
	}

}