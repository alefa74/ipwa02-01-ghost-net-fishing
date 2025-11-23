package de.iu.ghostnet.model;

import javax.persistence.*;

@Entity
@Table(name = "size")
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


	@Column(nullable = false, unique = true)
    private String name;

    public Size() {}

    // getters & setters
    public Size(String name) {
        this.name = name;
    }
    
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
