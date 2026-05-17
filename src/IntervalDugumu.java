//burda bir odanın belirli tarihler arasında dolu olup olmadığını kontrol etmek için 
/*Interval Tree, her düğümde bir tarih aralığını (Giriş-Çıkış) ve o düğümün alt ağacındaki en büyük bitiş tarihini (max) saklar. Bu sayede bir çakışma olup olmadığını O(log n) sürede bulabiliriz. */
import java.time.LocalDate;

public class IntervalDugumu {
    LocalDate baslangic, bitis, max;
    IntervalDugumu sol, sag;
    int odaNo; //hangi odaya ait oldugunu bilmek icin

    public IntervalDugumu(int odaNo, LocalDate baslangic, LocalDate bitis){
        this.odaNo = odaNo;
        this.baslangic = baslangic;
        this.bitis = bitis;
        this.max = bitis; //baslangicta max kendisi cunku sagında solunda baska bir rezervasyon yok

//max degiskeni = o dugumun(rezervasyonun) ve onun altındaki tüm dalların sahip oldugu en gec cıkıs tarihi
    }
}
/*Normal bir listede 10.000 rezervasyon varsa, yeni musterinin tarihi cakısıyor mu diye 10.000 kayda tek tek bakman gerekir (Zaman karmaşıklığı: O(n)
Ancak Interval Tree'de max sayesinde ağacın koca bir dalını saniyede cope atabiliriz (Zaman karmasıklıgı: O(log n). */