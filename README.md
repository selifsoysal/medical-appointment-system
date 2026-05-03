# Medical Appointment System

Hastaların doktorlardan randevu alabilmesini ve doktorların kendi takvimlerini yönetebilmesini sağlayan, **Spring Boot** ve **JSP** tabanlı bir web uygulamasıdır. 

## Proje Hakkında

Bu proje, hastane veya klinik ortamlarındaki randevu süreçlerini dijitalleştirmeyi amaçlar. Sistem, hem hastalar hem de doktorlar için ayrı paneller sunar. Hastalar uygun saat dilimlerini seçerek randevu oluşturabilirken, doktorlar kendi çalışma saatlerini (TimeSlot) belirleyebilir ve gelen randevuları görüntüleyebilir.

## Kullanılan Teknolojiler

- **Backend:** Java 21, Spring Boot 3.2.5, Spring MVC, Spring Data JPA
- **Frontend:** JSP (JavaServer Pages), JSTL, HTML, CSS
- **Veritabanı:** MySQL
- **Araçlar:** Maven, Lombok

## Özellikler

### Hastalar İçin:
- Sisteme kayıt olma ve giriş yapma (Patient Auth)
- Hasta paneli (Patient Dashboard)
- Profil bilgilerini görüntüleme ve güncelleme
- Doktorları listeleme ve uygun saat dilimleri üzerinden randevu alma
- Geçmiş ve aktif randevuları görüntüleme

### Doktorlar İçin:
- Sisteme giriş yapma (Doctor Auth)
- Doktor paneli (Doctor Dashboard)
- Randevu takvimini yönetme (Çalışma saatleri - TimeSlot - ekleme)
- Gelen randevuları ve hasta bilgilerini görüntüleme


## 📂 Proje Yapısı

- `model/`: Veritabanı tablolarına karşılık gelen Entity sınıfları (Doctor, Patient, Appointment, TimeSlot vb.).
- `repository/`: Spring Data JPA arayüzleri (Veritabanı işlemleri için).
- `service/`: İş kurallarının ve mantığının yürütüldüğü katman.
- `controller/`: HTTP isteklerini karşılayan ve ilgili JSP sayfalarına yönlendiren katman.
- `webapp/WEB-INF/jsp/`: Arayüz tasarımlarının (JSP) bulunduğu klasör.
- `webapp/css/`: Stillerin tutulduğu klasör.
