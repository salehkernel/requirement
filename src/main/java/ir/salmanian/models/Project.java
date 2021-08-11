package ir.salmanian.models;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name")
    private String name;
    @ManyToOne
    private User creator;
    @OneToMany(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Requirement> requirementList;

    @Transient
    private boolean editable;

    public Long getId() {
        return id;
    }

    public Project setId(Long id) {
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
}
