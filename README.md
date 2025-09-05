# Bank Back 🏦

## 📌 Descripción
**Bank Back** es un sistema bancario simplificado construido con **Java** y **Spring Boot**.  
Implementa diferentes tipos de cuentas (`Checking`, `StudentChecking`, `Savings`, `CreditCard`) y roles de usuario (`Admin`, `AccountHolder`, `ThirdParty`), siguiendo las mejores prácticas de **POO con herencia JPA** y una base de datos **MySQL**.  

El proyecto simula reglas reales del mundo bancario como:
- Saldos mínimos y penalizaciones.
- Intereses anuales y mensuales aplicados de forma *lazy* (cuando se consulta el balance).
- Conversión automática de Checking a StudentChecking para titulares jóvenes.
- Acceso seguro a cuentas según el tipo de usuario.
- Transferencias entre cuentas validadas con reglas de negocio.

Este proyecto forma parte del **Ironhack Bootcamp - Proyecto Final del Módulo 2**.

---

## 📊 Diagramas

### Diagrama de Clases (UML)

```plaintext
User (abstract)
 ├─ Admin
 ├─ AccountHolder { dateOfBirth, primaryAddress, mailingAddress? }
 └─ ThirdParty { hashedKey }

Address { street, city, country, zipCode }

Account (abstract) {
  id, balance: Money, secretKey, status: AccountStatus, createdAt,
  primaryOwner: AccountHolder, secondaryOwner?, penaltyFee
}
 ├─ Checking { minimumBalance=250, monthlyMaintenanceFee=12 }
 ├─ StudentChecking { /* no minBalance, no monthlyFee */ }
 ├─ Savings { minimumBalance>=100, interestRate<=0.5, lastInterestAppliedAt }
 └─ CreditCard { creditLimit<=100000, interestRate>=0.1, lastInterestAppliedAt }

Money { amount: BigDecimal, currency: String="EUR" }
AccountStatus = { ACTIVE, FROZEN }
```

### Diagrama de Casos de Uso (simplificado)

```plaintext
Actors:
  - Admin
  - AccountHolder
  - ThirdParty

Use Cases:
  Admin -> Crear cuentas
  Admin -> Consultar/modificar saldo
  Admin -> Crear/gestionar usuarios
  AccountHolder -> Consultar sus cuentas
  AccountHolder -> Transferir dinero
  ThirdParty -> Depositar/retirar fondos
```

---

## ⚙️ Configuración

### Requisitos
- **Java 17+**
- **Maven 3.8+**
- **MySQL 8+**
- Recomendado: Postman o Insomnia para pruebas de API

### Instalación
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/bank-back.git
   ```
2. Configurar la base de datos en `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bankdb
   spring.datasource.username=root
   spring.datasource.password=tu_password
   spring.jpa.hibernate.ddl-auto=update
   ```
3. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

---

## 🛠️ Tecnologías Usadas
- Java 17
- Spring Boot (Web, Data JPA, Validation, Security [opcional])
- MySQL
- JUnit & Spring Boot Test
- Maven
- Lombok

---

## 🌐 Controladores y Rutas

### Admin (`/api/admin`)
- `POST /holders` → Crear nuevo titular de cuenta  
- `GET /holders` → Listar todos los titulares  
- `GET /holders/{id}` → Consultar titular por id  
- `POST /users/third-party` → Crear nuevo usuario third-party  
- `GET /users/third-party/{id}` → Consultar third-party por id  
- `DELETE /users/third-party/{id}` → Eliminar third-party  

#### Cuentas
- `POST /accounts` → Crear nueva cuenta (Checking, Savings o CreditCard)  
- `GET /accounts` → Listar todas las cuentas  
- `GET /accounts/{id}` → Consultar cuenta por id  
- `PATCH /accounts/{id}/balance` → Modificar saldo de una cuenta  
- `PATCH /accounts/{id}/status` → Cambiar estado (ACTIVE/FROZEN)  
- `DELETE /accounts/{id}` → Eliminar cuenta  

---

### AccountHolder (`/api/holder`)
- `GET /accounts` → Listar mis cuentas (aplica intereses automáticamente)  
- `GET /accounts/{id}` → Consultar una de mis cuentas  
- `POST /transfers` → Transferir dinero entre cuentas  

**Body ejemplo transferencia:**
```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": "150.00",
  "targetOwnerName": "Alice Doe"
}
```

---

### ThirdParty (`/api/third-party`)
Requiere cabecera:  
```
X-Hashed-Key: <hash>
```

- `POST /transactions/deposit` → Depositar dinero en cuenta  
  ```json
  { "accountId": 2, "secretKey": "ABCD-1234", "amount": "120.00" }
  ```
- `POST /transactions/withdraw` → Retirar dinero de cuenta  
  ```json
  { "accountId": 2, "secretKey": "ABCD-1234", "amount": "50.00" }
  ```

---

## 🧪 Tests
- **Unit Tests**:  
  - Aplicación de `penaltyFee` al bajar del saldo mínimo.  
  - Aplicación de intereses anuales en `Savings`.  
  - Aplicación de intereses mensuales en `CreditCard`.  
  - Creación de `StudentChecking` si owner < 24.  

- **Integration Tests**:  
  - Flujo de transferencia válido (con validación de nombres y balances).  
  - Acceso restringido a cuentas (solo dueños).  
  - Endpoints de creación y consulta de cuentas/usuarios.  

---

## 🚨 Manejo de Errores
Respuestas estandarizadas en JSON:

```json
{
  "error": "INSUFFICIENT_FUNDS",
  "message": "Not enough balance in account",
  "path": "/api/holder/transfers",
  "timestamp": "2025-09-05T12:30:00"
}
```

- `404 Not Found` → Recurso inexistente  
- `403 Forbidden` → Acceso denegado  
- `409 Conflict` → Operación inválida por estado  
- `422 Unprocessable Entity` → Violación de regla de negocio  

---

## 🚀 Trabajo Futuro
- Sistema de detección de fraude (congelar cuenta en actividad sospechosa).  
- Despliegue en Heroku / Render / Railway.  
- Notificaciones en tiempo real e historial de transacciones.  
- Autenticación y autorización basada en roles con Spring Security & JWT.  

---

## 📚 Recursos
- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)  
- [Guía de Spring Data JPA](https://spring.io/guides/gs/accessing-data-jpa/)  
- [Documentación de MySQL](https://dev.mysql.com/doc/)  

---

## 📋 Gestión del Proyecto
- [Trello Board](https://trello.com/) *(enlace al tablero del proyecto)*

---

## 👥 Equipo
- **Tu Nombre** (Desarrollador)  
