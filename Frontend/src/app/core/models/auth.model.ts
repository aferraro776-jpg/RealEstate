import { User } from './user.model';

export interface AuthResponse {
    token: string;
    user: User;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    name: string;
    surname: string;
    email: string;
    password: string;
    birthDate: string;
    role: 'SELLER' | 'BUYER';
    otp: string;
    vatNumber?: string;
}

export interface ContactRequest {
    propertyId: number;
    fromName: string;
    fromEmail: string;
    fromPhone?: string;
    message: string;
}