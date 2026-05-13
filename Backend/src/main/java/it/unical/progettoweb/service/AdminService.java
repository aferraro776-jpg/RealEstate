package it.unical.progettoweb.service;

import it.unical.progettoweb.dao.impl.AdminDao;
import it.unical.progettoweb.dao.impl.BlacklistDao;
import it.unical.progettoweb.dao.impl.SellerDao;
import it.unical.progettoweb.dao.impl.UserDao;
import it.unical.progettoweb.model.Admin;
import it.unical.progettoweb.model.Seller;
import it.unical.progettoweb.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserDao userDao;
    private final SellerDao sellerDao;
    private final AdminDao adminDao;
    private final BlacklistDao blacklistDao;

    private final Random random = new Random();

    private int generateAdminId() {
        int id;
        do {
            id = random.nextInt(89999) + 10000;
        } while (adminDao.get(id).isPresent());
        return id;
    }

    public void banUser(String email) {

        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isBanned())
                throw new IllegalStateException("Utente già bannato.");
            user.setBanned(true);
            userDao.update(user);
            blacklistDao.ban(email);
            return;
        }

        Optional<Seller> sellerOpt = sellerDao.findByEmail(email);
        if (sellerOpt.isPresent()) {
            Seller seller = sellerOpt.get();
            if (seller.isBanned())
                throw new IllegalStateException("Venditore già bannato.");
            seller.setBanned(true);
            sellerDao.update(seller);
            blacklistDao.ban(email);
            return;
        }

        throw new IllegalArgumentException("Nessun utente trovato con questa email.");
    }

    public void unbanUser(String email) {
        if (!blacklistDao.isBanned(email))
            throw new IllegalStateException("L'utente non risulta bannato.");

        blacklistDao.unban(email);

        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setBanned(false);
            userDao.update(user);
            return;
        }

        Optional<Seller> sellerOpt = sellerDao.findByEmail(email);
        if (sellerOpt.isPresent()) {
            Seller seller = sellerOpt.get();
            seller.setBanned(false);
            sellerDao.update(seller);
        }
    }

    public void promuoviAdAdmin(String email) {

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nessun utente trovato con questa email. " +
                                "Solo gli acquirenti possono essere promossi ad amministratore."));

        Admin admin = new Admin();
        admin.setId(generateAdminId());
        admin.setName(user.getName());
        admin.setSurname(user.getSurname());
        admin.setEmail(user.getEmail());

        admin.setPassword(user.getPassword());

        adminDao.save(admin);
        userDao.delete(user.getId());
    }
}