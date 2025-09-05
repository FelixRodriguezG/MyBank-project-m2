# Bank Back  
M2 - Semana 6 - 1 al 5 de Septiembre  

**Día 5 - Proyecto Práctico Módulo 2**  

## Resumen del proyecto  
Este proyecto debe completarse de manera **individual**. No está permitido colaborar con compañeros en esta tarea. Si necesitas ayuda, debes contactar con tu equipo de instrucción.  

El proyecto está diseñado para ser desafiante y debe completarse lo más exhaustivamente posible.  

En este proyecto construirás un **sistema bancario**. Debes cumplir con todos los siguientes requisitos:  

---

## Requisitos  

El sistema debe tener **4 tipos de cuentas**:  
- StudentChecking  
- Checking  
- Savings  
- CreditCard  

### Checking (Cuenta corriente)  
Las cuentas corrientes deben tener:  
- Balance  
- `secretKey`  
- Propietario principal (PrimaryOwner)  
- Propietario secundario opcional (SecondaryOwner)  
- Saldo mínimo (minimumBalance)  
- Comisión por penalización (penaltyFee)  
- Comisión mensual de mantenimiento (monthlyMaintenanceFee)  
- Fecha de creación (creationDate)  
- Estado (FROZEN, ACTIVE)  

### StudentChecking (Cuenta corriente estudiantil)  
Son idénticas a las cuentas corrientes excepto que **NO tienen**:  
- Comisión mensual de mantenimiento  
- Saldo mínimo  

### Savings (Cuenta de ahorros)  
Son idénticas a las cuentas corrientes excepto que:  
- **NO** tienen comisión mensual de mantenimiento  
- **Sí** tienen interés (`interestRate`)  

### CreditCard (Tarjeta de crédito)  
Las tarjetas de crédito deben tener:  
- Balance  
- Propietario principal (PrimaryOwner)  
- Propietario secundario opcional (SecondaryOwner)  
- Límite de crédito (`creditLimit`)  
- Interés (`interestRate`)  
- Comisión por penalización (`penaltyFee`)  

---

## Usuarios  

El sistema debe tener **3 tipos de usuarios**:  
- **Admins**  
- **AccountHolders**  
- **ThirdParty**  

### AccountHolders (Titulares de cuentas)  
Deben poder acceder **solo a sus cuentas** usando credenciales correctas con **Basic Auth**.  
Tienen:  
- Nombre  
- Fecha de nacimiento  
- Dirección principal (`primaryAddress`, clase aparte)  
- Dirección postal opcional (`mailingAddress`)  

### Admins  
- Solo tienen nombre.  

### ThirdParty (Terceros)  
- Tienen un `hashedKey` y un nombre.  
- Deben ser añadidos por un Admin.  

---

## Creación de cuentas  

- **Admins** pueden crear nuevas cuentas: Checking, Savings o CreditCard.  

### Savings  
- Interés por defecto: **0.0025**  
- Máximo interés configurable: **0.5**  
- Saldo mínimo por defecto: **1000**  
- Mínimo configurable: **100**  

### CreditCards  
- Límite de crédito por defecto: **100**  
- Máximo configurable: **100000**  
- Interés por defecto: **0.2**  
- Mínimo configurable: **0.1**  

### CheckingAccounts  
- Si el propietario es menor de 24 años → **StudentChecking**  
- Si no → **Checking**  
- Saldo mínimo: **250**  
- Comisión mensual: **12**  

---

## Intereses y comisiones  

### PenaltyFee  
- Todas las cuentas: **40**  
- Si el balance baja del mínimo → se descuenta automáticamente  

### InterestRate  
- **Savings**: interés anual  
- **CreditCards**: interés mensual  

---

## Acceso a cuentas  

- **Admins**:  
  - Ver y modificar cualquier balance  

- **AccountHolders**:  
  - Ver su balance  
  - Transferir dinero entre cuentas (si hay fondos suficientes)  
  - Deben indicar: nombre del propietario (Primary o Secondary) + id de la cuenta destino  

- **Third-Party Users**:  
  - Pueden enviar/recibir dinero  
  - Deben estar en la base de datos  
  - Deben incluir en la cabecera: `hashedKey`, cantidad, id de cuenta y `secretKey`  

---

## Funcionalidades extra (bonus)  

### Detección de fraude  
El sistema debe congelar la cuenta si detecta fraude.  
Patrones:  
- Transacciones en 24h > 150% del mayor total diario registrado  
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
