package ir.salmanian.models;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * One of entity classes used in this application.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @Type(type = "uuid-char")
    private UUID id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "number")
    private Integer number;

    @OneToMany
    private List<Project> projects;

    public UUID getId() {
        return id;
    }

    public User setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public User setProjects(List<Project> projects) {
        this.projects = projects;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public User setNumber(Integer number) {
        this.number = number;
        return this;
    }
}
