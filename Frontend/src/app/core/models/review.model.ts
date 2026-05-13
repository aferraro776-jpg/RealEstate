export interface Review {
    id: number;
    propertyId: number;
    buyerId: number;
    buyerName: string;
    rating: number;
    comment: string;
    createdAt: string;
}