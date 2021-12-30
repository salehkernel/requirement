package ir.salmanian.models;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Main entity class used in this application.
 * Some of field of this class are used in creating xml file.
 */
@Entity
@Table(name = "requirements")
@XmlRootElement(name = "requirement")
@XmlAccessorType(XmlAccessType.NONE)
public class Requirement {

    @Id
    @Type(type = "uuid-char")
    @XmlAttribute(name = "requirement-id")
    private UUID id;
    @Column(name = "prefix")
    @XmlElement(name = "prefix")
    private String prefix;
    @Column(name = "title")
    @XmlElement(name = "title")
    private String title;
    @Column(name = "changes")
    @XmlElement(name = "change")
    private Change changes;
    @Column(name = "priority")
    @XmlElement(name = "priority")
    private Priority priority;
    @Column(name = "evaluation_method")
    @XmlElement(name = "evaluation-method")
    private EvaluationMethod evaluationMethod;
    @Column(name = "evaluation_status")
    @XmlElement(name = "evaluation-status")
    private EvaluationStatus evaluationStatus;
    @Column(name = "quality_factor")
    @XmlElement(name = "quality-factor")
    private QualityFactor qualityFactor;
    @Column(name = "requirement_type")
    @XmlElement(name = "requirement-type")
    private RequirementType requirementType;
    @Column(name = "review_status")
    @XmlElement(name = "review-status")
    private ReviewStatus reviewStatus;
    @ManyToOne
    private Project project;
    @Column(name = "attachment", length = 4000)
    @Lob
    @XmlElement(name = "attachment")
    private String attachment;
    @Column(name = "level")
    @ColumnDefault(value = "1")
    @XmlElement(name = "level")
    private Integer level;
    @Column(name = "number")
    @XmlElement(name = "number")
    private Integer number;

    @ManyToMany(/*mappedBy = "children", */fetch = FetchType.EAGER)
    /*@JoinTable(name = "requirements_parents",
            joinColumns = @JoinColumn(name = "requirement_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id"),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT)
    )*/
//    @OnDelete(action = OnDeleteAction.CASCADE)
    @XmlElementWrapper(name = "parents")
    @XmlElement(name = "requirement")
    private List<Requirement> parents = new ArrayList<>();

   /* @ManyToMany(*//*mappedBy = "parents",*//* fetch = FetchType.EAGER, cascade = {})
    *//*@JoinTable(name = "requirements_parents",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "requirement_id")
    )*//*
    @JoinTable(name = "requirements_parents",
            joinColumns = @JoinColumn(name = "requirement_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    @XmlElementWrapper(name = "children")
    @XmlElement(name = "requirement")
    private List<Requirement> children = new ArrayList<>();*/

    public UUID getId() {
        return id;
    }

    public Requirement setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Requirement setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Requirement setTitle(String title) {
        this.title = title;
        return this;
    }

    public Change getChanges() {
        return changes;
    }

    public Requirement setChanges(Change changes) {
        this.changes = changes;
        return this;
    }

    public Priority getPriority() {
        return priority;
    }

    public Requirement setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public EvaluationMethod getEvaluationMethod() {
        return evaluationMethod;
    }

    public Requirement setEvaluationMethod(EvaluationMethod evaluationMethod) {
        this.evaluationMethod = evaluationMethod;
        return this;
    }

    public EvaluationStatus getEvaluationStatus() {
        return evaluationStatus;
    }

    public Requirement setEvaluationStatus(EvaluationStatus evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
        return this;
    }

    public QualityFactor getQualityFactor() {
        return qualityFactor;
    }

    public Requirement setQualityFactor(QualityFactor qualityFactor) {
        this.qualityFactor = qualityFactor;
        return this;
    }

    public RequirementType getRequirementType() {
        return requirementType;
    }

    public Requirement setRequirementType(RequirementType requirementType) {
        this.requirementType = requirementType;
        return this;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public Requirement setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public Requirement setProject(Project project) {
        this.project = project;
        return this;
    }

    public String getAttachment() {
        return attachment;
    }

    public Requirement setAttachment(String attachment) {
        this.attachment = attachment;
        return this;
    }

    public List<Requirement> getParents() {
        return parents;
    }

    public Requirement setParents(List<Requirement> parents) {
        this.parents = parents;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public Requirement setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Requirement setNumber(Integer number) {
        this.number = number;
        return this;
    }

    /*public List<Requirement> getChildren() {
        return children;
    }

    public Requirement setChildren(List<Requirement> children) {
        this.children = children;
        return this;
    }*/

    @Override
    public String toString() {
        String prefix = "";
        for (int i = 0; i < level - 1; i++) {
            prefix = prefix + "S";
        }
        prefix = prefix + "R";
        return prefix + "-" + number + " " + title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requirement that = (Requirement) o;
        return that.id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
