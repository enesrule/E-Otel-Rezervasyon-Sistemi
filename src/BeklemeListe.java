import java.time.LocalDate;

public class BeklemeListe {
    //O(1) alan sadece head pointer tutuluyor

    private BeklemeDugumu head; //listenin başını göstermek için bir pointer
    private int boyut; //listenin boyutunu takip etmek için bir değişken

    public BeklemeListe(){
        this.head = null; //başlangıçta liste boş olduğu için head null olur
        this.boyut = 0; //başlangıçta boyut sıfırdır
    }

    //Listede yeni eleman eklerken sona kadar gitmememiz gerekiyor buda O(n) e sebep olur
    public void ekle(int rezervasyonNo, Musteri musteri, int odaNo, LocalDate girisTarihi, LocalDate cikisTarihi){
        BeklemeDugumu yeniDugum = new BeklemeDugumu(rezervasyonNo, musteri, odaNo, girisTarihi, cikisTarihi); //yeni bir düğüm oluşturuyoruz
        if(head == null){
            head = yeniDugum; //eğer liste boşsa yeni düğüm head olur
        } else {
            BeklemeDugumu temp = head; //geçici bir değişken oluşturup head'in değerini ona atıp listeyi dolaşcaz
            while(temp.sonraki != null){ //listenin sonuna kadar gidiyoruz
                temp = temp.sonraki; //temp'i sonraki düğüme geçiriyoruz
            }
            temp.sonraki = yeniDugum; //son düğümün sonraki göstergesini yeni düğüme atıyoruz
        }
        boyut++; //listeye yeni bir eleman eklediğimiz için boyutu artırıyoruz
        System.out.println(musteri.getMusteriAdSoyad() + " bekleme listesine eklendi. Sira: " + boyut);
    }

    //Listeden eleman çıkarırken de baştan çıkarmamız gerekiyor buda O(1) e sebep olur
    public BeklemeDugumu cikar(){
        if(head==null){
            System.out.println("Bekleme listesi boş, cikarilcak eleman yok.");
            return null; //liste boşsa null döndürüyoruz
        } else{
            BeklemeDugumu cikarilan = head; //cikarilcak elemanı geçici bir değişkene atıyoruz ve onda saklıcaz
            head = head.sonraki; //head'i bir sonraki düğüme geçiriyoruz, böylece ilk düğüm listeden çıkarılmış olur
            cikarilan.sonraki = null; //çıkarılan düğümün sonraki göstergesini null yapıyoruz cünkü bagı kopardık
            boyut--; //liste eleman çıkardığımız için boyutu azaltıyoruz
            System.out.println(cikarilan.musteri.getMusteriAdSoyad() + " bekleme listesinden cikarildi. Kalan sira: " + boyut);
            return cikarilan; //çıkarılan düğümü döndürüyoruz
        }
    }

    //Liste boş mu kontrolü yaparken O(1) e sebep olur çünkü sadece head'in null olup olmadığına bakıyoruz
    public boolean bosMu(){
        return head == null; //head null ise liste boştur, değilse doludur
    }

    public int getBoyut(){
        return boyut; //listenin boyutunu döndürüyoruz
    }

    //Listeyi yazdırırken O(n) e sebep olur çünkü tüm düğümleri dolaşıp yazdırmamız gerekiyor
    public void yazdir(){
        if(head == null){
            System.out.println("Bekleme listesi bos.");
        }else {
            BeklemeDugumu temp = head; //geçici bir değişken oluşturup head'in değerini ona atıp listeyi dolaşcaz
            int sira = 1; //sira numarası için bir değişken
            while(temp != null){ //listenin sonuna kadar gidiyoruz
                System.out.println(sira + ". " + temp); //temp'in gösterdiği düğümü yazdırıyoruz
                temp = temp.sonraki; //temp'i sonraki düğüme geçiriyoruz
                sira++; //sira numarasını artırıyoruz
            }
        }
    }

}

//BeklemeDugumu veri tipinde değişken oluşturma sebebim Listeyi dolaşmak için node’ları gezmen lazım.Mesela int temp; int int.sonraki diye bir şey yok.
// BeklemeDugumunde node’un içinde sonraki var,int ile ürettiğin zaman gezemezsin ama beklemedugumu ile ürettiğin zaman gezebilirsin
/*class BeklemeDugumu {
    Musteri musteri;
    BeklemeDugumu sonraki;
} 
yani bir node: hem veri tutar hem de sonraki node’a gider.Gezdiğin şey node olduğu için BeklemeDugumu kullanıyorsun*/
//BeklemeDugumu ile değişken tanımlıyorum çünkü listede dolaştığım şey node’lar ve node’ların içinde sonraki olduğu için bir düğümden diğerine geçebiliyorum.