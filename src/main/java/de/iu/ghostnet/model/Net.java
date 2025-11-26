package de.iu.ghostnet.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

@Entity
@Table(name = "net")
public class Net {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private Person reporter;

    @ManyToOne
    @JoinColumn(name = "recoverer_id")
    private Person recoverer;

    private LocalDateTime reportedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime recoveredAt;
    private LocalDateTime lostAt;
    
    public Net () {
    }
    
    public Net(Double latitude, Double longitude, Size size, Person reporter) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.size = size;
		this.reporter = reporter;
	}
    
	// getter & setter
    public Long getId() {
    	return id;
    }
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Size getSize() {
		return size;
	}
	public void setSize(Size size) {
		this.size = size;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Person getReporter() {
		return reporter;
	}
	public void setReporter(Person reporter) {
		this.reporter = reporter;
	}
	public Person getRecoverer() {
		return recoverer;
	}
	public void setRecoverer(Person recoverer) {
		this.recoverer = recoverer;
	}
	public LocalDateTime getReportedAt() {
		return reportedAt;
	}
	public void setReportedAt(LocalDateTime reportedAt) {
		this.reportedAt = reportedAt;
	}
	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}
	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}
	public LocalDateTime getRecoveredAt() {
		return recoveredAt;
	}
	public void setRecoveredAt(LocalDateTime recoveredAt) {
		this.recoveredAt = recoveredAt;
	}
	public LocalDateTime getLostAt() {
		return lostAt;
	}
	public void setLostAt(LocalDateTime lostAt) {
		this.lostAt = lostAt;
	}
	
	public String getFormattedReportedAt() {
        if (reportedAt == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return reportedAt.format(formatter);
    }	

}
