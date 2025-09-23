## Detección de fraude (bonus)
- Congelar cuenta si:  
  - Transacciones en 24h > 150% del mayor total diario registrado  
  - Más de 2 transacciones en < 1 segundo
---

## Tipos de cuentas (4)
## Requisitos técnicos
- Backend en Java/Spring Boot  
- Persistencia en MySQL  
- Rutas GET, POST, PUT/PATCH y DELETE  
- Autenticación con Spring Security (JWT)  
Atributos clave: balance, secretKey, primaryOwner, secondaryOwner (opcional), minimumBalance, penaltyFee, monthlyMaintenanceFee, creationDate, status (FROZEN|ACTIVE).  
Reglas: mínimo 250; mantenimiento mensual 12; penalización 40 si cae por debajo del mínimo.
- Money (BigDecimal + Currency) para cálculos monetarios
### StudentChecking (Cuenta corriente estudiantil)
Igual que Checking pero sin minimumBalance ni monthlyMaintenanceFee.  
Usada automáticamente para titulares < 24 años.
## Entregables
Igual que Checking pero sin mantenimiento mensual y con interés anual.  
- interestRate por defecto: 0.0025; máximo: 0.5  
- minimumBalance por defecto: 1000; mínimo configurable: 100
- Documentación completa en README.md
Atributos: balance, owners, creditLimit, interestRate, penaltyFee.  
- creditLimit por defecto: 100; máximo configurable: 100000  
- interestRate por defecto: 0.2; mínimo configurable: 0.1  
- Interés aplicado mensualmente sobre saldos negativos (deuda)

---

## Usuarios (3)
- Admins  
- AccountHolders  
- ThirdParty  

### AccountHolders (Titulares)
Acceden con JWT (no Basic Auth).  
Pueden ver sus cuentas y transferir entre cuentas si hay fondos suficientes.

### Admins
Administran usuarios y cuentas; pueden listar/crear/borrar administradores, crear titulares y terceros, y ejecutar procesos de mantenimiento/intereses/penalizaciones.

### ThirdParty (Terceros)
Cuentan con hashedKey y nombre.  
Operan depósitos/retiros sobre cuentas válidas indicando cabecera X-Hashed-Key y la secretKey de la cuenta.

---

## Transacciones unificadas
Toda operación que modifica el saldo crea una entidad Transaction.

Tipos (TransactionType):
- DEPOSIT: depósitos de terceros y aumentos de saldo  
- WITHDRAWAL: retiros de terceros y disminuciones de saldo  
- TRANSFER: transferencias entre cuentas (origen y destino)  
- INTEREST_PAYMENT: intereses (Savings anual, CreditCard mensual)  
- MAINTENANCE_FEE: mantenimiento mensual (Checking, cuando corresponde)  
- PENALTY_FEE: penalización por bajo/negativo saldo según reglas  

Notas:
- Los métodos internos updateBalance(...) en servicios de cuenta también registran Transaction (uso interno, no expuestos por API).  
- Se eliminan los tipos THIRD_PARTY_* en favor de DEPOSIT/WITHDRAWAL.

---

## Acceso y autenticación
- JWT stateless: Authorization: Bearer <token>  
- Login: POST /api/auth/login, body { username, password }  
- Rutas públicas: /api/auth/**, /swagger-ui/**, /v3/api-docs/**

---

## Capacidades por rol (resumen de endpoints)
- AccountHolder (/api/holder):
  - GET /accounts (mis cuentas)  
  - GET /accounts/{id} (si es propio)  
  - POST /transfers (TransferDTO con senderId, receiverId, amount, secretKey)
- Admin (/api/accounts, /api/admins):
  - Gestión de admins: CRUD  
  - Gestión operativa de cuentas:
    - POST /api/accounts/penalty/low-balance → PENALTY_FEE en Checking/Savings bajo mínimo  
    - POST /api/accounts/penalty/student-negative → PENALTY_FEE en Student con saldo negativo  
    - POST /api/accounts/maintenance/checking → MAINTENANCE_FEE si corresponde  
    - POST /api/accounts/interest/savings → INTEREST_PAYMENT  
    - POST /api/accounts/interest/credit-card → INTEREST_PAYMENT  
- ThirdParty (/api/third-party):
  - POST /transactions/deposit (X-Hashed-Key, body con accountId, amount, secretKey) → DEPOSIT  
  - POST /transactions/withdraw (X-Hashed-Key, body con accountId, amount, secretKey) → WITHDRAWAL
- Transacciones (/api/transactions):
  - GET listado por rango de fechas  
  - GET conteo por cuenta y rango

---


### Detección de fraude  
El sistema debe congelar la cuenta si detecta fraude.  
Patrones:  
- Transacciones en 24 h > 150% del mayor total diario registrado  
- Más de 2 transacciones en una cuenta en <1 segundo  

### Despliegue en servidor externo  
- Subir la app a un servicio externo (Heroku/Firebase)  

---

## Requisitos técnicos  

- Backend en **Java/Spring Boot**  
- Datos en **MySQL**  
- Al menos 1 ruta GET, POST, PUT/PATCH y DELETE  
- (Opcional) Autenticación con Spring Security  
- Tests unitarios e integración  
- Manejo robusto de errores  
- Usar **Money** para divisas y **BigDecimal** para cálculos  

---

## Entregables  

- API REST funcional en servidor local  
- Repositorio en GitHub  
- Diagramas UML (casos de uso y clases)  
- Documentación completa en **README.md**  
