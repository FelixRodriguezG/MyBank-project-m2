# Bank Back (API Bancaria)

Aplicación REST de banca construida con Java 17 y Spring Boot 3.5, que gestiona cuentas (Checking, StudentChecking, Savings, CreditCard), usuarios (Admin, AccountHolder, ThirdParty) y transacciones, con seguridad JWT y MySQL.


## Tecnologías y dependencias
- Java 17, Maven
- Spring Boot 3.5.5
  - spring-boot-starter-web (REST)
  - spring-boot-starter-data-jpa (JPA/Hibernate)
  - spring-boot-starter-validation (Jakarta Validation)
  - spring-boot-starter-security (Security)
- JWT: io.jsonwebtoken:jjwt-*(0.11.5)
- MySQL: mysql-connector-j
- OpenAPI/Swagger: springdoc-openapi-starter-webmvc-ui (2.3.0)
- Lombok


## Modelo y base de datos
- Herencia JPA "table-per-subclass":
  - Tabla base: account (campos comunes)
  - Subclases: checking, savings, credit_card, student_checking (1:1 por id)
- Entidades usuario: account_holders, admins, third_parties
- Transacciones: transactions
- Value objects embebidos: Money (amount + currency), Address, PersonalData

Diagrama lógico simplificado en bank-back/BankUML.png.


## Reglas de negocio (resumen)
- Checking: mínimo 250, mantenimiento mensual 12, penaltyFee 40 si baja del mínimo.
- StudentChecking: sin mínimo ni mantenimiento (para titulares <24 años).
- Savings: mínimo configurable (≥100, por defecto 1000), interés anual (por defecto 0.0025, máx 0.5).
- CreditCard: límite crédito (100–100000, por defecto 100), interés mensual (≥0.1, por defecto 0.2).
- PenaltyFee para todas: 40.


## Seguridad y autenticación
- JWT stateless con Authorization: Bearer <token>
- Login: POST /api/auth/login
  - Body: { "username": "...", "password": "..." }
  - Respuesta: { token, expiresIn }
- Resolución de usuarios para login:
  - Admin: busca por username (tabla admins)
  - AccountHolder: usa name como username (tabla account_holders)
- Rutas públicas: /api/auth/**, /swagger-ui/**, /v3/api-docs/**


## Configuración y ejecución
1) MySQL local (DB se crea si no existe):
   - src/main/resources/application.properties
     - spring.datasource.url=jdbc:mysql://localhost:3306/bank-back?createDatabaseIfNotExist=true
     - spring.datasource.username=<tu_user>
     - spring.datasource.password=<tu_pass>
     - spring.jpa.hibernate.ddl-auto=update
     - security.jwt.secret=<cadena_larga_random>
2) Ejecutar:
   - Windows: `mvnw.cmd -DskipTests spring-boot:run`
   - Linux/Mac: `./mvnw -DskipTests spring-boot:run`
3) Swagger UI: http://localhost:8080/swagger-ui/index.html


## Datos de ejemplo (DataLoader)
Se insertan al arrancar si la BD está vacía:
- 4 AccountHolders (ACTIVEs):
  - "Alice Smith" (pwd: alice1234)
  - "Bob Johnson" (pwd: bob1234)
  - "Carol Young" (pwd: carol1234)
  - "Dave Student" (pwd: dave1234)
- 1 ThirdParty: name "AcmePayments", hashedKey = bcrypt("tp-secret")
  - IMPORTANTE: los endpoints de terceros esperan el valor hashedKey tal cual está en BD (no el texto plano)
- 8 cuentas: 2 por tipo (Checking, Savings, CreditCard, StudentChecking)

Consejo: si quieres un Admin al instante, puedes (a) activar el perfil "inmemory" para un admin in-memory (admin/123456), o (b) crear un admin con POST /api/admins (ver más abajo) y luego usar su username/password para login.


## Endpoints principales y ejemplos
A menos que se indique, todos requieren JWT en Authorization.

### 1) Autenticación
- POST /api/auth/login
```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"Alice Smith","password":"alice1234"}'
```
Respuesta:
```json
{ "token": "<JWT>", "expiresIn": 3600 }
```

### 2) AccountHolder (/api/holder)
- GET /api/holder/accounts
```bash
curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/holder/accounts
```
- GET /api/holder/accounts/{id}
```bash
curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/holder/accounts/1
```
- POST /api/holder/transfers
Body (TransferDTO): { senderId, receiverId, amount, secretKey }
```bash
curl -X POST http://localhost:8080/api/holder/transfers \
  -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" \
  -d '{"senderId":1,"receiverId":2,"amount":100.0,"secretKey":"2345"}'
```
Errores habituales: 403 (no eres dueño), 404 (cuenta no existe), 422 (fondos insuficientes).

### 3) Admin de usuarios (/api/admins)
- POST /api/admins (crear admin)
```bash
curl -X POST http://localhost:8080/api/admins \
  -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" \
  -d '{"name":"Root","username":"root","password":"root123"}'
```
- GET /api/admins, GET /api/admins/{id}, PUT /api/admins/{id}, DELETE /api/admins/{id}

### 4) Administración de cuentas (/api/accounts)
(restringido a ROLE_ADMIN)
- GET /api/accounts/me (rol AccountHolder): ver mis cuentas (azúcar sobre /api/holder/accounts)
- GET /api/accounts/owner?primaryOwnerId=1&secondaryOwnerId=2
- GET /api/accounts/primary-owner/{id}
- GET /api/accounts/secondary-owner/{id}
- GET /api/accounts/status/{status}  (ACTIVE|FROZEN)
- GET /api/accounts/type/{type}      (CHECKING|SAVINGS|CREDIT_CARD|STUDENT)
- POST /api/accounts/penalty/low-balance
- POST /api/accounts/penalty/student-negative
- POST /api/accounts/maintenance/checking
- POST /api/accounts/interest/savings
- POST /api/accounts/interest/credit-card
- DELETE /api/accounts/{id}

Ejemplo (como ADMIN):
```bash
# Login admin (si usas el creado arriba)
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"root","password":"root123"}'

# Aplicar mantenimiento a checking
curl -X POST http://localhost:8080/api/accounts/maintenance/checking \
  -H "Authorization: Bearer <JWT_ADMIN>"
```

### 5) ThirdParty (/api/third-party)
Cabecera obligatoria: X-Hashed-Key: <valor almacenado en BD>
- POST /api/third-party/transactions/deposit
```bash
curl -X POST http://localhost:8080/api/third-party/transactions/deposit \
  -H "X-Hashed-Key: <HASH_BCRYPT_EN_BD>" -H "Content-Type: application/json" \
  -d '{"accountId":1,"secretKey":"1234","amount":"50.00"}'
```
- POST /api/third-party/transactions/withdraw
```bash
curl -X POST http://localhost:8080/api/third-party/transactions/withdraw \
  -H "X-Hashed-Key: <HASH_BCRYPT_EN_BD>" -H "Content-Type: application/json" \
  -d '{"accountId":1,"secretKey":"1234","amount":"20.00"}'
```
Nota: el valor esperado es el hashedKey exacto (bcrypt) guardado; consúltalo en la tabla third_parties.

### 6) Transacciones (/api/transactions)
- GET /api/transactions?start=2025-01-01T00:00:00&end=2025-12-31T23:59:59
- GET /api/transactions/count?accountId=1&start=2025-01-01T00:00:00&end=2025-12-31T23:59:59


## Errores y estados
- 400 Bad Request: datos inválidos
- 401 Unauthorized: falta o token inválido
- 403 Forbidden: sin permisos o fallo de validación (secretKey/propietario)
- 404 Not Found: recurso inexistente
- 422 Unprocessable Entity: regla de negocio (fondos insuficientes, etc.)


## Desarrollo y pruebas
- Compilar: `mvn -DskipTests package`
- Ejecutar tests: `mvn test`
- Ejecutar en caliente: `mvn spring-boot:run`


## Notas operativas
- Si vienes de un esquema previo y aparece un CHECK inconsistente (p. ej. en checking), elimina el constraint antiguo o recrea la BD. Con `spring.jpa.hibernate.ddl-auto=update` no se borran constraints heredados.
- Swagger UI está habilitado; usa los ejemplos como base y ajusta según tu dataset.


## Licencia
MIT (ver LICENSE)
