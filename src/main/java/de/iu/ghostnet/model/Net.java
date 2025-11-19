package de.iu.ghostnet.model;

import javax.persistence.*;

@Entity
@Table(name = "nets")
public class Net {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int length;

    public Net() {}

    public Net(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
}
