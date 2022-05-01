package cch.com.example.skaffold.entity;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tutorials")
public class Tutorial {
    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID id;

    @Column(nullable = false, name = "title")
	private String title;
	
    @Column(nullable = false, name = "description")
	private String description;
	
    @Column(nullable = false, name = "published")
	private boolean published;


    public Tutorial() {
    }

    public Tutorial(UUID id, String title, String description, boolean published) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.published = published;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublished() {
        return this.published;
    }

    public boolean getPublished() {
        return this.published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Tutorial)) {
            return false;
        }
        Tutorial tutorial = (Tutorial) o;
        return Objects.equals(id, tutorial.id) && Objects.equals(title, tutorial.title) && Objects.equals(description, tutorial.description) && published == tutorial.published;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, published);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", published='" + isPublished() + "'" +
            "}";
    }

}
