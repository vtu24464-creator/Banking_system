# Week 8 Microservices Tasks — Complete Guide

## Project Structure
```
microservices/
├── Discovery-MS/     ← Task 8.1  Eureka Server         (port 8761)
├── User-MS/          ← Task 8.1  Eureka Client          (port 8081, 8083)
├── AccountMng-MS/    ← Task 8.1  Eureka Client          (port 8082)
└── Gateway-MS/       ← Task 8.3  Spring Cloud Gateway   (port 8080)
```

---

## Prerequisites
- Java 17+
- Maven 3.8+
- Apache ActiveMQ 5.x running on port 61616 (Task 8.5)
  Download: https://activemq.apache.org/download
  Start: `bin/activemq start`

---

## Task 8.1 & 8.2 — Startup Order

> **Always start Discovery-MS first!**

```bash
# 1. Start Eureka Server
cd Discovery-MS && mvn spring-boot:run

# 2. Start User-MS
cd User-MS && mvn spring-boot:run

# 3. Start AccountMng-MS
cd AccountMng-MS && mvn spring-boot:run

# 4. Open Eureka Dashboard → verify both clients appear
http://localhost:8761
```

**Expected dashboard**: Both `USER-MS` and `ACCOUNTMNG-MS` appear under "Instances currently registered with Eureka".

---

## Task 8.3 — API Gateway

```bash
# 5. Start Gateway-MS
cd Gateway-MS && mvn spring-boot:run
```

### Manual Routing (YAML-based, active by default)
| Request | Forwarded To |
|---|---|
| GET http://localhost:8080/api/users | User-MS /users |
| GET http://localhost:8080/api/users/1 | User-MS /users/1 |
| GET http://localhost:8080/api/accounts | AccountMng-MS /accounts |
| GET http://localhost:8080/api/accounts/user/1 | AccountMng-MS /accounts/user/1 |

### Automatic Routing (Eureka-based, no config needed)
| Request | Forwarded To |
|---|---|
| GET http://localhost:8080/user-ms/users | User-MS /users |
| GET http://localhost:8080/accountmng-ms/accounts | AccountMng-MS /accounts |

---

## Task 8.4 — Load Balancer

Start a second instance of User-MS on a different port:

```bash
# In a new terminal:
cd User-MS
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8083"
```

Both instances register as `USER-MS` in Eureka. Spring Cloud LoadBalancer
will **round-robin** between port 8081 and 8083.

### Postman Test (repeat this request multiple times)
```
GET http://localhost:8080/api/users
```
Watch the `servedByPort` field in the response alternate between `8081` and `8083`.

---

## Task 8.5 — JMS Inter-Service Communication

Ensure ActiveMQ is running, then:

### Trigger a JMS request from User-MS → AccountMng-MS
```
POST http://localhost:8080/api/users/jms/request-accounts/1
```
1. User-MS sends a JMS message to `account.request.queue`
2. AccountMng-MS consumes it, fetches accounts for userId=1
3. AccountMng-MS replies to `account.response.queue`
4. User-MS consumer receives and logs the reply

### Broadcast a user event
```
POST http://localhost:8080/api/users/jms/event?msg=UserCreated:Alice
```

### JMS Queue Flow
```
User-MS (Producer)
    │
    ▼  account.request.queue
AccountMng-MS (Consumer + Producer)
    │
    ▼  account.response.queue
User-MS (Consumer)
```

### ActiveMQ Web Console (monitor queues)
```
http://localhost:8161/admin
Default credentials: admin / admin
```

---

## All Postman Endpoints Summary

| Method | URL | Description |
|--------|-----|-------------|
| GET | http://localhost:8761 | Eureka Dashboard |
| GET | http://localhost:8080/api/users | All users (via Gateway) |
| GET | http://localhost:8080/api/users/1 | User by ID |
| GET | http://localhost:8080/api/users/health | User-MS health |
| GET | http://localhost:8080/api/accounts | All accounts (via Gateway) |
| GET | http://localhost:8080/api/accounts/1 | Account by ID |
| GET | http://localhost:8080/api/accounts/user/1 | Accounts for userId=1 |
| GET | http://localhost:8080/api/accounts/health | AccountMng-MS health |
| GET | http://localhost:8080/user-ms/users | Auto-route via Eureka |
| GET | http://localhost:8080/accountmng-ms/accounts | Auto-route via Eureka |
| POST | http://localhost:8080/api/users/jms/request-accounts/1 | JMS trigger |
| POST | http://localhost:8080/api/users/jms/event?msg=test | JMS broadcast |
