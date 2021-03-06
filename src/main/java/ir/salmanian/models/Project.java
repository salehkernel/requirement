package ir.salmanian.models;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * One of entity classes used in this application.
 * Some fields of this class are used for creating xml file.
 */
@Entity
@Table(name = "projects")
@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.NONE)
public class Project {

    @Id
    @Type(type = "uuid-char")
    @XmlAttribute(name = "project-id")
    private UUID id;
    @Column(name = "name")
    @XmlElement(name = "name")
    private String name;
    @ManyToOne
    private User creator;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @XmlElementWrapper(name = "requirements")
    @XmlElement(name = "requirement")
    private List<Requirement> requirementList;
    @Column(name = "number")
    @XmlElement(name = "number")
    private Integer number;
    @Column(name = "last_requirement_number")
    @XmlElement(name = "last-requirement-number")
    private Integer lastRequirementNumber;

    @Transient
    private boolean editable;

    public UUID getId() {
        return id;
    }

    public Project setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Project setName(String name) {
        this.name = name;
        return this;
    }

    public User getCreator() {
        return creator;
    }

    public Project setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public Project setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
        return this;
    }

    public boolean isEditable() {
        return editable;
    }

    public Project setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Project setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public Integer getLastRequirementNumber() {
        return lastRequirementNumber;
    }

    public Project setLastRequirementNumber(Integer lastRequirementNumber) {
        this.lastRequirementNumber = lastRequirementNumber;
        return this;
    }
}
