package it.unical.progettoweb.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {

    T save(T entity);
    Optional<T> get(ID id);
    List<T> getAll();
    T update(T entity);
    void delete(ID id);
}