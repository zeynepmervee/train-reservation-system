# Train Reservation System

Yüksek trafikli tren rezervasyon senaryolarını modellemek amacıyla geliştirilen full-stack bir web uygulamasıdır. Kullanıcılar istasyon ve tarih seçerek sefer arayabilir, trenin vagonlarını yatay koltuk planında görüntüleyebilir, birden fazla koltuk seçebilir ve yolcu bilgileriyle geçici rezervasyon oluşturabilir.

Projenin odak noktası yalnızca bir bilet arayüzü hazırlamak değil; aynı koltuğun eş zamanlı olarak birden fazla kullanıcıya satılmasını önlemek, süreli koltuk tutmak ve rezervasyon durumlarını tutarlı biçimde yönetmek gibi gerçek sistem problemlerini ele almaktır.

## Öne Çıkan Özellikler

- Kalkış, varış ve tarihe göre sefer arama
- Tren, vagon ve koltuk yönetimi
- Ekonomi ve Business vagon desteği
- Vagon tipine göre otomatik koltuk üretimi
- Bir işlemde birden fazla koltuk seçebilme
- Seçilen her koltuk için yolcu bilgisi girişi
- Standart, öğrenci ve 65+ yolcu tipleri
- Yolcu tipine göre bilet fiyatı hesaplama
- Beş dakikalık geçici koltuk tutma süresi
- Süresi geçen rezervasyonları otomatik olarak serbest bırakma
- Dolu koltukları yolcu bilgisine göre farklı renklerle gösterme
- Aynı sefer ve koltuk için çakışan aktif biletleri veritabanı seviyesinde engelleme
- JPA `@Version` ile optimistic locking altyapısı
- Flyway ile sürümlü veritabanı migration yönetimi
- Docker Compose ile PostgreSQL kurulumu
- Responsive Vue 3 arayüzü

## Kullanılan Teknolojiler

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- Bean Validation
- Flyway
- Lombok
- Maven

### Frontend

- Vue 3
- Vite
- Vue Router
- Pinia
- JavaScript
- HTML ve CSS

### Veritabanı ve ortam

- PostgreSQL 16
- Docker ve Docker Compose
- Git / GitHub
- Postman

## Mimari

Uygulama başlangıç aşamasında yönetilebilirliği korumak amacıyla modüler monolit olarak tasarlanmıştır.

```text
Vue 3 Frontend
       |
       | REST API
       v
Spring Boot Backend
       |
       | Service + Transaction
       v
Spring Data JPA / Flyway
       |
       v
PostgreSQL
```

Backend, özellik bazlı paketlere ayrılmıştır:

```text
station      İstasyon işlemleri
train        Tren işlemleri
wagon        Vagon yönetimi
seat         Koltuk modeli
trip         Sefer oluşturma ve arama
reservation  Rezervasyon, yolcu ve bilet işlemleri
```

## Rezervasyon Akışı

1. Kullanıcı kalkış, varış ve tarih bilgisiyle sefer arar.
2. Seçilen sefere ait vagonlar ve koltuklar görüntülenir.
3. Kullanıcı istediği sayıda boş koltuk seçer.
4. Her koltuk için yolcu bilgileri girilir.
5. Backend bütün yolcuları tek transaction içerisinde işler.
6. Rezervasyon `PENDING`, biletler `HELD` durumunda oluşturulur.
7. Koltuklar beş dakika boyunca diğer kullanıcılar için kapatılır.
8. Süresi dolan geçici rezervasyonlar otomatik olarak `EXPIRED` yapılır ve koltuklar yeniden kullanılabilir hâle gelir.

## Eş Zamanlı Koltuk Seçimi

Aynı koltuğun iki farklı kullanıcı tarafından alınmasını engellemek için birden fazla koruma katmanı kullanılmıştır:

- Rezervasyon işlemleri `@Transactional` olarak yürütülür.
- Aynı istek içinde aynı koltuğun iki kez seçilmesi servis katmanında engellenir.
- Seçilen koltuğun ilgili seferdeki trene ait olduğu doğrulanır.
- PostgreSQL partial unique index ile bir seferde aynı koltuk için yalnızca bir aktif `HELD` veya `CONFIRMED` bilet oluşturulabilir.
- Rezervasyon entity'sinde `@Version` alanı ile optimistic locking altyapısı bulunur.

Bu sayede uygulama seviyesindeki kontroller aşılsa bile veritabanı son güvenlik katmanı olarak çakışan kaydı reddeder.

## Proje Yapısı

```text
train-reservation-system/
├── trainreservation/              # Spring Boot backend
├── train-reservation-frontend/    # Vue 3 frontend
├── docker-compose.yml             # PostgreSQL container tanımı
└── README.md
```

## Kurulum

### Gereksinimler

- Java 17
- Node.js 22.18 veya üzeri
- Docker Desktop
- Git

### 1. Projeyi klonlayın

```bash
git clone https://github.com/zeynepmervee/train-reservation-system.git
cd train-reservation-system
```

### 2. PostgreSQL'i başlatın

```bash
docker compose up -d
```

Container durumunu kontrol etmek için:

```bash
docker ps
```

### 3. Backend'i çalıştırın

Windows:

```powershell
cd trainreservation
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
cd trainreservation
./mvnw spring-boot:run
```

Backend şu adreste çalışır:

```text
http://localhost:8080
```

### 4. Frontend'i çalıştırın

Yeni bir terminal açın:

```bash
cd train-reservation-frontend
npm install
npm run dev
```

Frontend şu adreste çalışır:

```text
http://localhost:5173
```

## Temel API Endpoint'leri

| Metot | Endpoint | Açıklama |
|---|---|---|
| `GET` | `/api/v1/stations` | İstasyonları listeler |
| `POST` | `/api/v1/stations` | Yeni istasyon oluşturur |
| `GET` | `/api/v1/trains` | Trenleri listeler |
| `POST` | `/api/v1/trains` | Yeni tren oluşturur |
| `GET` | `/api/v1/trains/{trainId}/wagons` | Trenin vagon ve koltuklarını listeler |
| `POST` | `/api/v1/trains/{trainId}/wagons` | Vagonu ve koltuklarını otomatik oluşturur |
| `POST` | `/api/v1/trips` | Yeni sefer oluşturur |
| `GET` | `/api/v1/trips/search` | İstasyon ve tarihe göre sefer arar |
| `GET` | `/api/v1/trips/{id}` | Sefer ayrıntısını getirir |
| `GET` | `/api/v1/trips/{id}/wagons` | Sefere özel koltuk doluluklarını getirir |
| `POST` | `/api/v1/reservations` | Çoklu yolcu rezervasyonu oluşturur |

## Veritabanı Migration'ları

Flyway migration dosyaları aşağıdaki klasördedir:

```text
trainreservation/src/main/resources/db/migration
```

Mevcut migration sırası:

```text
V1  Stations
V2  Trains
V3  Wagons and Seats
V4  Trips
V5  Reservations and Tickets
```

Çalıştırılmış migration dosyaları değiştirilmez; her şema değişikliği yeni bir migration dosyasıyla eklenir.

## Mevcut Durum

Proje aktif olarak geliştirilmektedir. Bu sürümde sefer arama, çoklu koltuk seçimi, yolcu bilgileri, geçici rezervasyon, süre aşımı ve dolu koltuk gösterimi çalışmaktadır.

## Planlanan Geliştirmeler

- Rezervasyon tamamlama ve ödeme simülasyonu
- Rezervasyon iptal işlemi
- Fiyatlandırmayı Strategy Pattern yapısına dönüştürme
- Redis ile distributed seat lock
- Spring Security ve JWT tabanlı kullanıcı girişi
- Admin ve müşteri rol ayrımı
- Swagger / OpenAPI dokümantasyonu
- JUnit, Mockito ve Testcontainers ile kapsamlı testler
- Ara durak ve segment bazlı koltuk uygunluğu
- E-posta bildirimleri ve bekleme listesi

## Geliştirici

**Zeynep Merve Koyuncu**

- GitHub: [zeynepmervee](https://github.com/zeynepmervee)

