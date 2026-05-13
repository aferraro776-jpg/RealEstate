package it.unical.progettoweb.service;

import it.unical.progettoweb.dao.impl.AuctionDaoImpl;
import it.unical.progettoweb.dao.impl.BidDaoImpl;
import it.unical.progettoweb.dao.impl.PostDaoImpl;
import it.unical.progettoweb.dao.impl.UserDao;
import it.unical.progettoweb.dto.request.AuctionRequest;
import it.unical.progettoweb.dto.response.AuctionDto;
import it.unical.progettoweb.dto.response.BidDto;
import it.unical.progettoweb.mapper.BidRowMapper;
import it.unical.progettoweb.model.Auction;
import it.unical.progettoweb.model.Bid;
import it.unical.progettoweb.model.Post;
import it.unical.progettoweb.proxy.BidProxy;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuctionService {

    private final AuctionDaoImpl auctionDao;
    private final BidDaoImpl bidDao;
    private final PostDaoImpl postDao;
    private final UserDao userDao;
    private final EmailService emailService;
    private final JdbcTemplate jdbcTemplate;
    private final BidRowMapper bidRowMapper;


    public AuctionDto createAuction(AuctionRequest request, int sellerId) {
        Post post = postDao.get(request.getPostId()).orElse(null);

        if (post == null) {
            throw new RuntimeException("Post non trovato");
        }
        if (post.getSellerId() != sellerId) {
            throw new IllegalArgumentException("Non puoi creare un'asta su un annuncio che non è tuo");
        }
        if (auctionDao.findByPostId(request.getPostId()).isPresent()) {
            throw new RuntimeException("Esiste già un'asta attiva per questo annuncio");
        }
        if (!request.getEndDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La data di chiusura deve essere nel futuro");
        }

        Auction auction = new Auction();
        auction.setId(generateUniqueId());
        auction.setPostId(request.getPostId());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setCurrentBest(request.getStartingPrice());
        auction.setEndDate(request.getEndDate());
        auction.setClosed(false);
        auction.setWinnerId(null);

        auction = auctionDao.save(auction);
        return toDto(auction);
    }


    public AuctionDto getAuctionByPostId(int postId) {
        Auction auction = auctionDao.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Nessuna asta trovata per questo annuncio"));

        BidProxy proxy = new BidProxy(auction.getId(), jdbcTemplate, bidRowMapper);
        auction.setBids(proxy.getBids());

        return toDto(auction);
    }


    public BidDto placeBid(int auctionId, double amount, int userId) {
        Auction auction = auctionDao.get(auctionId).orElse(null);

        if (auction == null) {
            throw new RuntimeException("Asta non trovata");
        }
        if (auction.isClosed()) {
            throw new RuntimeException("L'asta è già terminata");
        }
        if (LocalDateTime.now().isAfter(auction.getEndDate())) {
            throw new RuntimeException("L'asta è scaduta");
        }
        if (amount < auction.getCurrentBest() + 2000.0) {
            throw new IllegalArgumentException(
                    "L'offerta deve essere almeno € " +
                            String.format("%.2f", auction.getCurrentBest() + 2000.0));
        }

        Bid bid = new Bid();
        bid.setId(generateUniqueId());
        bid.setAuctionId(auctionId);
        bid.setUserId(userId);
        bid.setAmount(amount);
        bid.setPlacedAt(LocalDateTime.now());

        bidDao.save(bid);
        auctionDao.updateCurrentBest(auctionId, amount);

        return bidToDto(bid);
    }


    public void deleteAuction(int auctionId, int sellerId) {
        Auction auction = auctionDao.get(auctionId).orElse(null);

        if (auction == null) {
            throw new RuntimeException("Asta non trovata");
        }

        Post post = postDao.get(auction.getPostId()).orElse(null);
        if (post == null || post.getSellerId() != sellerId) {
            throw new IllegalArgumentException("Non puoi eliminare un'asta che non è tua");
        }

        List<Bid> bids = bidDao.findByAuctionId(auctionId);
        if (!bids.isEmpty()) {
            throw new RuntimeException("Impossibile eliminare l'asta: sono già presenti offerte");
        }

        auctionDao.delete(auctionId);
    }


    public void closeExpiredAuctions() {
        List<Auction> openAuctions = auctionDao.findAllOpen();

        for (Auction auction : openAuctions) {
            if (!LocalDateTime.now().isAfter(auction.getEndDate())) {
                continue;
            }

            Optional<Bid> bestBid = bidDao.findBestBid(auction.getId());

            if (bestBid.isPresent()) {
                Bid winner = bestBid.get();
                auctionDao.setWinnerAndClose(auction.getId(), winner.getUserId());

                userDao.get(winner.getUserId()).ifPresent(user -> {
                    Post post = postDao.get(auction.getPostId()).orElse(null);
                    String titolo = post != null ? post.getTitle() : "Immobile #" + auction.getPostId();
                    emailService.sendAuctionWon(user.getEmail(), titolo, winner.getAmount());
                });

            } else {
                auctionDao.closeWithoutWinner(auction.getId());
            }
        }
    }


    private AuctionDto toDto(Auction auction) {
        List<BidDto> bidDtos = new ArrayList<>();
        if (auction.getBids() != null) {
            for (Bid b : auction.getBids()) {
                bidDtos.add(bidToDto(b));
            }
        }
        return new AuctionDto(
                auction.getId(),
                auction.getPostId(),
                auction.getStartingPrice(),
                auction.getCurrentBest(),
                auction.getEndDate(),
                auction.isClosed(),
                auction.getWinnerId(),
                bidDtos
        );
    }

    private BidDto bidToDto(Bid bid) {
        return new BidDto(
                bid.getId(),
                bid.getAuctionId(),
                bid.getUserId(),
                bid.getAmount(),
                bid.getPlacedAt()
        );
    }


    private int generateUniqueId() {
        int id;
        do {
            id = new Random().nextInt(89999) + 10000;
        } while (auctionDao.get(id).isPresent());
        return id;
    }
}