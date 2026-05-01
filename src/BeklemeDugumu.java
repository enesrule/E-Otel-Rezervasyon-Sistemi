import java.time.LocalDate;

public class BeklemeDugumu {
    //O(1)
    int rezervasyonNo;
    Musteri musteri; //Musteri nesnesini saklamak için bir değişken ekledik. Bu sayede bekleme düğümünde sadece müşteri kimlik numarası değil, tüm müşteri bilgilerine erişebiliriz.
    int odaNo;
    LocalDate girisTarihi;
    LocalDate cikisTarihi;
    BeklemeDugumu sonraki; //sonraki düğümü göstermek için

    public BeklemeDugumu(int rezervasyonNo, Musteri musteri, int odaNo, LocalDate girisTarihi,  LocalDate cikisTarihi){
        this.rezervasyonNo = rezervasyonNo;
        this.musteri = musteri;
        this.odaNo = odaNo;
        this.girisTarihi = girisTarihi;
        this.cikisTarihi = cikisTarihi;
        this.sonraki = null; //yeni oluşturulan düğümün sonraki göstergesi başlangıçta null olur.
    }

    @Override
    public String toString() {
        return "BeklemeDugumu{"+
                "rezervasyonNo= " + rezervasyonNo +
                ", musteri = " + musteri + 
                ", odaNo= " + odaNo +
                ", girisTarihi= " + girisTarihi +
                ", cikisTarihi= " + cikisTarihi +
                '}';
    }
}
