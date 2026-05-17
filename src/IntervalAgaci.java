//Burası rezervasyonları eklicek ve cakısma kontrolu yapcak olan kısım
/*Zaman Karmaşıklığı: O(n*h), burada n aralık sayısı ve h aralık ağacının yüksekliğidir. Ortalama durumlarda, h = log(n) ve zaman karmaşıklığı O(n * log(n)) olacaktır.
 En kötü durumda, ağaç çarpık olacak ve yükseklik n olacak, dolayısıyla zaman karmaşıklığı O(n^2) olacaktır. Aralık ağacı AVL ağacı gibi kendi kendini dengeleyen bir ağaç haline getirilirse, zaman karmaşıklığı O(n * log n)'ye düşer. */
import java.time.LocalDate;

public class IntervalAgaci {
    private IntervalDugumu kok;

    //Yeni bir rezervasyon aralıgı ekleyelim
    public void ekle(int odaNo, LocalDate baslangic, LocalDate bitis){
        kok = ekle(kok, odaNo, baslangic, bitis);
    }

    private IntervalDugumu ekle(IntervalDugumu dugum, int odaNo, LocalDate bas, LocalDate bit){
        if(dugum == null) return new IntervalDugumu(odaNo, bas, bit);

        //BST mantıgı gibi baslangıc tarihine gore ekleyelim
        if(bas.isBefore(dugum.baslangic))
            dugum.sol = ekle(dugum.sol, odaNo, bas, bit);//Yeni müsterinin girisi eskiyse SOLA git
        else
            dugum.sag = ekle(dugum.sag, odaNo, bas, bit);//Yeni müsterinin girisi yeniyse SAGA git

        //max degerini guncellicez
        if(dugum.max.isBefore(bit))
            dugum.max = bit;
        /*bura sayesinde Her dugumun kendi altındaki en gec cıkıs tarihini tutuyor bu saeyde dalda arama yapmaya gerek var mı sorusuna cevabı hızlıca veriyoruz
         [Ocak 10-15, max=25]  ← altında en geç 25'e kadar rezervasyon var
        /                    \
  [Ocak 5-8, max=8]    [Ocak 20-25, max=25]*/
        
        return dugum; //Bu recursive yapının yukarı doğru geri bağlanmasını sağlar.
    }
    
    //Cakısma kontrolu; eger verilen tarihlerde oda doluysa o dugumu doner
    public IntervalDugumu cakismaKontrol(int odaNo, LocalDate bas, LocalDate bit){
        return cakismaVarMi(kok, odaNo, bas, bit); //kökle başlama sebebi aramaya ağacın en üstünden başlanmalı.
    }

    private IntervalDugumu cakismaVarMi(IntervalDugumu dugum, int odaNo, LocalDate bas, LocalDate bit){
        if(dugum == null) return null;

        //1. KURAL: Hem TARİH çakışmalı, hem de ODA NUMARASI eşleşmeli
        boolean tarihCakisiyor = !(bit.isBefore(dugum.baslangic) || bas.isAfter(dugum.bitis));

        if(dugum.odaNo == odaNo && tarihCakisiyor){
            return dugum; // Tam eşleşme bulundu
        }

        //Eger sol cocugun maxı aradıgımız baslangıctan büyükse sola bak
        if (dugum.sol != null && !dugum.sol.max.isBefore(bas)) {
            // Sola in ve bizim odamız için çakışma var mı kontrol et
            IntervalDugumu solSonuc = cakismaVarMi(dugum.sol, odaNo, bas, bit);

            // Eğer solda kendi odamızla ilgili çakışma bulduysak onu döndür
            if (solSonuc != null) {
                return solSonuc;
            }
        }
        //bulamazsak aramayı bitirmeden sağ tarafta olma ihtimalini de değerlednirmemz gerek

        // Değilse sağa bak
        return cakismaVarMi(dugum.sag, odaNo, bas, bit);
    }

    public void sil(int odaNo, LocalDate girisTarihi, LocalDate cikisTarihi){
        kok = sil(kok,odaNo, girisTarihi, cikisTarihi);
    }

    private IntervalDugumu sil(IntervalDugumu dugum, int odaNo, LocalDate bas, LocalDate bit){
        if(dugum == null) return null;
    

        //Bu dugum mu silincek
        if(dugum.odaNo == odaNo && dugum.baslangic.equals(bas) && dugum.bitis.equals(bit)) {//Rezervasyonu iptal edilcek olan dugumun odaNo, bas, bitis degerleri bizim iptal etcegimizle aynıysa
            //sol cocuk yoksa sagı dondur
            if(dugum.sol == null) return dugum.sag;
            //sag cocuk yoksa solu dondur
            if(dugum.sag == null) return dugum.sol;

            //iki cocuk varsa silcegimiz icin sagın en solunu bulalım
            IntervalDugumu successor = dugum.sag;
            while(successor.sol != null) 
                successor = successor.sol;

            //successorın verilerini bu dugume kopyalayalım
            dugum.odaNo = successor.odaNo;
            dugum.baslangic = successor.baslangic;
            dugum.bitis  =successor.bitis;

            //SIMDI successorı sag daldan silelim
            dugum.sag = sil(dugum.sag, successor.odaNo, successor.baslangic, successor.bitis);

        }else if(bas.isBefore(dugum.baslangic)){
            dugum.sol = sil(dugum.sol, odaNo, bas, bit);
        }else{
            dugum.sag = sil(dugum.sag, odaNo, bas, bit);
        }
    
        //max degerini guncellicez
        dugum.max = dugum.bitis;
        if(dugum.sol != null && dugum.sol.max.isAfter(dugum.max)) dugum.max = dugum.sol.max;
        if(dugum.sag != null && dugum.sag.max.isAfter(dugum.max)) dugum.max = dugum.sag.max;
        return dugum;

    }
}
/*
    bas ve bit yeni verilerimiz, dugum (Eski Veri / Mevcut Kayıt) Dolayısıyla dugum.baslangic, o eski kaydın giriş tarihi; dugum.bitis ise o eski kaydın çıkış tarihidir.
    dugum.sol = ekle(dugum.sol, odaNo, bas, bit)
Sağ Tarafın Görevi (Emri Vermek):
Parantez içindeki dugum.sol, şu anki düğümün mevcut sol çocuğudur. Bilgisayara şu emri veriyoruz: "Yeni bir müşteri geldi. Benim mevcut sol dalımdaki adamı çağır (dugum.sol) ve ona de ki: 'Al bu yeni müşteriyi, kendi alt dallarında uygun, boş bir yere yerleştir'."
Sol Tarafın Görevi (Bağı Yeniden Kurmak):
Eşittirin solundaki dugum.sol =  kısmı ise "Sol Kolum" demektir.
Diyelim ki sağ taraftaki ekle metodu işini bitirdi, en aşağılara indi, boş bir yer buldu ve yeni kutuyu (müşteriyi) yarattı. Eğer biz sadece ekle(dugum.sol, ...) deyip bıraksaydık, o yeni yaratılan kutu havada asılı kalırdı, bizim ağacımıza bağlanmazdı!
İşte sol taraftaki atama şunu diyor: "Aşağıda yapılan ekleme işlemi sonucunda güncellenmiş ve yeni kutu eklenmiş olan o koca sol dalı al, tekrar benim sol koluma sıkıca bağla ki ağaç kopmasın."
    
    if(dugum == null) return new IntervalDugumu(...)
ağaçta aşağı doğru iniyor, iniyor... En sonunda bakıyor ki gidecek yer kalmamış, karşısı boşluk (null).
O zaman bilgisayar diyor ki: "Aha! Aradığım boş yeri buldum!" Hemen new IntervalDugumu(...) ile yeni bir kutu (yeni rezervasyon nesnesi) yaratıyor. return ile de yukarı gönderiyor.

    return dugum;
alttan yukarı dogru baglanmamızı saglar fermuar gibi

    kok = ekle(kok, odaNo, baslangic, bitis);

    
    return cakismaVarMi(kok, bas, bit); Buradaki kok Nedir?
kok (Root): Sisteme eklenen İLK rezervasyondur. Ağacın en tepesindeki babasıdır. Her şey bu düğümün altından dallanır.
Biz dışarıdan cakismaKontrol(bas, bit) diye bir metot çağırdığımızda, bilgisayar ağacın neresinden aramaya başlayacağını bilemez. Bilgisayara "Aramaya en tepeden başla!" dememiz gerekir.
İşte cakismaVarMi(kok, bas, bit) şunu der:
"Aramaya ağacın en tepesinden (kok) başla. Yeni müşterinin bas ve bit tarihlerini alıp aşağı doğru inerek kontrol et."

cakismaVarMi(IntervalDugumu dugum, LocalDate bas, LocalDate bit)
Aranan Seyin Ne Oldugu: Yeni musterinin ne zaman girip ne zaman cıkacagı (bas ve bit), Su An Nerede Oldugunu da dugum belirler
 */
