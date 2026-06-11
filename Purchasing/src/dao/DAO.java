package dao;

import java.util.List;

/**
 * Generic DAO (Data Access Object) interface for standard CRUD operations.
 * @param <T> The entity type (e.g., Product, User, Purchase)
 */
public interface DAO<T> {
    T findById(int id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(int id);
}