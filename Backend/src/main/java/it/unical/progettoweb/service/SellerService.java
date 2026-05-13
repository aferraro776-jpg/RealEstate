package it.unical.progettoweb.service;

import it.unical.progettoweb.dao.impl.AdminDao;
import it.unical.progettoweb.dao.impl.BlacklistDao;
import it.unical.progettoweb.dao.impl.SellerDao;
import it.unical.progettoweb.dao.impl.UserDao;
import it.unical.progettoweb.dto.request.SellerRequest;
import it.unical.progettoweb.dto.response.SellerDto;
import it.unical.progettoweb.model.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerDao sellerDao;
    private final UserDao userDao;
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

    public SellerDto getSellerByEmail(String email) {
        if (sellerDao.findByEmail(email).isEmpty())
            throw new IllegalArgumentException("Venditore non trovato.");
        Seller seller = sellerDao.findByEmail(email).get();
        return toDto(seller);
    }

    public void aggiornaProfilo(String emailDalToken, SellerRequest dto) {
        if (sellerDao.findByEmail(emailDalToken).isEmpty())
            throw new IllegalArgumentException("Venditore non trovato.");
        Seller seller = sellerDao.findByEmail(emailDalToken).get();

        validaGeneralita(dto.getName(), dto.getSurname());
        validaEmailModifica(dto.getEmail(), emailDalToken);
        validaDataNascita(dto.getBirthDate());

        seller.setName(dto.getName());
        seller.setSurname(dto.getSurname());
        seller.setEmail(dto.getEmail());
        seller.setBirthDate(dto.getBirthDate());
        sellerDao.update(seller);
    }

    public void cambiaPassword(String emailDalToken, String oldPassword, String newPassword) {
        if (sellerDao.findByEmail(emailDalToken).isEmpty())
            throw new IllegalArgumentException("Venditore non trovato.");
        Seller seller = sellerDao.findByEmail(emailDalToken).get();

        if (!passwordEncoder.matches(oldPassword, seller.getPassword()))
            throw new IllegalArgumentException("La vecchia password non è corretta.");

        String errori = Validation.getErrorePassword(newPassword);
        if (errori != null)
            throw new IllegalArgumentException(errori);

        seller.setPassword(passwordEncoder.encode(newPassword));
        sellerDao.update(seller);
    }

    public void cancellaAccount(String emailDalToken) {
        if (sellerDao.findByEmail(emailDalToken).isEmpty())
            throw new IllegalArgumentException("Venditore non trovato.");
        Seller seller = sellerDao.findByEmail(emailDalToken).get();
        sellerDao.delete(seller.getId());
    }

    private SellerDto toDto(Seller seller) {
        return new SellerDto(
                seller.getId(),
                seller.getName(),
                seller.getSurname(),
                seller.getEmail(),
                seller.getVatNumber(),
                seller.getBirthDate(),
                "SELLER"
        );
    }

    public SellerDto getSellerById(int id) {
        Seller seller = sellerDao.get(id)
                .orElseThrow(() -> new RuntimeException("Venditore non trovato"));
        return toDto(seller);
    }
}