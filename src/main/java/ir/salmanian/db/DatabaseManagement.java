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
        List<Project> projects = null;
        Session session = getSession();
        String hql = "FROM Project p WHERE p.creator =:user";

        Query query = session.createQuery(hql);
        query.setParameter("user", user);
        projects = query.getResultList();
        session.close();
        return projects;

    }

    public void saveProject(Project project) {
        Session session = getSession();
        session.beginTransaction();
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

    public List<Project> searchProjects(String text) {
        Session session = getSession();
        String hql = "FROM Project p WHERE p.name LIKE :text";
        Query query = session.createQuery(hql);
        query.setParameter("text", "%" + text + "%");
        List<Project> projects = query.getResultList();
        session.close();
        return projects;
    }

    public List<Requirement> getProjectRequirements(Project project) {
        Session session = getSession();
        String hql = "FROM Requirement r WHERE r.project =:project ORDER BY r.prefix,r.id ASC";
        Query query = session.createQuery(hql);
        query.setParameter("project", project);
        List<Requirement> requirements = query.getResultList();
        session.close();
        return requirements;
    }

    public void saveRequirement(Requirement requirement) {
        Session session = getSession();
        session.beginTransaction();
        session.saveOrUpdate(requirement);
        session.getTransaction().commit();
        session.close();
    }

    public List<Requirement> searchRequirements(String text, Project project) {
        Session session = getSession();
        String hql = "FROM Requirement r WHERE r.project =:project AND (r.title LIKE :text OR CONCAT(r.prefix,'-',r.id) LIKE :text)";
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
        String hql = "SELECT r FROM Requirement r JOIN r.parents p WHERE p=:requirement";
        Query query = session.createQuery(hql);
        query.setParameter("requirement", requirement);
        List<Requirement> requirements = query.getResultList();
        session.close();
        return requirements;
    }

}
