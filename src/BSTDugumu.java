import java.time.LocalDate;

public class BSTDugumu {
    Rezervasyon rezervasyon; //Rezervasyonun tüm bilgisi müşteri oda giriş çıkış tarihleri ve ücret bilgisini tutan nesne
    LocalDate girisTarihi; //Anahtar olarak kullanılacak giriş tarihi çünkü giriş tarihine göre karşılaştırcaz
    BSTDugumu sol; //Daha eski tarihler
    BSTDugumu sag; //Daha yeni tarihler

    public BSTDugumu(Rezervasyon rezervasyon){// yeni bir node oluştururken icşni doldururuz. Rezervasyon → class (referans tipi) , rezervasyon → bir nesnenin adresini (referansını) tutar
        this.rezervasyon = rezervasyon;
        this.girisTarihi = rezervasyon.getGirisTarihi();
        this.sol = null;
        this.sag = null;
    }


}

/* 
┌─────────────────────────┐
│ Rezervasyon bilgisi     │  ← asıl veri
│ Giriş tarihi (anahtar)  │  ← sıralama için
│ Sol kutunun adresi      │  ← daha eski tarihler
│ Sağ kutunun adresi      │  ← daha yeni tarihler
└─────────────────────────┘
*/