# Email + JWT Authentication API (Java 17)

Spring Boot REST API: email orqali ro'yxatdan o'tish, email OTP tasdiqlash, login va JWT bilan himoyalangan endpointlar.

## Texnologiyalar

- Java 17 (Amazon Corretto 17 tavsiya qilinadi)
- Spring Boot 3.5.16
- Maven
- Lombok 1.18.48
- PostgreSQL 16
- Spring Data JPA / Hibernate
- Spring Security
- JJWT 0.13.0
- Spring Mail (SMTP)
- Springdoc OpenAPI / Swagger UI 2.10.0

## 1. Talablar

- Amazon Corretto JDK 17
- Maven 3.6.3 yoki undan yuqori
- Docker Desktop (yoki lokal PostgreSQL 16)
- SMTP email akkaunti (Gmail ishlatilsa App Password)

## 2. PostgreSQL'ni ishga tushirish

Docker ishlatsangiz, PostgreSQL 16 konteynerini quyidagi buyruq bilan ishga tushiring:

```bash
docker run --name email-jwt-postgres -e POSTGRES_DB=email_jwt_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16-alpine
```

Bu buyruq `email_jwt_db` nomli database'ni yaratadi. Konteyner avvaldan mavjud bo'lsa, uni qayta yaratish shart emas.

Docker ishlatmasangiz, lokal PostgreSQL'da quyidagini bajaring:

```sql
CREATE DATABASE email_jwt_db;
```

Database ma'lumotlari `src/main/resources/application.properties` ichida environment variable orqali sozlanadi.

## 3. SMTP email sozlamasi

Gmail ishlatilsa:

1. Google Account'da 2-Step Verification'ni yoqing.
2. App Password yarating.
3. Oddiy Gmail parolini emas, App Password'ni `MAIL_PASSWORD` sifatida kiriting.

Environment variable'larni IDE Run Configuration yoki terminal orqali belgilang.

### Windows PowerShell

```powershell
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:MAIL_FROM="your-email@gmail.com"
```

### Linux / macOS

```bash
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
export MAIL_FROM="your-email@gmail.com"
```

SMTP sozlanmasa, register endpoint email yubora olmaydi va xatolik qaytaradi.

## 4. JWT secret

`application.properties` ichida development uchun Base64 secret mavjud. Production serverda `JWT_SECRET` environment variable orqali kamida 256-bit (32 byte) tasodifiy kalit kiriting. Secret'ni Git'ga joylamang.

Tokenning standart amal qilish muddati:

- `app.jwt.expiration-ms=900000`
- 900000 millisekund = 15 daqiqa

Email OTP muddati standart holatda 10 daqiqa.

## 5. Loyihani ishga tushirish

```bash
mvn clean spring-boot:run
```

Jar yaratish:

```bash
mvn clean package
java -jar target/email-jwt-auth-1.0.0.jar
```

## 6. Swagger

Brauzerda oching:

- http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Login'dan keyin qaytgan access tokenni Swagger UI'dagi **Authorize** maydoniga kiriting. `Bearer ` prefiksini qo'lda yozish shart emas; Swagger HTTP Bearer sxemasini o'zi qo'shadi.

## 7. API ketma-ketligi

### 7.1 Register

`POST /api/auth/register`

```json
{
  "firstName": "Ali",
  "lastName": "Valiyev",
  "email": "ali@example.com",
  "password": "Ali123456!"
}
```

Server foydalanuvchini `USER` roli bilan yaratadi va emailga 6 xonali OTP yuboradi. Email tasdiqlanmaguncha login qilib bo'lmaydi.

### 7.2 Emailni tasdiqlash

`POST /api/auth/verify-email`

```json
{
  "email": "ali@example.com",
  "code": "123456"
}
```

`code` qiymatini email orqali olasiz. Kod bir martalik va muddati 10 daqiqa.

### 7.3 Kodni qayta yuborish

`POST /api/auth/resend-verification`

```json
{
  "email": "ali@example.com"
}
```

### 7.4 Login

`POST /api/auth/login`

```json
{
  "email": "ali@example.com",
  "password": "Ali123456!"
}
```

Javob namunasi:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInMs": 900000,
  "user": {
    "id": 2,
    "firstName": "Ali",
    "lastName": "Valiyev",
    "email": "ali@example.com",
    "role": "USER",
    "emailVerified": true
  }
}
```

### 7.5 JWT bilan mening profilim

`GET /api/users/me`

Header:

```http
Authorization: Bearer YOUR_ACCESS_TOKEN
```

### 7.6 Admin: barcha userlar

`GET /api/admin/users`

Faqat `ADMIN` role'iga ruxsat beriladi.

## 8. DataLoader

Birinchi ishga tushishda `DataLoader` avtomatik `ADMIN` yaratadi:

- Email: `admin@example.com`
- Password: `Admin12345!`

Bu demo qiymatlar. Ishga tushirishdan oldin `ADMIN_EMAIL` va `ADMIN_PASSWORD` environment variable'larini o'zingizga moslang. DataLoader admin mavjud bo'lsa, qayta yaratmaydi.

## 9. Package strukturasi

```text
uz.sfera.auth
├── config
│   ├── DataLoader.java
│   └── OpenApiConfig.java
├── controller
│   ├── AuthController.java
│   └── UserController.java
├── dto
│   ├── ApiError.java
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── MessageResponse.java
│   ├── RegisterRequest.java
│   ├── ResendCodeRequest.java
│   ├── UserResponse.java
│   └── VerifyEmailRequest.java
├── entity
│   ├── AppUser.java
│   ├── EmailVerificationToken.java
│   └── Role.java
├── exception
│   └── GlobalExceptionHandler.java
├── repository
│   ├── AppUserRepository.java
│   └── EmailVerificationTokenRepository.java
├── security
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
└── service
    ├── AuthService.java
    ├── CustomUserDetailsService.java
    ├── EmailService.java
    └── JwtService.java
```

## 10. Test

```bash
mvn test
```

Testlar H2 in-memory database'dan foydalanadi; alohida PostgreSQL test bazasi talab qilinmaydi.

## 11. Muhim xavfsizlik eslatmalari

- Parollar BCrypt bilan hash qilinadi.
- OTP kodi database'da BCrypt hash ko'rinishida saqlanadi.
- Email tasdiqlanmaguncha oddiy user login qila olmaydi.
- JWT HMAC imzosi tekshiriladi va token muddati nazorat qilinadi.
- Production'da `ddl-auto=validate` yoki migratsiya vositasi (Flyway/Liquibase) ishlating.
- Production'da demo admin paroli va JWT secret'ni albatta almashtiring.
- Bu loyiha Access Token beradi; Refresh Token va token revoke/blacklist mexanizmi kiritilmagan.
- OTP endpointlarini production'da rate limit bilan himoyalash tavsiya etiladi.
