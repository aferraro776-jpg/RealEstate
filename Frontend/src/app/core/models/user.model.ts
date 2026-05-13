export type Role = 'ADMIN' | 'SELLER' | 'BUYER';

export interface User {
    id: number;
    name: string;
    surname: string;
    email: string;
    role: Role;
    banned: boolean;
}

export const ROLE_LABELS: Record<Role, string> = {
    ADMIN: 'Amministratore',
    SELLER: 'Venditore',
    BUYER: 'Acquirente',
};