# Foodie Express – Enterprise Full-Stack Food Delivery & Health-Aware Platform

An enterprise-grade, health-aware food ordering and delivery platform built with **Spring Boot 3 (Java 17)** and **React 18 (Vite)**. Foodie Express features role-based access control, a clinical dietary & medical condition guardrail engine, live WebSocket order tracking, simulated payments, printable GST tax invoices, and multi-portal operational dashboards.

---

## 🌟 Key Features

### 🥗 Health & Medical Condition Guardrail Engine (Core Differentiator)
- **Medical Condition Profiles:** Support for Diabetes, Hypertension, Heart Disease, PCOS, Celiac / Gluten Intolerance, Lactose Intolerance, and Kidney-Friendly diets.
- **Allergen Detection:** Real-time checking for Nuts, Dairy, Soy, Shellfish, Eggs, and Gluten.
- **Clinical Nutritional Badges:** Every dish embeds Calories, Protein, Carbs, Sugar, Sodium, and Glycemic Index (GI).
- **"Safe For My Health" Toggle:** One-click storefront filter that hides contraindicated dishes and allergen conflicts based on the logged-in user's profile.
- **Interactive Cart Health Advisory:** Evaluates cart additions in real time; warns users of medical conflicts and suggests certified safe alternatives from the same restaurant.

### 👥 Multi-Role Portals & RBAC
1. **Customer Experience:**
   - Multi-restaurant storefront with cuisine and dietary filters.
   - Dish customization modal (e.g., "No Added Sugar", "Low Salt", "Extra Paneer").
   - Persistent cart synced with backend.
   - Promo coupon validation (`WELCOME50`, `HEALTHY20`, `DIABETICCARE`).
   - Simulated Razorpay/UPI/Card/COD payments.
   - Live real-time WebSocket order tracking with simulated GPS rider progress.
   - Printable GST Tax Invoices.
2. **Restaurant Owner Dashboard (`/restaurant-dashboard`):**
   - Live incoming kitchen queue with audio/visual alerts.
   - Order lifecycle actions (`Accept`, `Mark Preparing`, `Ready for Pickup`, `Reject`).
   - Dish stock availability toggles (`In Stock` / `Sold Out`).
3. **Delivery Partner Portal (`/delivery-dashboard`):**
   - Online/Offline toggle.
   - Active delivery dispatch queue with pickup/drop locations.
   - Status transitions (`Picked Up`, `Delivered`) and simulated GPS coordinate emitter.
4. **Super Admin Console (`/admin`):**
   - KPI metrics cards (Total GMV, Active Orders, Total Users, Active Restaurants).
   - Restaurant status toggles and coupon management.
   - Platform-wide order audit logs.

---

## 🛠️ Tech Stack

- **Backend:**
  - Java 17, Spring Boot 3.3.5
  - Spring Security with JWT (15-minute access token + refresh token rotation)
  - Spring Data JPA with H2 file persistence (`dev`) and PostgreSQL compatibility (`prod`)
  - Spring WebSocket (STOMP over SockJS)
  - Caffeine Cache for menu & dietary tags
  - OpenAPI 3 / Swagger (`springdoc-openapi`)
  - RFC-7807 ProblemDetail error responses
- **Frontend:**
  - React 18, Vite
  - Context API (`AuthContext`, `HealthProfileContext`, `CartContext`)
  - Lucide React icons
  - Custom responsive styling with dark/light theme support and print stylesheet

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Apache Maven 3.8+
- Node.js 18+ & npm

### 1. Run the Backend
```bash
cd backend
mvn clean spring-boot:run
```
- API Base URL: `http://localhost:8080/`
- Swagger UI Documentation: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON Spec: `http://localhost:8080/v3/api-docs`

### 2. Run the Frontend
```bash
cd frontend
npm install
npm run dev
```
- Frontend Application: `http://localhost:5173/`

---

## 🔑 Demo Accounts

All demo accounts use the password: `Password@123`

| Role | Email | Password | Primary Portal |
| :--- | :--- | :--- | :--- |
| **Customer** | `customer@foodie.com` | `Password@123` | Storefront & Health Profile |
| **Restaurant Owner** | `owner@foodie.com` | `Password@123` | `/restaurant-dashboard` |
| **Delivery Partner** | `rider@foodie.com` | `Password@123` | `/delivery-dashboard` |
| **Super Admin** | `admin@foodie.com` | `Password@123` | `/admin` |

---

## 🧪 Testing

Run backend unit and integration tests:
```bash
cd backend
mvn test
```
Build frontend production bundle:
```bash
cd frontend
npm run build
```
