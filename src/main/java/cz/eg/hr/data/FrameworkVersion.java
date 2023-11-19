package cz.eg.hr.data;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;

@Entity
public class FrameworkVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private JavascriptFramework framework;

    @Column(nullable = false, length = 10)
    private String version;

    @Temporal(TemporalType.DATE)
    private LocalDate deprecationDate;

    public FrameworkVersion() {
    }

    public FrameworkVersion(String version, LocalDate deprecationDate) {
        this.version = version;
        this.deprecationDate = deprecationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JavascriptFramework getFramework() {
        return framework;
    }

    public void setFramework(JavascriptFramework framework) {
        this.framework = framework;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getDeprecationDate() {
        return deprecationDate;
    }

    public void setDeprecationDate(LocalDate deprecationDate) {
        this.deprecationDate = deprecationDate;
    }

    @Override
    public String toString() {
        return framework.getName() + " " + version;
    }
}