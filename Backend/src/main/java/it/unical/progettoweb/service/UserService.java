package it.unical.progettoweb.service;

import it.unical.progettoweb.dao.impl.AdminDao;
import it.unical.progettoweb.dao.impl.BlacklistDao;
import it.unical.progettoweb.dao.impl.SellerDao;
import it.unical.progettoweb.dao.impl.UserDao;
import it.unical.progettoweb.dto.request.UserRequest;
import it.unical.progettoweb.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final SellerDao sellerDao;
    private final AdminDao adminDao;
    private final BlacklistDao blacklistDao;
    private final PasswordEncoder passwordEncoder;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private void validaEmailModifica(String nuovaEmail, String emailAttuale) {
        if (!Validation.checkEmail(nuovaEmail))
            throw new IllegalArgumentException("Formato email non valido.");

        if (blacklistDao.isBanned(nuovaEmail))
            throw new IllegalArgumentException("Si è verificato un errore. La mail che stai cercando di usare è stata bannata.");

        if (!nuovaEmail.equalsIgnoreCase(emailAttuale)) {
            if (userDao.existsByEmail(nuovaEmail) ||
                    sellerDao.existsByEmail(nuovaEmail) ||
                    adminDao.existsByEmail(nuovaEmail))
                throw new IllegalArgumentException("Questa Email è gia associata ad un account.");
        }
    }

    private void validaGeneralita(String nome, String cognome) {
        if (!Validation.checkNome(nome))
            throw new IllegalArgumentException("Nome non valido (minimo 3 caratteri).");
        if (!Validation.checkCognome(cognome))
            throw new IllegalArgumentException("Cognome non valido (minimo 3 caratteri).");
    }

    private void validaDataNascita(java.util.Date data) {
        if (data == null)
            throw new IllegalArgumentException("Data di nascita obbligatoria.");
        if (!Validation.checkDataNascita(SDF.format(data)))
            throw new IllegalArgumentException("Data di nascita non valida.");
    }

    public User getUtenteByEmail(String email) {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));
    }

    public void aggiornaProfilo(String emailDalToken, UserRequest dto) {
        User user = getUtenteByEmail(emailDalToken);

        validaGeneralita(dto.getName(), dto.getSurname());
        validaEmailModifica(dto.getEmail(), emailDalToken);
        validaDataNascita(dto.getBirthDate());

        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setBirthDate(dto.getBirthDate());

        userDao.update(user);
    }

    public void cambiaPassword(String emailDalToken, String oldPassword, String newPassword) {
        User user = getUtenteByEmail(emailDalToken);

        if ("GOOGLE".equalsIgnoreCase(user.getAuthProvider()))
            throw new IllegalStateException(
                    "Sei registrato con Google. Non puoi modificare la password.");

        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new IllegalArgumentException("La vecchia password non è corretta.");

        String errori = Validation.getErrorePassword(newPassword);
        if (errori != null)
            throw new IllegalArgumentException(errori);

        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.update(user);
    }

    public void cancellaAccount(String emailDalToken) {
        User user = getUtenteByEmail(emailDalToken);
        userDao.delete(user.getId());
    }
}