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

	// Größe des Netzes
    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    // Aktueller Status des Netzes
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    // Person, die das Netz ursprünglich gemeldet hat
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private Person reporter;

    // Person, die für die Bergung zugewiesen wurde
    @ManyToOne
    @JoinColumn(name = "recoverer_id")
    private Person recoverer;

    // Person, die meldet, dass das Netz verschollen ist
    @ManyToOne
    @JoinColumn(name = "missing_reporter_id")
    private Person missingReporter;

    private LocalDateTime reportedAt;		// Zeitpunkt der Meldung
    private LocalDateTime assignedAt;		// Zeitpunkt der Zuweisung an eine Bergungsperson
    private LocalDateTime recoveredAt;		// Zeitpunkt der Bergung
    private LocalDateTime lostAt;			// Zeitpunkt, ab dem das Netz als verloren gilt
    
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
	public Person getMissingReporter() {
		return missingReporter;
	}

	public void setMissingReporter(Person missingReporter) {
		this.missingReporter = missingReporter;
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
	
	/**
	 * Gibt das Meldedatum im Format "dd.MM.yyyy" zurück.
	 * Falls kein Datum vorhanden ist, wird ein Bindestrich ausgegeben.
	 */
	public String getFormattedReportedAt() {
        if (reportedAt == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return reportedAt.format(formatter);
    }	

}
