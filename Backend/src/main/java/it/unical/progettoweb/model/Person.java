package it.unical.progettoweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Person {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;
}
