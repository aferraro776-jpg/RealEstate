export type RealEstateType =
    | 'APARTMENT'
    | 'VILLA'
    | 'GARAGE'
    | 'BUILDING_LOT'
    | 'NON_BUILDING_LOT';

export type PropertyCategory = 'APARTMENT' | 'VILLA' | 'HOUSE' | 'GARAGE' | 'LAND' | 'COMMERCIAL';
export type ListingType = 'SALE' | 'RENT';

export interface RealEstateRequest {
    type: RealEstateType;
    title: string;
    numberOfRooms: number;
    description: string;
    squareMetres: number;
    street: string;
    civicNumber: string;
    city: string;
    cap: string;
    province: string;
}

export interface PostCreateDto {
    title: string;
    description: string;
    currentPrice: number;
    photoUrls: { url: string }[];
    realEstate: RealEstateRequest;
}

export interface PostUpdateDto {
    title: string;
    description: string;
    currentPrice: number;
    realEstateId: number;
    photoUrls: { url: string }[];
}

export interface Post {
    id: number;
    title: string;
    description: string;
    previousPrice: number;
    currentPrice: number;
    createdAt: string;
    sellerId: number;
    realEstateId: number;
    photoUrls: { url: string }[];
}

export interface RealEstateDto {
    id: number;
    title: string;
    numberOfRooms: number;
    description: string;
    squareMetres: number;
    latit: number;
    longit: number;
    address: string;
    createdAt: string;
    type: string;
}

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
    realEstateId?: number;
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
    sortBy?: string;
    direction?: 'asc' | 'desc';
}

export const REAL_ESTATE_TYPE_LABELS: Record<RealEstateType, string> = {
    APARTMENT:        'Appartamento',
    VILLA:            'Villa',
    GARAGE:           'Box auto',
    BUILDING_LOT:     'Terreno edificabile',
    NON_BUILDING_LOT: 'Terreno non edificabile',
};

export const CATEGORY_LABELS: Record<PropertyCategory, string> = {
    APARTMENT:  'Appartamento',
    VILLA:      'Villa',
    HOUSE:      'Casa',
    GARAGE:     'Box auto',
    LAND:       'Terreno',
    COMMERCIAL: 'Commerciale',
};

export const LISTING_TYPE_LABELS: Record<ListingType, string> = {
    SALE: 'Vendita',
    RENT: 'Affitto',
};