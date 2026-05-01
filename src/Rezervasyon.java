import java.time.LocalDate;
public class Rezervasyon {
    public int rezervasyonNo;
    public Musteri musteri;
    public Oda oda;
    public LocalDate girisTarihi;
    public LocalDate cikisTarihi;
    public double toplamUcret;
    public String durum;

    public Rezervasyon(int rezervasyon, Musteri musteri, Oda oda, LocalDate girisTarihi, LocalDate cikisTarihi, double toplamUcret, String durum){
        this.rezervasyonNo = rezervasyon;
        this.musteri = musteri;
        this.oda = oda;
        this.girisTarihi = girisTarihi;
        this.cikisTarihi = cikisTarihi;
        this.toplamUcret = toplamUcret;
        this.durum = durum;

    }

    public int getRezervasyonNo(){
        return rezervasyonNo;
    }
    public void setRezervasyonNo(int rezervasyonNo){
        this.rezervasyonNo = rezervasyonNo;
    }

    public Musteri getMusteri(){
        return musteri;
    }
    public void setMusteri(Musteri musteri){
        this.musteri = musteri;
    }

    public Oda getOda(){
        return oda;
    }
    public void setOda(Oda oda){
        this.oda = oda;
    }

    public LocalDate getGirisTarihi(){
        return girisTarihi;
    }
    public void setGirisTarihi(LocalDate girisTarihi){
        this.girisTarihi = girisTarihi;
    }

    public LocalDate getCikisTarihi(){
        return cikisTarihi;
    }
    public void setCikisTarihi(LocalDate cikisTarihi){
        this.cikisTarihi = cikisTarihi;
    }

    public double getToplamUcret(){
        return toplamUcret;
    }
    public void setToplamUcret(double toplamUcret){
        this.toplamUcret = toplamUcret;
    }

    public String getDurum(){
        return durum;
    }
    public void setDurum(String durum){
        this.durum = durum;
    }

    @Override
    public String toString() {
        return "Rezervasyon{" +
                "rezervasyonNo=" + rezervasyonNo +
                ", musteri=" + musteri +
                ", oda=" + oda +
                ", girisTarihi=" + girisTarihi +
                ", cikisTarihi=" + cikisTarihi +
                ", toplamUcret=" + toplamUcret +
                ", durum='" + durum + '\'' +
                '}';
    }
    /*musteri + ve + oda +
İşte burası kodun en can alıcı noktası! Fark ettiysen musteri ve oda değişkenleri basit birer sayı veya metin değil, onlar birer Nesne (Object).

Java'da bir nesneyi metinlerle (String) + işaretiyle birleştirmeye kalktığında, Java arka planda otomatik olarak o nesnenin kendi toString() metodunu çağırır.
Yani sen:
", musteri=" + musteri yazdığında, Java aslında şunu yapar:
", musteri=" + musteri.toString()

Hatırlarsan bir önceki aşamada Musteri sınıfına da bir toString() eklemiştik. İşte o eklediğimiz metot tam da bu anda işe yarıyor! Eğer Musteri sınıfına toString yazmamış olsaydın, bu rezervasyonu ekrana yazdırdığında müşteri kısmında anlamsız bir hafıza adresi (Musteri@7a81...) çıkacaktı. Ama şimdi, müşterinin tüm detaylarını (Adı, TC'si vs.) rezervasyonun içine çok şık bir şekilde gömecek. */
}
