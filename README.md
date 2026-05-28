# Digital Banking Spring Boot Angular Security AI

Projet final universitaire : application Digital Banking avec backend Spring Boot,
frontend Angular, securite JWT, dashboard Chart.js et chatbot AI/RAG.

## Stack

- Java 21
- Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security
- JWT avec JJWT
- Lombok
- MySQL en local, H2 pour les tests
- Springdoc OpenAPI Swagger
- Angular 21
- Chart.js avec ng2-charts
- OpenAI Responses API via variable d'environnement
- Telegram Bot API optionnelle

## Structure

```text
src/main/java/ma/enset/digitalbanking
├── ai
├── audit
├── config
├── dtos
├── entities
├── enums
├── exceptions
├── mappers
├── repositories
├── security
├── services
└── web

frontend/
└── src/app
```

## Variables d'environnement

Copier `.env.example` vers `.env` et remplacer les valeurs sensibles.

```bash
cp .env.example .env
```

Variables principales :

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=digital_banking
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION_MS=86400000
OPENAI_API_KEY=
OPENAI_MODEL=gpt-5.2
TELEGRAM_BOT_TOKEN=
TELEGRAM_WEBHOOK_SECRET=
```

Ne jamais commiter `.env`, `application-local.yml`, `node_modules` ou `target`.

## Backend

Installer Maven si necessaire, puis lancer :

```bash
mvn clean test
mvn spring-boot:run
```

URL backend :

```text
http://localhost:8080
```

Swagger :

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/swagger-ui/index.html
```

Comptes crees automatiquement au demarrage :

```text
admin / admin123
user / user123
```

## Authentification

Login :

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Utiliser ensuite le token JWT :

```text
Authorization: Bearer <token>
```

Routes publiques :

- `POST /api/auth/login`
- `POST /api/auth/register`
- Swagger/OpenAPI
- `POST /api/telegram/webhook` avec secret Telegram

Toutes les autres routes sont protegees.

## API principales

Clients :

- `GET /api/customers`
- `GET /api/customers/{id}`
- `POST /api/customers`
- `PUT /api/customers/{id}`
- `DELETE /api/customers/{id}`
- `GET /api/customers/search?keyword=`

Comptes :

- `GET /api/accounts`
- `GET /api/accounts/{accountId}`
- `POST /api/accounts/current`
- `POST /api/accounts/saving`
- `GET /api/accounts/{accountId}/operations`
- `GET /api/accounts/{accountId}/pageOperations?page=&size=`

Operations :

- `POST /api/accounts/debit`
- `POST /api/accounts/credit`
- `POST /api/accounts/transfer`

Dashboard :

- `GET /api/dashboard/stats`

Chatbot :

- `POST /api/chat`
- `GET /api/chat/history`

## Frontend Angular

Installer les dependances :

```bash
cd frontend
npm install
npm start
```

URL frontend :

```text
http://localhost:4200
```

Fonctionnalites implementees :

- Login/register
- Interceptor JWT
- Guard de routes
- Dashboard avec graphiques
- Gestion clients
- Gestion comptes
- Debit, credit, transfert
- Chatbot Angular connecte au backend

## Chatbot AI/RAG

Le chatbot utilise :

- documents locaux dans `src/main/resources/rag`
- statistiques dashboard
- clients et comptes visibles par l'utilisateur authentifie
- OpenAI via `OPENAI_API_KEY`

Si `OPENAI_API_KEY` est vide, le backend retourne une reponse locale de secours.

## Telegram optionnel

Configurer :

```text
TELEGRAM_BOT_TOKEN=
TELEGRAM_WEBHOOK_SECRET=
```

Webhook backend :

```text
POST /api/telegram/webhook
Header: X-Telegram-Bot-Api-Secret-Token: <TELEGRAM_WEBHOOK_SECRET>
```

## Tests

Backend :

```bash
mvn test
```

Frontend :

```bash
cd frontend
npm install
npm run build
```

## Captures

Ajouter les captures du rapport dans :

```text
docs/screenshots/
```

Captures recommandees :

- Swagger
- Login Angular
- Dashboard
- Liste clients
- Liste comptes
- Details compte
- Chatbot

## Git

Chaque partie fonctionnelle est commitee separement pour faciliter la verification
de l'avancement par le professeur.
