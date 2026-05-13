package it.unical.progettoweb.service;

import it.unical.progettoweb.dao.impl.AdminDao;
import it.unical.progettoweb.dao.impl.BlacklistDao;
import it.unical.progettoweb.dao.impl.SellerDao;
import it.unical.progettoweb.dao.impl.UserDao;
import it.unical.progettoweb.dto.response.UserDto;
import it.unical.progettoweb.dto.request.SellerRequest;
import it.unical.progettoweb.dto.request.UserRequest;
import it.unical.progettoweb.dto.response.SellerDto;
import it.unical.progettoweb.dto.response.AdminDto;
import it.unical.progettoweb.model.Admin;
import it.unical.progettoweb.model.Seller;
import it.unical.progettoweb.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userDao;
    private final SellerDao sellerDao;
    private final AdminDao adminDao;
    private final BlacklistDao blacklistDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private final Random random = new Random();

    private int generateUserId() {
        int id;
        do { id = random.nextInt(89999) + 10000; }
        while (userDao.get(id).isPresent());
        return id;
    }

    private int generateSellerId() {
        int id;
        do { id = random.nextInt(89999) + 10000; }
        while (sellerDao.get(id).isPresent());
        return id;
    }

    private void validaEmail(String email) {
        if (!Validation.checkEmail(email))
            throw new IllegalArgumentException("Formato email non valido.");
        if (blacklistDao.isBanned(email))
            throw new IllegalArgumentException("Email non autorizzata alla registrazione.");
        if (userDao.existsByEmail(email) || sellerDao.existsByEmail(email) || adminDao.existsByEmail(email))
            throw new IllegalArgumentException("Email già registrata.");
    }

    private void validaGeneralita(String nome, String cognome) {
        if (!Validation.checkNome(nome))
            throw new IllegalArgumentException("Nome non valido (minimo 3 caratteri).");
        if (!Validation.checkCognome(cognome))
            throw new IllegalArgumentException("Cognome non valido (minimo 3 caratteri).");
    }

    private void validaPassword(String password) {
        String errori = Validation.getErrorePassword(password);
        if (errori != null)
            throw new IllegalArgumentException(errori);
    }

    private void validaDataNascita(java.util.Date data) {
        if (data == null)
            throw new IllegalArgumentException("Data di nascita obbligatoria.");
        if (!Validation.checkDataNascita(SDF.format(data)))
            throw new IllegalArgumentException("Data di nascita non valida.");
    }

    public void registraUser(UserRequest dto) {
        validaEmail(dto.getEmail());
        validaGeneralita(dto.getName(), dto.getSurname());
        validaPassword(dto.getPassword());
        validaDataNascita(dto.getBirthDate());

        User user = new User();
        user.setId(generateUserId());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setBirthDate(dto.getBirthDate());
        user.setAuthProvider("LOCAL");
        user.setBanned(false);

        userDao.save(user);
    }

    public void registraSeller(SellerRequest dto) {
        validaEmail(dto.getEmail());
        validaGeneralita(dto.getName(), dto.getSurname());
        validaPassword(dto.getPassword());
        validaDataNascita(dto.getBirthDate());

        if (dto.getVatNumber() == null || dto.getVatNumber().isBlank())
            throw new IllegalArgumentException("Partita IVA obbligatoria per i venditori.");
        if (sellerDao.existsByVatNumber(dto.getVatNumber()))
            throw new IllegalArgumentException("Partita IVA già registrata.");

        Seller seller = new Seller();
        seller.setId(generateSellerId());
        seller.setName(dto.getName());
        seller.setSurname(dto.getSurname());
        seller.setEmail(dto.getEmail());
        seller.setPassword(passwordEncoder.encode(dto.getPassword()));
        seller.setBirthDate(dto.getBirthDate());
        seller.setVatNumber(dto.getVatNumber());
        seller.setBanned(false);

        sellerDao.save(seller);
    }

    public String login(String email, String password) {
        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("GOOGLE".equals(user.getAuthProvider()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Questo account usa Google per accedere.");
            if (!passwordEncoder.matches(password, user.getPassword()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide.");
            return jwtUtil.generateToken(email, "BUYER", user.getId());
        }

        Optional<Seller> sellerOpt = sellerDao.findByEmail(email);
        if (sellerOpt.isPresent()) {
            Seller seller = sellerOpt.get();
            if (!passwordEncoder.matches(password, seller.getPassword()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide.");
            return jwtUtil.generateToken(email, "SELLER", seller.getId());
        }

        Optional<Admin> adminOpt = adminDao.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (!passwordEncoder.matches(password, admin.getPassword()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide.");
            return jwtUtil.generateToken(email, "ADMIN", admin.getId());
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide.");
    }

    public Object getMe(String authHeader) {
        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token non valido o scaduto.");

        String email = jwtUtil.extractEmail(token);
        String ruolo = jwtUtil.extractRole(token);

        return switch (ruolo) {
            case "BUYER" -> userDao.findByEmail(email)
                    .map(u -> new UserDto(
                            u.getId(), u.getName(), u.getSurname(),
                            u.getEmail(), u.getBirthDate(), u.getAuthProvider(), "BUYER"))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato."));

            case "SELLER" -> sellerDao.findByEmail(email)
                    .map(s -> new SellerDto(
                            s.getId(), s.getName(), s.getSurname(),
                            s.getEmail(), s.getVatNumber(), s.getBirthDate(), "SELLER"))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venditore non trovato."));

            case "ADMIN" -> adminDao.findByEmail(email)
                    .map(a -> new AdminDto(
                            a.getId(), a.getName(), a.getSurname(), a.getEmail(), "ADMIN"))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin non trovato."));

            default -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ruolo non riconosciuto.");
        };
    }

    public void inviaOtpRegistrazione(String email) {
        if (userDao.findByEmail(email).isPresent() || sellerDao.existsByEmail(email))
            throw new IllegalArgumentException("Email già registrata.");
        String code = otpService.generateOtp(email);
        emailService.sendOtp(email, code, "Registrazione");
    }

    public void inviaOtpRecuperoPassword(String email) {
        if (userDao.findByEmail(email).isEmpty() && sellerDao.findByEmail(email).isEmpty())
            throw new IllegalArgumentException("Email non trovata.");
        String code = otpService.generateOtp(email);
        emailService.sendOtp(email, code, "Recupero password");
    }

    public void resetPassword(String email, String otp, String newPassword) {
        if (!otpService.verifyOtp(email, otp))
            throw new IllegalArgumentException("OTP non valido o scaduto.");

        validaPassword(newPassword);

        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userDao.update(user);
            return;
        }

        Seller seller = sellerDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));
        seller.setPassword(passwordEncoder.encode(newPassword));
        sellerDao.update(seller);
    }
}