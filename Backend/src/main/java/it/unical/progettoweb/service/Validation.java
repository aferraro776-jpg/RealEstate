package it.unical.progettoweb.service;

import java.util.regex.Pattern;

public class Validation {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    public Validation(){}

    public static boolean checkEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean checkNome(String nome) {
        return nome != null && nome.length() > 2;
    }

    public static boolean checkCognome(String cognome) {
        return cognome != null && cognome.length() > 2;
    }

    public static boolean checkDataNascita(String dataNascita) {
        return dataNascita != null && DATE_PATTERN.matcher(dataNascita).matches();
    }

    public static String getErrorePassword(String password) {

        if (password == null) return "La password non può essere vuota.\n";

        StringBuilder errori = new StringBuilder();
        if (password.length() < 8) errori.append("La password deve avere almeno 8 caratteri.\n");
        if (!password.matches(".*\\p{Lu}.*")) errori.append("La password deve avere almeno una lettera maiuscola.\n");
        if (!password.matches(".*\\p{Ll}.*")) errori.append("La password deve avere almeno una lettera minuscola.\n");
        if (!password.matches(".*\\d.*")) errori.append("La password deve avere almeno un numero.\n");
        if (!password.matches(".*\\p{Punct}.*")) errori.append("La password deve avere almeno un carattere speciale(es. ! @ # $ % ^ & * ...).\n");
        if (password.contains(" ")) errori.append("La passaword non deve avere spazi.\n");
        return errori.isEmpty() ? null : errori.toString();
    }

}