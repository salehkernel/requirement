package ir.salmanian.models;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "requirements")
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    @GenericGenerator(name = "sequence-generator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "requirement_value"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")})
    private Long id;
    @Column(name = "prefix")
    private String prefix;
    @Column(name = "title")
    private String title;
    @Column(name = "changes")
    private Change changes;
    @Column(name = "priority")
    private Priority priority;
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;
    @Column(name = "evaluation_status")
    private EvaluationStatus evaluationStatus;
    @Column(name = "quality_factor")
    private QualityFactor qualityFactor;
    @Column(name = "requirement_type")
    private RequirementType requirementType;
    @Column(name = "review_status")
    private ReviewStatus reviewStatus;
    @ManyToOne
    private Project project;
    @Column(name = "attachment")
    @Lob
    private String attachment;
    @Column(name = "level")
    @ColumnDefault(value = "1")
    private Integer level;

    @ManyToMany(fetch = FetchType.EAGER)
    List<Requirement> parents = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Requirement setId(Long id) {
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

    @Override
    public String toString() {
        String prefix = "";
        for (int i = 0; i < level - 1; i++) {
            prefix = prefix + "S";
        }
        prefix = prefix + "R";
        return prefix + "-" + id + " " + title;
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
