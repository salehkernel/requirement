package ir.salmanian.db;

import ir.salmanian.models.Project;
import ir.salmanian.models.Requirement;
import ir.salmanian.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

public class DatabaseManagement {

    private static DatabaseManagement instance;
    private final SessionFactory sessionFactory;


    private Session getSession() {
        return sessionFactory.openSession();
    }

    private DatabaseManagement() {
        Configuration config = new Configuration();
        config.configure();
        sessionFactory = config.buildSessionFactory();
    }

    public static DatabaseManagement getInstance() {
        if (instance == null) {
            instance = new DatabaseManagement();
        }
        return instance;
    }

    public void saveUser(User user) {
        Session session = getSession();
        session.beginTransaction();
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
            user.setNumber(getNextUserNumber());
        }
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    public User findByUsername(String username) {
        Session session = getSession();
        String hql = "FROM User u WHERE u.username =:username";
        Query query = session.createQuery(hql, User.class);
        query.setParameter("username", username);
        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            user = null;
        }
        return user;
    }

    public User findByEmail(String email) {
        Session session = getSession();
        String hql = "FROM User u WHERE u.email=:email";
        Query query = session.createQuery(hql);
        query.setParameter("email", email);
        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            user = null;
        }
        return user;
    }

    public List<Project> getUserProjects(User user) {
        List<Project> projects;
        Session session = getSession();
        String hql = "FROM Project p WHERE p.creator =:user ORDER BY p.number";

        Query query = session.createQuery(hql);
        query.setParameter("user", user);
        projects = query.getResultList();
        session.close();
        return projects;

    }

    public void saveProject(Project project) {
        Session session = getSession();
        session.beginTransaction();
        if (project.getId() == null) {
            project.setId(UUID.randomUUID());
            project.setNumber(getNextProjectNumber(project.getCreator()));
        }
        session.saveOrUpdate(project);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteProject(Project project) {
        Session session = getSession();
        session.beginTransaction();
        session.remove(project);
        session.getTransaction().commit();
        session.close();
    }

    public List<Project> searchProjects(String text, User user) {
        Session session = getSession();
        String hql = "FROM Project p WHERE p.name LIKE :text AND p.creator =:creator ORDER BY p.number";
        Query query = session.createQuery(hql);
        query.setParameter("text", "%" + text + "%");
        query.setParameter("creator", user);
        List<Project> projects = query.getResultList();
        session.close();
        return projects;
    }

    public List<Requirement> getProjectRequirements(Project project) {
        Session session = getSession();
        String hql = "FROM Requirement r WHERE r.project =:project ORDER BY r.level,r.number ASC";
        Query query = session.createQuery(hql);
        query.setParameter("project", project);
        List<Requirement> requirements = query.getResultList();
        session.close();
        return requirements;
    }

    public void saveRequirement(Requirement requirement) {
        Session session = getSession();
        session.beginTransaction();
        if (requirement.getId() == null) {
            requirement.setId(UUID.randomUUID());
            Project project = requirement.getProject();
            requirement.setNumber(getNextRequirementNumber(project));
            project.setLastRequirementNumber(requirement.getNumber());
            saveProject(project);
        }
        session.saveOrUpdate(requirement);
        session.getTransaction().commit();
        session.close();
    }

    public List<Requirement> searchRequirements(String text, Project project) {
        Session session = getSession();
        String hql = "FROM Requirement r " +
                "WHERE r.project =:project AND (r.title LIKE :text OR CONCAT(r.prefix,'-',r.number) LIKE :text) " +
                "ORDER BY r.level, r.number ASC";
        Query query = session.createQuery(hql);
        query.setParameter("text", "%" + text + "%");
        query.setParameter("project", project);
        List<Requirement> requirements = query.getResultList();
        session.close();
        return requirements;
    }

    public void deleteRequirement(Requirement requirement) {
        Session session = getSession();
        session.beginTransaction();
        List<Requirement> requirements = getChildrenRequirements(requirement);
        for (Requirement childRequirement : requirements) {
            childRequirement.getParents().remove(requirement);
            session.saveOrUpdate(childRequirement);
        }
        session.delete(requirement);
        session.getTransaction().commit();
        session.close();
    }

    public List<Requirement> getChildrenRequirements(Requirement requirement) {
        Session session = getSession();
        String hql = "SELECT r FROM Requirement r JOIN r.parents p WHERE p=:requirement ORDER BY r.level, r.number ASC";
        Query query = session.createQuery(hql);
        query.setParameter("requirement", requirement);
        List<Requirement> requirements = query.getResultList();
        session.close();
        return requirements;
    }

    public Integer getMaxLevel(Project project) {
        Session session = getSession();
        String hql = "SELECT MAX(r.level) FROM Requirement r WHERE r.project =:project";
        Query query = session.createQuery(hql);
        query.setParameter("project", project);
        Integer maxLevel = (Integer) query.getSingleResult();
        session.close();
        return maxLevel;
    }

    public Integer getNextUserNumber() {
        Session session = getSession();
        String hql = "SELECT MAX(u.number) FROM User u";
        Query query = session.createQuery(hql);
        Integer lastUserNumber = (Integer) query.getSingleResult();
        session.close();
        return lastUserNumber != null ? lastUserNumber + 1 : 1;
    }

    public Integer getNextProjectNumber(User user) {
        Session session = getSession();
        String hql = "SELECT MAX(p.number) FROM Project p WHERE p.creator =:user";
        Query query = session.createQuery(hql);
        query.setParameter("user", user);
        Integer lastProjectNumber = (Integer) query.getSingleResult();
        session.close();
        return lastProjectNumber != null ? lastProjectNumber + 1 : 1;
    }

    public Integer getNextRequirementNumber(Project project) {
        Session session = getSession();
        String hql = "SELECT p.lastRequirementNumber FROM Project p WHERE p =:project";
        Query query = session.createQuery(hql);
        query.setParameter("project", project);
        Integer lastRequirementNumber = (Integer) query.getSingleResult();
        session.close();
        return lastRequirementNumber != null ? lastRequirementNumber + 1 : 1;

    }
}
