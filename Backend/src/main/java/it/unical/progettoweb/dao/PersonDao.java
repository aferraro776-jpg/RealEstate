package it.unical.progettoweb.dao;

import it.unical.progettoweb.model.Person;

import java.util.Optional;

public interface PersonDao<T extends Person> extends Dao<T, Integer>{
    Optional<T> findByEmail(String email);
    boolean existsByEmail(String email);
}
