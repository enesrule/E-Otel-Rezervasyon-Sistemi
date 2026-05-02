[README.md](https://github.com/user-attachments/files/27303285/README.md)
# E-Otel Rezervasyon Sistemi

Konsol tabanlı bir otel rezervasyon yönetim sistemi. Java ile geliştirilmiştir.

---

## Gereksinimler

- Java 17 (OpenJDK Temurin 17.0.18)

---

## Çalıştırma Talimatları

### 1. Derleme
```bash
javac -encoding UTF-8 *.java
```

### 2. Çalıştırma
```bash
java Main
```

---

## Proje Yapısı

```
src/
├── Main.java               → Ana program, menü ve kullanıcı etkileşimi
├── Oda.java                → Oda sınıfı
├── Musteri.java            → Müşteri sınıfı
├── Rezervasyon.java        → Rezervasyon sınıfı
├── BeklemeListe.java       → Kuyruk tabanlı bekleme listesi (sıfırdan implement)
├── BeklemeDugumu.java      → Bekleme listesi düğümü
├── RezervasyonBST.java     → Binary Search Tree - tamamlanan rezervasyonlar
├── BSTDugumu.java          → BST düğümü
├── IntervalAgaci.java      → Interval Tree - tarih çakışma kontrolü
├── IntervalDugumu.java     → Interval Tree düğümü
```

---

## Menü Seçenekleri

| Seçenek | İşlem |
|---------|-------|
| 1 | Oda Ekle |
| 2 | Müşteri Ekle |
| 3 | Rezervasyon Yap |
| 4 | Müşterileri Listele |
| 5 | Odaları Listele |
| 6 | Rezervasyonları Listele |
| 7 | Rezervasyon İptal Et |
| 8 | Tamamlanan Rezervasyonları Listele |
| 9 | Çıkış |

---

## Kullanılan Veri Yapıları

### 1. HashMap (Java Collections)
- Oda, Müşteri ve Rezervasyon verilerini O(1) erişimle saklar.

### 2. BeklemeListe - Kuyruk (Sıfırdan implement edildi)
- Dolup taşan odalar için bekleme sırası tutar.
- FIFO mantığıyla çalışır: ilk gelen ilk alır.

### 3. RezervasyonBST - Binary Search Tree (Sıfırdan implement edildi)
- İptal edilen rezervasyonları giriş tarihine göre sıralı saklar.
- Kronolojik listeleme: O(n), Ekleme: O(log n) ortalama.

### 4. IntervalAgaci - Interval Tree
- Tarih çakışma kontrolünü O(log n) ile yapar.
- For döngüsüne göre çok daha hızlı.

---

## Veri Kalıcılığı

Program kapanırken veriler otomatik olarak CSV dosyalarına kaydedilir:
- `odalar.csv`
- `musteriler.csv`
- `rezervasyonlar.csv`

Program açılırken bu dosyalardan veriler otomatik yüklenir.

---

## Hata Yönetimi

- Olmayan müşteri veya oda ile rezervasyon yapılamaz.
- Geçersiz menü girişi yakalanır.
- Tarih çakışması varsa bekleme listesine alınır.
- CSV dosyası yoksa hata vermez, boş başlar.
