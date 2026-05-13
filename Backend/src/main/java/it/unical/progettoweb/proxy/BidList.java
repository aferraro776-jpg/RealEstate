package it.unical.progettoweb.proxy;

import it.unical.progettoweb.model.Bid;
import java.util.List;

public class BidList implements BidCollection {

    private final List<Bid> bids;

    public BidList(List<Bid> bids) {
        this.bids = bids;
    }

    @Override
    public List<Bid> getBids() {
        return bids;
    }
}