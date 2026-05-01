public class Musteri {  //public class → Bu sınıfı başka dosyalardan, başka classlardan kullanabilmek için. Yani bu classtan nesne üretmek için public yapıyoruz. Eğer public olmazsa sadece aynı dosya içinden erişilebilir olurdu.
    private int musteriId;
    private String musteriAdSoyad;   //private field'lar → Verinin kontrolsüz değiştirilmesini engellemek için private yapıyoruz. Yani bu field'lara sadece getter ve setter metodlarıyla erişilebilir olur.
    private String musteriTelefon;
    private String musteriEmail;
    private String musteriTcKimlikNo;
    private String musteriAdres;

    public Musteri(int musteriId, String musteriAdSoyad, String musteriTelefon, String musteriEmail, String musteriTcKimlikNo, String musteriAdres) { //constructor → Bu constructor, nesne oluşturulurken tüm bilgilerin sağlanmasını zorunlu kılar. Yani bir müşteri nesnesi oluşturulurken bu bilgilerin tamamı verilmelidir.
        this.musteriId = musteriId;
        this.musteriAdSoyad = musteriAdSoyad;
        this.musteriTelefon = musteriTelefon;
        this.musteriEmail = musteriEmail;
        this.musteriTcKimlikNo = musteriTcKimlikNo;
        this.musteriAdres = musteriAdres; 
        
    }

    public int getMusteriId() {
        return musteriId;
    }
    public void setMusteriId(int musteriId) {
        this.musteriId = musteriId;
    }
    public String getMusteriAdSoyad() {
        return musteriAdSoyad;
    }
    public void setMusteriAdSoyad(String musteriAdSoyad) {
        this.musteriAdSoyad = musteriAdSoyad;
    }

    public String getMusteriTelefon() {
        return musteriTelefon;
    }
    public void setMusteriTelefon(String musteriTelefon) {
        this.musteriTelefon = musteriTelefon;
    }

    public String getMusteriEmail() {
        return musteriEmail;
    }
    public void setMusteriEmail(String musteriEmail) {
        this.musteriEmail = musteriEmail;
    }

    public String getMusteriTcKimlikNo() {
        return musteriTcKimlikNo;
    }
    public void setMusteriTcKimlikNo(String musteriTcKimlikNo) {
        this.musteriTcKimlikNo = musteriTcKimlikNo;
    }
    
    public String getMusteriAdres() {
        return musteriAdres;
    }
    public void setMusteriAdres(String musteriAdres) {
        this.musteriAdres = musteriAdres;
    }

    @Override
    public String toString() {
        return "Musteri{" +
                "id=" + musteriId +
                ", adSoyad='" + musteriAdSoyad + '\'' +
                ", telefon='" + musteriTelefon + '\'' +
                ", email='" + musteriEmail + '\'' +
                ", tcKimlikNo='" + musteriTcKimlikNo + '\'' +
                ", adres='" + musteriAdres + '\'' +
                '}';
    }
/*Main sınıfı içerisinde müşteriyi direkt ekrana yazdırmak istersen: System.out.println(musteri1);
Konsolda şöyle anlamsız bir hafıza (RAM) adresi görürsün: Musteri@7a81197d

Ama böyle yaptığımızda toString() metodunu eklersen, Java artık o nesneyi ekrana yazdırırken referans adresini değil, senin belirlediğin bu metni kullanır. Çıktı anında şuna dönüşür:

Musteri{id=1, adSoyad='Ahmet Yılmaz', telefon='0555...', ...}*/
}  

/* this.musteriAdres: Buradaki this kelimesi "bu nesne" anlamına gelir. Yani nesnenin kendi kalıcı hafızasıdır.

musteriAdres: Bu ise senin formdan (veya konsoldan) kullanıcıdan aldığın ve metoda gönderdiğin geçici girdi (parametre) değeridir.
Kullanıcının formdan girdiği o geçici bilgiyi al, bu nesnenin içindeki kalıcı hafıza çekmecesine (this) kaydet.*/


//Override: Java'da bir sınıfın, üst sınıfından (parent class) veya arayüzünden (interface) miras aldığı bir metodu kendi ihtiyaçlarına göre yeniden tanımlamasına "override" denir. Override işlemi, alt sınıfın (child class) üst sınıfın metodunu geçersiz kılarak kendi versiyonunu sağlamasına olanak tanır. Bu sayede alt sınıf, üst sınıfın sağladığı genel davranışı özelleştirebilir veya tamamen değiştirebilir. Override edilen metodun imzası (yani adı, parametreleri ve dönüş tipi) üst sınıftaki metodla aynı olmalıdır. Override işlemi, genellikle @Override anotasyonu ile belirtilir, bu da derleyiciye bu metodun gerçekten bir üst sınıf metodunu geçersiz kıldığını doğrulama imkanı verir.
/*Java'da sen kodlarken fark etmesen de, oluşturduğun her yeni sınıf (örneğin senin yazdığın Musteri sınıfı), arka planda Object adında dev ve evrensel bir "Ata Sınıftan" (Parent Class) miras alır.

Bu ata sınıfın (Object) içinde standart bir toString() metodu zaten vardır. Ancak bu standart metot senin Müşteri nesnenin içindeki adı, soyadı veya TC'yi bilemez. O yüzden sen müşteriyi ekrana yazdırmak istediğinde sadece o nesnenin bilgisayardaki ruhsuz RAM adresini ekrana basar (Örn: Musteri@7a81197d).

Sen @Override Yazdığında Ne Olur?
Sen kendi koduna toString() yazıp başına da @Override eklediğinde Java'ya tam olarak şu emri vermiş olursun:

"Ey Java! Ata sınıftan (Object) bana miras kalan o standart ve işe yaramaz toString() metodunu unut. Ben onu ezip geçiyorum (override) ve kendi kurallarımı koyuyorum. Artık biri bu müşteriyi ekrana yazdırmak isterse, benim yazdığım bu yeni metni (TC, Ad, Soyad) göstereceksin!" */

/*1. "Kendim bir metot oluştursam (örneğin yazdir()) daha mantıklı değil mi?"
Kesinlikle yapabilirsin! Gidip public String bilgileriGetir() adında bir metot yazabilirdin. Ancak toString() metodunun çok özel bir "sihri" vardır: Java'nın içine entegredir.

Diyelim ki kendi metodunu yazdın. Müşteriyi ekrana yazdırmak için kodda şunu yapman gerekir: 
System.out.println(musteri1.bilgileriGetir()); // Kendi metodunu elle çağırmak zorundasın
Ama toString() metodunu ezip geçersen, Java'nın o sihirli mekanizması devreye girer. Sen sadece nesnenin adını yazarsın:
System.out.println(musteri1);Bunu yazdığın anda Java arka planda gizlice "Aa bu bir nesne, ben bunu metne çevirmek için otomatik olarak içindeki toString() metodunu çağırayım" der. Yani toString(), Java'nın otomatik olarak tanıdığı ve çağırdığı standart bir dildir.*/