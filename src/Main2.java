import java.io.*;
import java.time.LocalDate;
import java.util.HashMap; // HashMap verileri depolamak için,
import java.util.Scanner; // Scanner kullanıcıdan girdi almak için,

public class Main2 {
            static HashMap<Integer, Oda> odalar = new HashMap<>();//Key, value > değişken adı
            static HashMap<Integer, Musteri> musteriler = new HashMap<>();
            static HashMap<Integer, Rezervasyon> rezervasyonlar = new HashMap<>();//Aktif rezervasyonlae
            static HashMap<Integer, BeklemeListe> beklemeListeleri = new HashMap<>(); //Oda numarasına göre bekleme listesi tutmak için bir HashMap oluşturduk. Key olarak oda numarasını, value olarak o odanın bekleme listesini saklayacağız.
            static RezervasyonBST tamamlananRezervasyonlar = new RezervasyonBST(); //RezervasyonBST tamamlanan rezervasyonlari kronolojik sirada saklamak icin. Static oldugu icinde tum sistem burayi kullanabilir tek bir agac oldu
            static IntervalAgaci intervalAgaci = new IntervalAgaci();

        public static void main(String[] args) {
            //Neden HashMap? Oda numarasını biliyorsan odalar.get(101) diyerek anında o odaya ulaşırsın. Liste olsaydı tek tek aramak gerekirdi. new RezervasyonBST() diyerek boş bir BST ağacı oluşturduk ve tamamlananRezervasyonlar değişkenine atadık.
/*Neden Integer? → Müşteri numarası tam sayı, musteriler.get(123) diye erişeceksin
Neden Musteri? → İçinde Musteri nesnesi saklayacaksın, başka bir şey değil
Neden musteriler? → Bu dolabın adı, sonradan bu isimle eriseceksin
 */
                //sistem açılınca dosyalardan verileri yükleyelim
                odaVerileriniYukle();
                musteriVerileriYukle();
                rezervasyonVerileriYukle();

            Scanner scanner = new Scanner(System.in);
            System.out.println("E-Otel Rezervasyon Sistemine Hosgeldiniz!");
            int secim;
            do {
                System.out.println("1. Oda Ekle");
                System.out.println("2. Musteri Ekle");
                System.out.println("3. Rezervasyon Yap");
                System.out.println("4. Musterileri Listele");
                System.out.println("5. Odalari Listele");
                System.out.println("6. Rezervasyonlari Listele");
                System.out.println("7. Iptal Edilicek Rezervasyonlari Listele");
                System.out.println("8. Tammalanan Rezervasyonlari Listele");
                System.out.println("9. Cikis");
                System.out.println("Seciminizi yapiniz:");
                try {
                    secim = scanner.nextInt();
                    } catch (Exception e) {
                            System.out.println("Gecersiz giris! Lutfen bir sayi giriniz.");
                            scanner.nextLine();
                            secim = 0;
                            break;
                        }
                //Kullanıcıya seçenekleri gösterir ve nextInt() ile klavyeden girilen tam sayıyı yakalayıp secim değişkenine atar.
                switch (secim) {
                    case 1://Oda ekleme işlemi için kullanıcıdan gerekli bilgileri alacağız.
                        System.out.println("Oda numarasini giriniz:");
                        int odaNo = scanner.nextInt();
                        scanner.nextLine(); // Temizlik için
                        System.out.println("Oda tipini giriniz:");
                        String tip = scanner.next();
                        scanner.nextLine(); // Temizlik için
                        System.out.println("Oda kapasitesini giriniz:");
                        int kapasite = scanner.nextInt();
                        scanner.nextLine(); // Temizlik için
                        System.out.println("Oda fiyatini giriniz:");
                        double odaFiyat = scanner.nextDouble();
                        scanner.nextLine(); // Temizlik için
                        System.out.println("Oda musait mi? (true/false)");
                        boolean odaMusait = scanner.nextBoolean();
                        System.out.println("Oda aciklamasini giriniz:");
                        String aciklama = scanner.next();
                        scanner.nextLine(); // Temizlik için

                        Oda oda = new Oda(odaNo, tip, kapasite, odaFiyat, odaMusait, aciklama);//Oda class'ından bir nesne ürettik ve constructor'ına kullanıcıdan aldığımız bilgileri gönderdik ve bu nesneyi oda değişkenine atadık.
                        odalar.put(odaNo, oda);
                        System.out.println("Oda eklendi: " + oda);
                        break;

                    case 2://Müşteri ekleme işlemi için kullanıcıdan gerekli bilgileri alacağız.
                        System.out.println("Musteri numarasini giriniz:");
                        int musteriId = scanner.nextInt();//nextInt() kullanıcıdan tam sayı girişi bekler ve bu değeri musteriNo değişkenine atar.
                        scanner.nextLine(); //nextInt()'den sonra nextLine() kullanarak boş satırı temizliyoruz. nextInt() sadece sayıyı okur, ardından gelen enter tuşunu okumaz ve bu da bir sonraki nextLine() çağrısında boş bir satır olarak algılanır. Bu satırı ekleyerek bu sorunu çözüyoruz.
                        System.out.println("Musteri adini giriniz:");
                        String adSoyad = scanner.nextLine(); //next() boşluk görür görmez okumayı durdurur, yani sadece tek kelime alır. Eğer isim ve soyisim gibi birden fazla kelime girmek istiyorsan nextLine() kullanmalısın.
                        System.out.println("Musteri telefonunu giriniz:");
                        String telefon = scanner.next();
                        scanner.nextLine(); // Temizlik için
                        System.out.println("Musteri emailini giriniz:");
                        String email = scanner.next();
                        scanner.nextLine(); // Temizlik için
                        System.out.println("Musteri TC kimlik numarasini giriniz:");
                        String tcKimlikNo = scanner.next();
                        scanner.nextLine(); // Temizlik için
                        System.out.println("Musteri adresini giriniz:");
                        String musteriAdres = scanner.next();
                        scanner.nextLine(); // Temizlik için
                        Musteri musteri = new Musteri(musteriId, adSoyad, telefon, email, tcKimlikNo, musteriAdres);//Musteri class'ından bir nesne ürettik ve constructor'ına kullanıcıdan aldığımız bilgileri gönderdik ve bu nesneyi musteri değişkenine atadık.
                        musteriler.put(musteriId, musteri); //musteriId = anahtar, musteri = değer(Birinci parametre → anahtarı, yani dolabın hangi gözüne koyacağını söylüyorsun İkinci parametre → o göze ne koyacağını söylüyorsun yani içi müşteri bilgileriyle (ad, soyad, telefon.....) dolu olan çantanın (nesnenin) ta kendisidir.)  musteriler HashMap'ine musteriId'yi anahtar olarak, musteri nesnesini de değer olarak ekledik. Artık bu müşteri bilgilerine musteriler.get(musteriId) diyerek kolayca ulaşabiliriz. O kutuyu HashMap dolabına koyduk.
                        System.out.println("Musteri eklendi: " + musteri);
                        break;


                    case 3://Rezervasyon yapma işlemi için kullanıcıdan gerekli bilgileri alacağız.
                        System.out.println("Rezervasyon numarasini giriniz:");
                        int rezervasyonNo = scanner.nextInt();
                        scanner.nextLine(); // Temizlik için
                        //Müşteri no girince HashMap'ten o müşteriyi çekiyoruz ve rezervasyona atıyoruz.
                        System.out.println("Musteri numarasini giriniz:");// rezervasyonda musteri bir nesne olarak tutulduğu için kullanıcıdan musteri numarasını alıp o numaraya karşılık gelen Musteri nesnesini musteriler HashMap'inden çekmemiz gerekiyor.
                        //musteri Bir Nesnedir (Sınıf Türü): Sınıfında public Musteri musteri; yazıyor. Yani Java senden düz bir sayı (örneğin müşteri numarası olan 5'i) beklemiyor. Senden adı, soyadı, TC kimliği ve telefonu olan bütün bir Müşteri paketini bekliyor. Kullanıcının klavyeden girdiği "5" numarası müşteri nesnesi değildir; sadece o müşteriyi bulmamıza yarayan bir ipucudur (ID).
                        int musteriNo = scanner.nextInt();
                        scanner.nextLine(); // Temizlik için
                        Musteri rezervasyonMusteri = musteriler.get(musteriNo); //musteriler HashMap'inden musteriNo anahtarına karşılık gelen Musteri nesnesini alır ve rezervasyonMusteri değişkenine atar.komutu devreye giriyor. Bu komut bilgisayara şunu der: Git o bizim yukarıda oluşturduğumuz musteriler defterini aç. İçinden 5 numaralı (musteriNo) satırı bul ve oradaki Müşteri nesnesini (adıyla sanıyla) alıp bana getir.

                        if(rezervasyonMusteri == null){
                            System.out.println("HATA: Sistemde " + musteriNo + " numarali bir musteri bulunamadi!");
                            break; // İşlemi iptal et ve ana menüye (switch-case dışına) dön
                        }

                        //Oda no girince HashMap'ten o odayı çekiyoruz ve rezerv
                        System.out.println("Oda numarasini giriniz:");
                        int rezervasyonOdaNo = scanner.nextInt();
                        scanner.nextLine(); // Temizlik için
                        Oda rezervasyonOda = odalar.get(rezervasyonOdaNo); //odalar HashMap'inden rezervasyonOdaNo anahtarına karşılık gelen Oda nesnesini alır ve rezervasyonOda değişkenine atar.komutu devreye giriyor. Bu komut bilgisayara şunu der: Git o bizim yukarıda oluşturduğumuz odalar defterini aç. İçinden 101 numaralı (rezervasyonOdaNo) satırı bul ve oradaki Oda nesnesini (tipiyle, fiyatıyla vs.) alıp bana getir.
                        //case 1 de Oda oda = new Oda(...)diyerek nesne oluşturduk. Oda class'indan bir nesne urettik ve constructor'ina kullanicidan aldigimiz bilgileri gonderdik ve bu nesneyi oda degiskenine atadik. Simdi de rezervasyonOda degiskenine atiyoruz. Boylece rezervasyonOda artik o odanin tüm bilgilerini (tipi, fiyatı, müsaitligi vs.) iceren bir nesne oluyor. Rezervasyon yaparken o odanin fiyatini bilmemiz gerekiyor, müsait olup olmadigini bilmemiz gerekiyor, tipi ne bilmemiz gerekiyor, işte bu yüzden odayi komple cekiyoruz.

                        // 1. ÖNCE GÜVENLİK KONTROLÜ: Oda sistemde var mı?
                        if (rezervasyonOda == null) {
                            System.out.println("HATA: Sistemde " + rezervasyonOdaNo + " numarali bir oda bulunamadi!");
                            break; // İşlemi iptal et ve ana menüye (switch-case dışına) dön
                        }

                        System.out.println("Giris tarihini giriniz (YYYY-MM-DD):");
                        String girisTarihiStr = scanner.next(); //"2024-01-15" string olarak alındı
                        LocalDate girisTarihi = LocalDate.parse(girisTarihiStr); //Kullanıcının girdiği tarih stringini LocalDate nesnesine dönüştürür.
                        System.out.println("Cikis tarihini giriniz (YYYY-MM-DD):");
                        String cikisTarihiStr = scanner.next();
                        LocalDate cikisTarihi = LocalDate.parse(cikisTarihiStr); //Kullanıcının girdiği tarih stringini LocalDate nesnesine dönüştürür.
                        scanner.nextLine(); 

                        
                        //for ile yapsaydık tum rezervasyonlari tek tek dolasması gerekirdi, agac ile yarısını atlaya atlaya gittik
                        //O(logn)
                        IntervalDugumu cakisan = intervalAgaci.cakismaKontrol(girisTarihi, cikisTarihi);
                        Boolean rezervasyonUygun = (cakisan == null); // null ise cakisma yok uygun

                        double toplamUcret = rezervasyonOda.getOdaFiyat() * (cikisTarihi.toEpochDay() - girisTarihi.toEpochDay()); //Oda fiyatını, kalınan gün sayısıyla çarparak toplam ücreti hesaplar. toEpochDay() metodu, bir tarihi 1970-01-01'den itibaren geçen gün sayısına dönüştürür. İki tarih arasındaki farkı alarak kalınan gün sayısını buluruz.
                        
                        
                        if(!rezervasyonUygun){
                            //cakısma var => bekleme listesine ekle
                            //once o oda numarası icin liste var mi kontrol et
                            BeklemeListe liste = beklemeListeleri.get(rezervasyonOdaNo);
                            if(liste == null){
                                liste = new BeklemeListe();
                                beklemeListeleri.put(rezervasyonOdaNo, liste);//dolaba koyduk
                            }
                            liste.ekle(rezervasyonNo, rezervasyonMusteri, rezervasyonOdaNo, girisTarihi, cikisTarihi); 
                            System.out.println("Bekleme listesine alindi.");

                        } else {
                            //tarih çakışması yok, rezervasyonu yapalım
                            Rezervasyon rezervasyon = new Rezervasyon(rezervasyonNo, rezervasyonMusteri, rezervasyonOda, girisTarihi, cikisTarihi, toplamUcret, "Aktif");
                            rezervasyonlar.put(rezervasyonNo, rezervasyon); //rezervasyonlar HashMap'ine rezervasyonNo'yu anahtar olarak, rezervasyon nesnesini de değer olarak ekledik. Artık bu rezervasyon bilgilerine rezervasyonlar.get(rezervasyonNo) diyerek kolayca ulaşabiliriz. O kutuyu HashMap dolabına koyduk.
                            intervalAgaci.ekle(rezervasyonOdaNo, girisTarihi, cikisTarihi);
                            rezervasyonOda.setOdaMusait(false); // Odayı artık dolu olarak işaretleriz.
                            System.out.println("Rezervasyon yapildi: " + rezervasyon);
                        }
                        //Burda da aslında musteri de yaptığımız şeyle aynı tüm bilgileri alıp  kutuya koyuyoruz. Oda bilgilerini de aynı şekilde alıp kutuya koyuyoruz. Sonra bu iki kutuyu (musteri ve oda) rezervasyon kutusuna koyuyoruz. Böylece rezervasyon nesnesi içinde hem müşteri bilgileri hem de oda bilgileri saklanmış oluyor. Rezervasyonun içine müşteri ve oda nesnelerini gömdük, böylece rezervasyonun içinde o müşterinin adı, soyadı, TC'si ve o odanın tipi, fiyatı gibi bilgilere kolayca erişebiliriz.
                        break;


                    case 4: //Müşterileri listeleme işlemi
                        System.out.println("Musteriler:");
                        if(musteriler.isEmpty()){
                            System.out.println("Sistemde henuz kayitli musteri bulunmamaktadir.");
                        } else {
                            for(Musteri m: musteriler.values()){ //values() metodu, HashMap içindeki tüm değerleri (yani Musteri nesnelerini) bir koleksiyon olarak döndürür. : anlamı da içindeki her bir eleman için döngüyü çalıştır demektir. Yani musteriler.values() bize tüm müşteri nesnelerini verecek ve biz de her bir müşteri nesnesi için döngüyü çalıştıracağız. ve her bir müşteri nesnesini m değişkenine atayarak o müşteriyi ekrana yazdıracağız.
                                System.out.println(m);
                            }
                        }
                        break;
                    
                    case 5: //Odaları listeleme işlemi
                        System.out.println("Odalar:");
                        if(odalar.isEmpty()){
                            System.out.println("Sistemde henuz kayitli oda bulunmamaktadir.");
                        } else {
                            for(Oda o: odalar.values()){ 
                                System.out.println(o);
                            }
                        }
                        break;
                    
                    case 6: //Rezervasyonları listeleme işlemi
                        System.out.println("Rezervasyonlar:");
                        if(rezervasyonlar.isEmpty()){
                            System.out.println("Sistemde henuz kayitli rezervasyon bulunmamaktadir.");
                        } else {
                            for(Rezervasyon r: rezervasyonlar.values()){ 
                                System.out.println(r);
                            }
                        }
                        break;
                    
                    /*case 7:
                        System.out.println("Iptal edilicek rezervasyon numarasini giriniz:");
                        int iptalRezervasyonNo = scanner.nextInt();
                        scanner.nextLine(); // Temizlik için
                        //Rezervasyon numarasını alıp o rezervasyonu bulalım.
                        Rezervasyon iptalRezervasyon = rezervasyonlar.get(iptalRezervasyonNo);// HashMap'e gidiyor verdiğimiz numaraya ait rezervasyonu buluyor ve iptalRezervasyon değişkenine atıyor. Eğer böyle bir rezervasyon yoksa iptalRezervasyon null olur.
                        if(iptalRezervasyon == null){
                            System.out.println("Hata: Sistemde" + iptalRezervasyonNo + " numarali bir rezervasyon bulunmamaktadir!");
                        } else {
                            //Rezervasyon bulundu, şimdi odayı çekip müsait yapıcaz.
                            Oda iptalOda = iptalRezervasyon.getOda(); //iptalRezervasyon değişkeni içindeki oda bilgilerini çekiyoruz. Rezervasyon sınıfında public Oda getOda() metodu vardı, bu metot bize rezervasyonun içindeki oda nesnesini verecek. O nesneyi iptalOda değişkenine atıyoruz.
                            rezervasyonlar.remove(iptalRezervasyonNo); //rezervasyonlar HashMap'inden iptalRezervasyonNo anahtarına karşılık gelen rezervasyonu siler. Artık o rezervasyon bilgilerine rezervasyonlar.get(iptalRezervasyonNo) diyerek ulaşmaya çalıştığında null dönecektir çünkü o rezervasyon artık sistemde yok.
                            iptalOda.setOdaMusait(true); //Odayı müsait yapıyoruz.
                            System.out.println("Rezervasyon iptal edildi: " + iptalRezervasyon);
                        }
                        //bekleme listesinde bu oda için bekleyen biri var mı diye bakalım
                        BeklemeListe beklemeListesi = beklemelisteleri.get(iptalOda.getOdaNo()); //beklemelisteleri HashMap'inden iptal edilen odanın numarasına karşılık gelen bekleme listesini çekiyoruz. Bekleme listesi sınıfından bir nesne dönecek ve onu beklemeListesi değişkenine atacağız.
                        if(beklemeListesi != null && !beklemeListesi.bosMu()){ //Eğer bekleme listesi null değilse ve boş değilse (yani o oda için bekleyen biri varsa)
                            BeklemeDugumu beklemeDugumu = beklemeListesi.cikar(); //Bekleme listesinden sıradaki rezervasyonu çıkarıyoruz. Bu metot, sıradaki rezervasyonun bilgilerini içeren bir BeklemeDugumu nesnesi döndürecek ve onu beklemeDugumu değişkenine atacağız.
                            if(beklemeDugumu != null){
                                //Çıkarılan rezervasyonu aktif hale getirelim
                                Rezervasyon yeniRezervasyon = new Rezervasyon(beklemeDugumu.getRezervasyonNo(), beklemeDugumu.getMusteri(), iptalOda, beklemeDugumu.getGirisTarihi(), beklemeDugumu.getCikisTarihi(), iptalOda.getOdaFiyat() * (beklemeDugumu.getCikisTarihi().toEpochDay() - beklemeDugumu.getGirisTarihi().toEpochDay()), "Aktif");
                                rezervasyonlar.put(yeniRezervasyon.getRezervasyonNo(), yeniRezervasyon); //Yeni rezervasyonu rezervasyonlar HashMap'ine ekliyoruz.
                                iptalOda.setOdaMusait(false); //Odayı tekrar dolu yapıyoruz çünkü yeni bir rezervasyon aktif oldu.
                                System.out.println("Bekleme listesinden bir rezervasyon aktif edildi: " + yeniRezervasyon);
                            }
                        }
                        break;*/
                        case 7:
                            System.out.println("Iptal edilicek rezervasyon numarasini giriniz:");
                            int iptalRezervasyonNo = scanner.nextInt();
                            scanner.nextLine();

                            //Rezervasyon numarasını alıp o rezervasyonu bulalım.
                            Rezervasyon iptalRezervasyon = rezervasyonlar.get(iptalRezervasyonNo);// HashMap'e gidiyor verdiğimiz numaraya ait rezervasyonu buluyor ve iptalRezervasyon değişkenine atıyor. Eğer böyle bir rezervasyon yoksa iptalRezervasyon null olur.
                            if(iptalRezervasyon == null){
                                System.out.println("Hata: Sistemde " + iptalRezervasyonNo + " numarali rezervasyon bulunamadi!");
                            } else {
                                //Rezervasyon bulundu, şimdi odayı çekip müsait yapıcaz.
                                Oda iptalOda = iptalRezervasyon.getOda(); ///İptal edilen rezervasyonun hangi odaya ait olduğunu getOda() ile çektik ve iptalOda değişkenine atadık.
                                rezervasyonlar.remove(iptalRezervasyonNo); //rezervasyonlar HashMap'inden iptalRezervasyonNo anahtarına karşılık gelen rezervasyonu siler. Artık o rezervasyon bilgilerine rezervasyonlar.get(iptalRezervasyonNo) diyerek ulaşmaya çalıştığında null dönecektir çünkü o rezervasyon artık sistemde yok.
                                tamamlananRezervasyonlar.ekle(iptalRezervasyon); //Rezervasyon iptal edilince BST ye ekledim
                                intervalAgaci.sil(iptalOda.getOdaNo(), iptalRezervasyon.getGirisTarihi(), iptalRezervasyon.getCikisTarihi());
                                System.out.println("Rezervasyon iptal edildi: " + iptalRezervasyon);

                                // Bekleme listesinde bu oda için biri var mı?
                                BeklemeListe liste = beklemeListeleri.get(iptalOda.getOdaNo());
                                if(liste != null && !liste.bosMu()){//Bu oda için oluşturulmuş bir liste var mı VE bu listenin içi dolu mu (bekleyen biri var mı)
                                    // Sıradaki kişiyi listeden çıkar
                                    BeklemeDugumu siradaki = liste.cikar();
                                    System.out.println("Bekleme listesinden siradaki musteri alindi: "+ siradaki.musteri.getMusteriAdSoyad());
                                    double yeniUcret = iptalOda.getOdaFiyat() *(siradaki.cikisTarihi.toEpochDay() - siradaki.girisTarihi.toEpochDay());

                                    // Sıradaki kişi için otomatik rezervasyon oluştur
                                    Rezervasyon yeniRezervasyon = new Rezervasyon(
                                        siradaki.rezervasyonNo, siradaki.musteri, iptalOda,
                                        siradaki.girisTarihi, siradaki.cikisTarihi, yeniUcret, "Aktif"
                                    );
                                    rezervasyonlar.put(siradaki.rezervasyonNo, yeniRezervasyon);
                                    iptalOda.setOdaMusait(false); // Oda yine doldu
                                    System.out.println("Otomatik rezervasyon olusturuldu: " + yeniRezervasyon);
                                } else {
                                    // Bekleyen kimse yok, odayı müsait yap
                                    iptalOda.setOdaMusait(true);
                                    System.out.println("Bekleme listesi bos. Oda musait duruma getirildi.");
                                }
                            }
                            break;
                            
                        case 8:
                                System.out.println("8. Tammalanan Rezervasyonlari Listele");
                                tamamlananRezervasyonlar.kronolojikListele();
                                break;
                            
                        case 9:
                                System.out.println("9. Cikis");
                                break;
                        default:
                            System.out.println("Gecersiz secim! Lutfen 1-9 arasi bir sayi giriniz.");
                            break;    

                }
            } while (secim != 9);
            System.out.println("Veriler dosyalara kaydediliyor...");
            odaVerileriKaydet();
            musteriVerileriKaydet();
            rezervasyonVerileriKaydet();

            System.out.println("Programdan cikiliyor...");
            scanner.close();
 
        }

        public static void odaVerileriKaydet(){
            try(PrintWriter writer = new PrintWriter(new FileWriter("odalar.csv"))){
                for(Oda o: odalar.values()){
                    writer.println(o.getOdaNo() + ","+
                    o.getTip() + ","+
                    o.getKapasite() + ","+
                    o.getOdaFiyat() + ","+
                    o.getOdaMusait() + ","+ 
                    o.getAciklama());

                }
                System.out.println("Veriler kaydedildi.");
            } catch (IOException e){
                System.out.println("Veriler kaydedilirken hata olustu: " + e.getMessage());
            }
        }
        public static void odaVerileriniYukle(){
            File file = new File("odalar.csv");
            if(!file.exists()) return; //Dosya yoksa yükleme yapmaya gerek yok, direkt çık

            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String satir;
                while((satir = reader.readLine()) != null){
                    String[] parca = satir.split(",");
                    // CSV formatından verileri okuyup tekrar nesneye dönüştürme mantığı burda
                    int odaNo = Integer.parseInt(parca[0]);
                    String tip = parca[1];
                    int kapasite = Integer.parseInt(parca[2]);
                    double odaFiyat = Double.parseDouble(parca[3]);
                    boolean odaMusait = Boolean.parseBoolean(parca[4]);
                    String aciklama = parca[5];

                    Oda oda = new Oda(odaNo, tip, kapasite, odaFiyat, odaMusait, aciklama); //constructoramıza göre nesneyi ürettik
                    oda.setOdaMusait(odaMusait);
                    odalar.put(odaNo, oda); //HashMap'e ekledik
                }
            } catch (IOException e){
                System.out.println("Veriler yüklenirken hata olustu: " + e.getMessage());
            }
        }

        public static void musteriVerileriKaydet(){
            try(PrintWriter writer = new PrintWriter(new FileWriter("musteriler.csv"))){
                for(Musteri m: musteriler.values()){
                    writer.println(m.getMusteriId() + ","+
                    m.getMusteriAdSoyad() + ","+
                    m.getMusteriTelefon() + ","+
                    m.getMusteriEmail() + ","+
                    m.getMusteriTcKimlikNo() + ","+
                    m.getMusteriAdres());

                }
                System.out.println("Veriler kaydedildi.");
            } catch (IOException e){
                System.out.println("Veriler kaydedilirken hata olustu: " + e.getMessage());
            }
        }
        public static void musteriVerileriYukle(){
            File file = new File("musteriler.csv");
            if(!file.exists()) return; //Dosya yoksa yükleme yapmaya gerek yok, direkt çık

            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                String satir;
                while((satir = reader.readLine()) != null){
                    String[] parca = satir.split(",");
                    // CSV formatından verileri okuyup tekrar nesneye dönüştürme mantığı burda
                    int musteriId = Integer.parseInt(parca[0]);
                    String adSoyad = parca[1];
                    String telefon = parca[2];
                    String email = parca[3];
                    String tcKimlikNo = parca[4];
                    String musteriAdres = parca[5];

                    Musteri musteri = new Musteri(musteriId, adSoyad, telefon, email, tcKimlikNo, musteriAdres); //constructoramıza göre nesneyi ürettik
                    musteriler.put(musteriId, musteri); //HashMap'e ekledik
                }
            } catch (IOException e){
                System.out.println("Veriler yüklenirken hata olustu: " + e.getMessage());
            }
        }



        public static void rezervasyonVerileriKaydet(){
                try(PrintWriter writer = new PrintWriter(new FileWriter("rezervasyonlar.csv"))){ //FileWriter → bilgisayardaki dosyayı fiziksel olarak açar ve içine yazmak,  PrintWriter → onun üstüne oturur, println gibi kullanışlı metodlar ekler
                    for(Rezervasyon r: rezervasyonlar.values()){
                        writer.println(r.getRezervasyonNo() +","+
                        r.getMusteri().getMusteriId() +","+
                        r.getOda().getOdaNo() +","+
                        r.getGirisTarihi() +","+
                        r.getCikisTarihi() +","+
                        r.getToplamUcret() +","+
                        r.getDurum());
                    }
                    System.out.println("Veriler kaydedildi.");
                } catch (IOException e){
                    System.out.println("Veriler kaydedilirken hata olustu: " + e.getMessage());
                }
                //burda hepsini parçaladık satır satır yazdık. Rezervasyon nesnesinin içindeki müşteri nesnesinin TC kimlik numarasını, oda nesnesinin oda numarasını, giriş ve çıkış tarihlerini, toplam ücreti ve durumu tek tek çekip virgülle ayırarak yazdık. Böylece her rezervasyon bilgisi tek bir satırda, birbirinden virgülle ayrılmış şekilde kaydedilmiş oldu. Bu format CSV (Comma-Separated Values) olarak adlandırılır ve verileri kolayca okunabilir ve işlenebilir hale getirir.
            }

            public static void rezervasyonVerileriYukle(){
                File file = new File("rezervasyonlar.csv");//File = dosyayı temsil eder ama açmaz sadece varlığına yokluğuna nerede olduğuna bakar
                if(!file.exists()) return; //Dosya yoksa yükleme yapmaya gerek yok, direkt çık
                
                try(BufferedReader reader = new BufferedReader(new FileReader(file))){ // FileReader → dosyayı karakter karakter okur, BufferedReader → onun üstüne oturur, satır satır okuma gibi kullanışlı metodlar ekler readline() gibi
                    String satir;
                    while((satir = reader.readLine()) != null){ //readLine() metodu dosyadan bir satır okur ve o satırı string olarak döndürür. Eğer dosyanın sonuna gelinmişse null döndürür. Yani bu döngü, dosyada okunacak satır kalmayana kadar devam eder.
                        String[] parca = satir.split(","); //satırı parçalıyoruz.
                        //CSV formatından verileri okuyup tekrar nesneye dönüştürme mantığı burda 
                        int rezervasyonNo = Integer.parseInt(parca[0]);
                        int musteriId = Integer.parseInt(parca[1]);
                        int odaNo = Integer.parseInt(parca[2]);
                        LocalDate girisTarihi = LocalDate.parse(parca[3]);
                        LocalDate cikisTarihi = LocalDate.parse(parca[4]);
                        double toplamUcret = Double.parseDouble(parca[5]);
                        String durum = parca[6];
                        //CSV dosyasından okunan veriler string olarak gelir, biz onları uygun veri tiplerine dönüştürdük.

                        // Ben müşteri ve odayı yeniden üretmiyorum sadece HashMapten cekicem
                        Musteri musteri = musteriler.get(musteriId); //musteriler HashMap'inden musteriId anahtarına karşılık gelen Musteri nesnesini alır ve musteri değişkenine atar.komutu devreye giriyor. Bu komut bilgisayara şunu der: Git o bizim yukarıda oluşturduğumuz musteriler defterini aç. İçinden 5 numaralı (musteriId) satırı bul ve oradaki Müşteri nesnesini (adıyla sanıyla) alıp bana getir.
                        Oda oda = odalar.get(odaNo);

                        //Sadece müşteri ve oda gerçekten sistemde (HashMap'te) varsa rezervasyonu yükle
                        if(musteri != null && oda != null){
                            Rezervasyon rezervasyon = new Rezervasyon(rezervasyonNo, musteri, oda, girisTarihi, cikisTarihi, toplamUcret, durum); //CSV’den gelen verilerle nesne yeniden yaratılıyor
                            rezervasyonlar.put(rezervasyonNo, rezervasyon);
                        }else{
                            System.out.println("Uyari: " + rezervasyonNo + " numarali rezervasyonun musteri veya oda bilgisi eksik oldugu icin yuklenemedi!");
                        }
                        //burda ise satırları tek tek okuduk ve her satırı parçalara ayırarak rezervasyon bilgilerini elde ettik. Sonra o bilgileri kullanarak rezervasyon nesneleri oluşturduk ve onları rezervasyonlar HashMap'ine ekledik. Ancak burada önemli bir kontrol yaptık: Rezervasyonu yüklemeden önce, o rezervasyona ait müşteri ve oda bilgilerinin gerçekten sistemde mevcut olup olmadığını kontrol ettik. Eğer müşteri veya oda bilgisi eksikse, o rezervasyonu yüklemedik ve kullanıcıya bir uyarı mesajı gösterdik. Böylece hatalı veya eksik verilerin sisteme girmesini engellemiş olduk.
                    }
                } catch(Exception e){
                    System.out.println("Veriler yuklenirken hata olustu: " + e.getMessage());
                }
                
    }
}

/* HashMap verileri depolamak için kullanılır . Bir nevi dolap gibi
odalar dolabı:

101  Oda{no=101, tip="Deluxe", ...}   
102  Oda{no=102, tip="Standart", ...} 
103  Oda{no=103, tip="Suite", ...}    

101, 102, 103 → dolabın gözleri (sen bunları belirledin)
Oda nesnesi → o gözün içindeki kutu

Integer = Anahtar (Etiket), Oda = Saklanan Nesne
HashMap<Integer, Oda> odalar = new HashMap<>(); 
odalar →  [ 101 → Oda{no=101, tip="Deluxe", fiyat=500} ]
          [ 102 → Oda{no=102, tip="Standart", fiyat=300} ]
          [ 103 → Oda{no=103, tip="Suite", fiyat=1000} ]*/
           
   

//satır 89 Oda rezervasyonOda = odalar.get(rezervasyonOdaNo);  bu kısım nesne 