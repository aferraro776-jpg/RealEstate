# hw26 — Frontend Angular

Frontend in **Angular 18 (standalone components)** per il progetto d'esame
*Web Applications A.A. 2025/26* — applicazione di annunci di vendita/affitto
di immobili.

## Stack

- Angular 18 standalone, TypeScript strict
- Bootstrap 5 + Bootstrap Icons (responsive)
- HttpClient + interceptor per JWT
- Routing con guard per i 3 ruoli (`ADMIN`, `SELLER`, `BUYER`)
- 7 regole CSS personalizzate in `src/styles.scss`

## Avvio in locale

```bash
npm install
npm start
```

L'app gira su <http://localhost:4200>.

## Configurare il backend

Il file `src/environments/environment.ts` contiene l'URL base del backend:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
};
```

Modificalo se il tuo Spring Boot gira su un'altra porta. In produzione usa
`environment.prod.ts` (default: `/api`, utile per servire frontend e backend
dietro lo stesso reverse proxy).

## Endpoint REST attesi dal frontend

Il backend (Spring Boot + DAO + Postgres) deve esporre — sotto `/api` — gli
endpoint qui sotto. Tutti i payload sono JSON. Le rotte protette richiedono
header `Authorization: Bearer <jwt>`.

### Auth
| Metodo | Path | Body | Ruoli |
| --- | --- | --- | --- |
| POST | `/auth/register` | `{ username, email, password, role }` | guest |
| POST | `/auth/login` | `{ username, password }` | guest |

Risposta: `{ token, user: { id, username, email, role, banned } }`

### Annunci (Properties)
| Metodo | Path | Body | Ruoli |
| --- | --- | --- | --- |
| GET  | `/properties?q=&city=&category=&listingType=&minPrice=&maxPrice=&minSquareMeters=&sort=` | — | guest |
| GET  | `/properties/:id` | — | guest |
| GET  | `/properties/mine` | — | SELLER |
| POST | `/properties` | `Partial<Property>` | SELLER |
| PUT  | `/properties/:id` | `Partial<Property>` | SELLER, ADMIN |
| DELETE | `/properties/:id` | — | SELLER, ADMIN |
| PATCH | `/properties/:id/lower-price` | `{ price }` | SELLER |
| POST | `/properties/:id/contact` | `{ fromName, fromEmail, fromPhone?, message }` | guest |
| POST | `/properties/:id/promote/facebook` | `{}` → `{ postUrl }` | SELLER |

### Recensioni
| Metodo | Path | Body | Ruoli |
| --- | --- | --- | --- |
| GET  | `/properties/:id/reviews` | — | guest |
| POST | `/properties/:id/reviews` | `{ rating, comment }` | BUYER |
| DELETE | `/reviews/:id` | — | BUYER (autore), ADMIN |

### Aste
| Metodo | Path | Body | Ruoli |
| --- | --- | --- | --- |
| GET  | `/properties/:id/auction` | — | guest |
| POST | `/properties/:id/auction` | `{ startPrice, endsAt }` | SELLER |
| GET  | `/auctions/:id/bids` | — | guest |
| POST | `/auctions/:id/bids` | `{ amount }` | BUYER |
| POST | `/auctions/:id/close` | — | SELLER, ADMIN |

### Utenti (admin)
| Metodo | Path | Body | Ruoli |
| --- | --- | --- | --- |
| GET  | `/users` | — | ADMIN |
| PATCH | `/users/:id/ban` | `{ banned: boolean }` | ADMIN |
| PATCH | `/users/:id/role` | `{ role }` | ADMIN |
| DELETE | `/users/:id` | — | ADMIN |

## Modello dati

Vedi `src/app/core/models/index.ts` per i tipi TypeScript completi
(`User`, `Property`, `Review`, `Auction`, `Bid`, ecc.).

## CORS

Sul backend Spring Boot abilita CORS per `http://localhost:4200` durante lo
sviluppo, ad esempio:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override public void addCorsMappings(CorsRegistry r) {
    r.addMapping("/api/**")
     .allowedOrigins("http://localhost:4200")
     .allowedMethods("*")
     .allowCredentials(true);
  }
}
```

## Funzionalità coperte (riferimento alla traccia)

- Registrazione e login (ruoli BUYER / SELLER; admin pre-esistente nel DB)
- Annunci con foto, descrizione, prezzo, m², posizione Google Maps, recensioni
- Venditori: CRUD annunci, ribasso prezzo (vecchio prezzo barrato), contatti,
  aste, promozione su Facebook
- Acquirenti: ricerca con filtri (vendita/affitto, categoria, prezzo, m²),
  ordinamento per prezzo o metratura, recensioni, modulo di contatto
- Amministratore: modifica/cancella tutto, banna utenti, promuove utenti ad
  amministratori
- Sito responsive (Bootstrap)
- 7 regole CSS personalizzate
- Integrazione esterna: Google Maps (iframe embed)

## Note tecniche

- Usa **lazy loading** per le pagine (`loadComponent`)
- Usa **signals** per lo stato locale dei componenti
- Token JWT salvato in `localStorage` e iniettato in tutte le richieste
- Risposta `401` → logout automatico + redirect al login
