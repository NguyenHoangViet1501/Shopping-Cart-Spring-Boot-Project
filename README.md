# 🛒 Shopping Cart - Spring Boot Project

## 📖 Introduction
**Shopping Cart** is a simple e-commerce web application built using **Spring Boot** and **Spring Data JPA**.  
The project allows users to register, browse products, add items to their shopping cart, and proceed to checkout.  
Administrators can manage products, categories, and orders through a secure admin dashboard.  

This project demonstrates the core features of a real-world backend system, applying the **MVC architecture**, **role-based authentication**, and **database operations** using **Spring Data JPA**.

---

## 🚀 Features

### 👤 User Features
- Register, login, and manage user profile.
- Browse product listings and search by name or category.
- Add products to cart, update quantities, and remove items.
- View cart summary and complete checkout process.

### 🔧 Admin Features
- Manage products, categories, and orders.
- Add, update, delete product details.
- View all orders and customer information.

### ⚙️ System Features
- Authentication and Authorization using **Spring Security**.
- CRUD operations through **Spring Data JPA**.
- Form validation and global exception handling.
- Responsive user interface with **Bootstrap** and **Thymeleaf** templates.
- Clear multi-layered architecture: **Controller → Service → Repository**.

---

## 🧰 Tech Stack

| Layer | Technologies |
|-------|---------------|
| **Backend** | Java, Spring Boot, Spring MVC, Spring Data JPA |
| **Frontend** | Thymeleaf, HTML5, CSS3, Bootstrap |
| **Database** | MySQL |
| **Build Tool** | Maven |
| **Version Control** | Git & GitHub |
| **Testing Tools** | Postman, JUnit (optional) |

---

## 🏗️ Project Structure

```
Shopping-Cart-Spring-Boot-Project
├── src
│   ├── main
│   │   ├── java/com/example/shoppingcart
│   │   │   ├── controller/        # Web controllers (User & Admin)
│   │   │   ├── service/           # Business logic layer
│   │   │   ├── repository/        # Spring Data JPA interfaces
│   │   │   ├── model/             # Entity classes (Product, Category, User, Order, CartItem)
│   │   │   └── config/            # Security & application configuration
│   │   └── resources/
│   │       ├── templates/         # Thymeleaf HTML templates
│   │       ├── static/            # CSS, JS, and images
│   │       └── application.properties
│   └── test/                      # Unit and integration tests
└── pom.xml
```

---

## ⚙️ Installation & Setup Guide

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/NguyenHoangViet1501/Shopping-Cart-Spring-Boot-Project.git
cd Shopping-Cart-Spring-Boot-Project
```

### 2️⃣ Configure Database (MySQL)
- Create a new database, e.g. `shopping_cart_db`
- Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shopping_cart_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3️⃣ Build and Run
If you are using Maven:
```bash
mvn clean package
java -jar target/Shopping-Cart-Spring-Boot-Project-0.0.1-SNAPSHOT.jar
```

Or run directly from an IDE (IntelliJ / Eclipse) using the main class:
```
com.example.shoppingcart.ShoppingCartApplication
```

### 4️⃣ Access Application
- **User Interface:** http://localhost:8080  
- **Admin Interface:** http://localhost:8080/admin

(Default admin credentials can be defined in the database or configuration.)

---

## 🧪 API Endpoints (Example for REST Integration)

| Method | Endpoint | Description |
|--------|-----------|-------------|
| `GET` | `/products` | Get all products |
| `GET` | `/products/{id}` | Get product by ID |
| `POST` | `/cart/add/{productId}` | Add product to cart |
| `GET` | `/cart` | View current user's cart |
| `POST` | `/checkout` | Complete checkout process |
| `GET` | `/admin/products` | View all products (admin) |

---

## 🧠 Key Learning Points
- Applying **Spring MVC architecture** with a clean separation of layers.  
- Implementing **CRUD operations** using **Spring Data JPA**.  
- Using **Spring Security** for authentication and authorization.  
- Managing templates and server-side rendering with **Thymeleaf**.  
- Working with **MySQL** and handling database relations (OneToMany, ManyToOne).  
- Deploying and testing the project in a local development environment.

---

## 🪄 Future Improvements
- Integrate JWT authentication for a RESTful API version.  
- Add payment gateway integration (Stripe / PayPal).  
- Deploy to **Docker** or **AWS EC2** for production.  
- Improve UI/UX with **React** or **Angular** frontend.  
- Add unit and integration tests for services and controllers.

---

## 👨‍💻 Author
**Nguyễn Hoàng Việt**  
📧 hoangvit2k4@gmail.com  
🔗 [GitHub Profile](https://github.com/NguyenHoangViet1501)

---
