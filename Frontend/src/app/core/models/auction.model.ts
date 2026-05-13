export interface Auction {
    id: number;
    propertyId: number;
    startPrice: number;
    currentBid: number;
    endsAt: string;
    active: boolean;
    bidsCount: number;
}

export interface Bid {
    id: number;
    auctionId: number;
    buyerId: number;
    buyerName: string;
    amount: number;
    createdAt: string;
}