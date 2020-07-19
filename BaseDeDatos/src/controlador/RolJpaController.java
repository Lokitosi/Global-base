/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import basededatos.persistencia.Permiso;
import basededatos.persistencia.Rol;
import java.util.ArrayList;
import java.util.List;
import basededatos.persistencia.Usuario;
import controlador.exceptions.IllegalOrphanException;
import controlador.exceptions.NonexistentEntityException;
import controlador.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author andre
 */
public class RolJpaController implements Serializable {

    public RolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rol rol) throws PreexistingEntityException, Exception {
        if (rol.getPermisoList() == null) {
            rol.setPermisoList(new ArrayList<Permiso>());
        }
        if (rol.getUsuarioList() == null) {
            rol.setUsuarioList(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Permiso> attachedPermisoList = new ArrayList<Permiso>();
            for (Permiso permisoListPermisoToAttach : rol.getPermisoList()) {
                permisoListPermisoToAttach = em.getReference(permisoListPermisoToAttach.getClass(), permisoListPermisoToAttach.getIdPermiso());
                attachedPermisoList.add(permisoListPermisoToAttach);
            }
            rol.setPermisoList(attachedPermisoList);
            List<Usuario> attachedUsuarioList = new ArrayList<Usuario>();
            for (Usuario usuarioListUsuarioToAttach : rol.getUsuarioList()) {
                usuarioListUsuarioToAttach = em.getReference(usuarioListUsuarioToAttach.getClass(), usuarioListUsuarioToAttach.getUsuario());
                attachedUsuarioList.add(usuarioListUsuarioToAttach);
            }
            rol.setUsuarioList(attachedUsuarioList);
            em.persist(rol);
            for (Permiso permisoListPermiso : rol.getPermisoList()) {
                permisoListPermiso.getRolList().add(rol);
                permisoListPermiso = em.merge(permisoListPermiso);
            }
            for (Usuario usuarioListUsuario : rol.getUsuarioList()) {
                Rol oldRolidRolOfUsuarioListUsuario = usuarioListUsuario.getRolidRol();
                usuarioListUsuario.setRolidRol(rol);
                usuarioListUsuario = em.merge(usuarioListUsuario);
                if (oldRolidRolOfUsuarioListUsuario != null) {
                    oldRolidRolOfUsuarioListUsuario.getUsuarioList().remove(usuarioListUsuario);
                    oldRolidRolOfUsuarioListUsuario = em.merge(oldRolidRolOfUsuarioListUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRol(rol.getIdRol()) != null) {
                throw new PreexistingEntityException("Rol " + rol + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rol rol) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol persistentRol = em.find(Rol.class, rol.getIdRol());
            List<Permiso> permisoListOld = persistentRol.getPermisoList();
            List<Permiso> permisoListNew = rol.getPermisoList();
            List<Usuario> usuarioListOld = persistentRol.getUsuarioList();
            List<Usuario> usuarioListNew = rol.getUsuarioList();
            List<String> illegalOrphanMessages = null;
            for (Usuario usuarioListOldUsuario : usuarioListOld) {
                if (!usuarioListNew.contains(usuarioListOldUsuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuario " + usuarioListOldUsuario + " since its rolidRol field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> attachedPermisoListNew = new ArrayList<Permiso>();
            for (Permiso permisoListNewPermisoToAttach : permisoListNew) {
                permisoListNewPermisoToAttach = em.getReference(permisoListNewPermisoToAttach.getClass(), permisoListNewPermisoToAttach.getIdPermiso());
                attachedPermisoListNew.add(permisoListNewPermisoToAttach);
            }
            permisoListNew = attachedPermisoListNew;
            rol.setPermisoList(permisoListNew);
            List<Usuario> attachedUsuarioListNew = new ArrayList<Usuario>();
            for (Usuario usuarioListNewUsuarioToAttach : usuarioListNew) {
                usuarioListNewUsuarioToAttach = em.getReference(usuarioListNewUsuarioToAttach.getClass(), usuarioListNewUsuarioToAttach.getUsuario());
                attachedUsuarioListNew.add(usuarioListNewUsuarioToAttach);
            }
            usuarioListNew = attachedUsuarioListNew;
            rol.setUsuarioList(usuarioListNew);
            rol = em.merge(rol);
            for (Permiso permisoListOldPermiso : permisoListOld) {
                if (!permisoListNew.contains(permisoListOldPermiso)) {
                    permisoListOldPermiso.getRolList().remove(rol);
                    permisoListOldPermiso = em.merge(permisoListOldPermiso);
                }
            }
            for (Permiso permisoListNewPermiso : permisoListNew) {
                if (!permisoListOld.contains(permisoListNewPermiso)) {
                    permisoListNewPermiso.getRolList().add(rol);
                    permisoListNewPermiso = em.merge(permisoListNewPermiso);
                }
            }
            for (Usuario usuarioListNewUsuario : usuarioListNew) {
                if (!usuarioListOld.contains(usuarioListNewUsuario)) {
                    Rol oldRolidRolOfUsuarioListNewUsuario = usuarioListNewUsuario.getRolidRol();
                    usuarioListNewUsuario.setRolidRol(rol);
                    usuarioListNewUsuario = em.merge(usuarioListNewUsuario);
                    if (oldRolidRolOfUsuarioListNewUsuario != null && !oldRolidRolOfUsuarioListNewUsuario.equals(rol)) {
                        oldRolidRolOfUsuarioListNewUsuario.getUsuarioList().remove(usuarioListNewUsuario);
                        oldRolidRolOfUsuarioListNewUsuario = em.merge(oldRolidRolOfUsuarioListNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rol.getIdRol();
                if (findRol(id) == null) {
                    throw new NonexistentEntityException("The rol with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol rol;
            try {
                rol = em.getReference(Rol.class, id);
                rol.getIdRol();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rol with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Usuario> usuarioListOrphanCheck = rol.getUsuarioList();
            for (Usuario usuarioListOrphanCheckUsuario : usuarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Rol (" + rol + ") cannot be destroyed since the Usuario " + usuarioListOrphanCheckUsuario + " in its usuarioList field has a non-nullable rolidRol field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> permisoList = rol.getPermisoList();
            for (Permiso permisoListPermiso : permisoList) {
                permisoListPermiso.getRolList().remove(rol);
                permisoListPermiso = em.merge(permisoListPermiso);
            }
            em.remove(rol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rol> findRolEntities() {
        return findRolEntities(true, -1, -1);
    }

    public List<Rol> findRolEntities(int maxResults, int firstResult) {
        return findRolEntities(false, maxResults, firstResult);
    }

    private List<Rol> findRolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rol.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Rol findRol(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rol> rt = cq.from(Rol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
