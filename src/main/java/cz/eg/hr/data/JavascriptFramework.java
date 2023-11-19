package cz.eg.hr.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Entity
@Indexed
public class JavascriptFramework {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false, length = 30)
    @FullTextField
    private String name;

    @Column(nullable = true, length = 1000)
    @FullTextField
    private String description;

    @NotNull
    @Min(value = 1, message = "must be between 1 and 5")
    @Max(value = 5, message = "must be between 1 and 5")
    @Column(nullable = false)
    private Integer rating;

    @OneToMany(mappedBy = "framework", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FrameworkVersion> versions = new ArrayList<>();

    public JavascriptFramework() {
    }

    public JavascriptFramework(String name, String description, Integer rating) {
        this.name = name;
        this.description = description;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<FrameworkVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<FrameworkVersion> versions) {
        this.versions = versions;
    }

    public void addVersion(FrameworkVersion version) {
        versions.add(version);
        version.setFramework(this);
    }

    public void removeVersion(FrameworkVersion version) {
        versions.remove(version);
        version.setFramework(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
