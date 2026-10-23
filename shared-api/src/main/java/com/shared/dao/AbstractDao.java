package com.shared.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID extends Serializable> {

    @Getter
    @PersistenceContext
    public EntityManager entityManager;

    private final Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return entityManager.createQuery(jpql, entityClass).getResultList();
    }
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public void delete(T entity) {
        T managed = entityManager.contains(entity) ? entity : entityManager.merge(entity);
        entityManager.remove(managed);
    }

    public boolean deleteById(ID id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            delete(entity.get());
            return true;
        }
        return false;
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }
}
