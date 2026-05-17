public class RezervasyonBST {
    private BSTDugumu kok; //Agacın tepesi

    public RezervasyonBST(){
        this.kok = null; //baslangıcta agaç bos
    }

    // Dışarıdan çağrılan ekle metodu → O(log n) ortalama, O(n) en kötü
    public void ekle(Rezervasyon rezervasyon){// yeni gelen rezervasyonu agaca eklemek icin
        kok = ekleYardimci(kok,rezervasyon); //kok'u sadece agac bosken ilk eklemede güncelliyoruz
        
    }

    private BSTDugumu ekleYardimci(BSTDugumu dugum, Rezervasyon rezervasyon){ //BSTDugumu dugum → "Şu an ağacın hangi düğümündeyim?" sorusunun cevabı. Metod her çağrıldığında farklı bir düğümde olacak, o yüzden parametre olarak alıyor. Rezervasyon rezervasyon → "Nereye ekleyeceğim?" bilmek için değil, "Ne ekleyeceğim?" bilmek için. Eklenecek veri bu.
        if(dugum == null){ //eger dugum bossa yeni bir dugum olusturup onu dondurcez
            System.out.println("BST'ye yeni rezervasyon eklendi: " + rezervasyon.getRezervasyonNo() + " - Giris Tarihi: " + rezervasyon.getGirisTarihi());
            return new BSTDugumu(rezervasyon);//BSTdugumu vonstructrımız 1 parametre alabiliyor
        }

        //yeni rezervasyonun giris tarihi, mevcut dugumun giris tarihinden kucukse sol tarafa ekle
        if(rezervasyon.getGirisTarihi().isBefore(dugum.girisTarihi)){
            dugum.sol = ekleYardimci(dugum.sol, rezervasyon); //sol alt agaca git ve sonucu bana geri ver bende sola ekliyim mantıgı mevcut 
            

        } else if(rezervasyon.getGirisTarihi().isAfter(dugum.girisTarihi)){ //yeni rezervasyonun giris tarihi, mevcut dugumun giris tarihinden buyuk veya esit ise sag tarafa ekle
            dugum.sag = ekleYardimci(dugum.sag, rezervasyon); //sag alt agaca ekle
        }
        //aynı tarihse saga ekle
        else {
            dugum.sag = ekleYardimci(dugum.sag, rezervasyon);
        }
        return dugum;//agacı yukarı dogru geri kurar

        /*ekleYardimci(kok, rezervasyon)
            └→ ekleYardimci(kok.sag, rezervasyon)
                └→ ekleYardimci(kok.sag.sol, rezervasyon)
                    └→ dugum == null → yeni düğüm döndür
                ← kok.sag.sol = yeni düğüm
            ← kok.sag döndür (değişmedi ama bağlantı güncellendi)
        ← kok döndür 
        */
        
        /*mesela 3 eklerken ekle(3) 
        ekleYardimci(10)
        → sola git: 10.sol = ekleYardimci(5)
        tekrar sola: 5.sol = ekleYardimci(null)
        null → yeni node: return 3 
        Şimdi geri sarıyoruz: 
        5.sol = 3
        return 5
        10.sol = 5
        return 10
        kok = 10
        dugum.sol = ... 3 olusturuldu ama 5 e baglanması icin bu lazım olusur ama kaybolurdu.*/
    }

    // Kronolojik sıralı listele (In-Order: sol → kök → sağ) → O(n)
    public void kronolojikListele(){
        if(kok == null){
            System.out.println("Tamamlanan rezervasyon bulunmamaktadir.");
            return;
        }
        System.out.println("=== Tamamlanan Rezervasyonlar ===");
        inOrder(kok);
    }

    // Swing tablosuna in-order sıralı ekle → O(n)
    public void tabloIcinListele(javax.swing.table.DefaultTableModel model) {
        tabloInOrder(kok, model);
    }

    private void tabloInOrder(BSTDugumu dugum, javax.swing.table.DefaultTableModel model) {
        if (dugum == null) return;
        tabloInOrder(dugum.sol, model);
        Rezervasyon r = dugum.rezervasyon;
        model.addRow(new Object[]{
            r.getRezervasyonNo(),
            r.getMusteri().getMusteriAdSoyad(),
            r.getOda().getOdaNo(),
            r.getGirisTarihi(),
            r.getCikisTarihi(),
            r.getToplamUcret() + " ₺",
            r.getDurum()
        });
        tabloInOrder(dugum.sag, model);
    }
    // BST'yi in-order (tarihe göre sıralı) dolaşıp doğrudan tabloya satır ekliyor

    private void inOrder(BSTDugumu dugum){
        if(dugum ==null)  return; //nulla geldiginde bir ust cagrıya döner. Fonksiyondan cık ve geldigin yere geri dön
        inOrder(dugum.sol);
        System.out.println(dugum.rezervasyon);
        inOrder(dugum.sag);
        /*inOrder(10)
            ↓
        inOrder(5)
        ↓
        inOrder(3)
        ↓
        inOrder(null) nulla geldik return; bu returnda bir üst cagrıya döncek inOrder(3)*/
        
    }
     // BST boş mu? → O(1)
     public boolean bosMu(){
        return kok == null;
     }
}
//Tamamlanan Rezervasyonlar burda Kronolojik olarak

//buraya tekrar calıs!!!!
