export type PropertyCategory =
    | 'APARTMENT'
    | 'VILLA'
    | 'HOUSE'
    | 'GARAGE'
    | 'LAND'
    | 'COMMERCIAL';

export type ListingType = 'SALE' | 'RENT';

export interface Property {
    id: number;
    code: string;
    title: string;
    description: string;
    category: PropertyCategory;
    listingType: ListingType;
    price: number;
    oldPrice?: number | null;
    squareMeters: number;
    address: string;
    city: string;
    latitude: number;
    longitude: number;
    photos: string[];
    sellerId: number;
    sellerName?: string;
    createdAt: string;
}

export interface PropertyFilters {
    q?: string;
    city?: string;
    category?: PropertyCategory;
    listingType?: ListingType;
    minPrice?: number;
    maxPrice?: number;
    minSquareMeters?: number;
    sort?: 'price-asc' | 'price-desc' | 'sqm-desc' | 'newest';
}

export const CATEGORY_LABELS: Record<PropertyCategory, string> = {
    APARTMENT: 'Appartamento',
    VILLA: 'Villa',
    HOUSE: 'Casa',
    GARAGE: 'Box auto',
    LAND: 'Terreno edificabile',
    COMMERCIAL: 'Commerciale',
};

export const LISTING_TYPE_LABELS: Record<ListingType, string> = {
    SALE: 'Vendita',
    RENT: 'Affitto',
};