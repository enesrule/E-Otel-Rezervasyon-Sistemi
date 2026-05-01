public class Oda {
    public int odaNo;
    public String tip;
    public int kapasite;
    public double odaFiyat;
    public boolean odaMusait;
    public String aciklama;

    public Oda(int odaNo, String tip, int kapasite, double odaFiyat, boolean odaMusait, String aciklama) {
        this.odaNo = odaNo;
        this.tip = tip;
        this.kapasite = kapasite;
        this.odaFiyat = odaFiyat;
        this.odaMusait = odaMusait;
        this.aciklama = aciklama;
    }

    public int getOdaNo() {
        return odaNo;
    }
    public void setOdaNo(int odaNo) {
        this.odaNo = odaNo;
    }

    public String getTip(){
        return tip;
    }
    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getKapasite(){
        return kapasite;
    }
    public void setKapasite(int kapasite){
        this.kapasite = kapasite;
    }

    public double getOdaFiyat(){
        return odaFiyat;
    }
    public void setOdaFiyat(double odaFiyat){
        this.odaFiyat = odaFiyat;
    }

    public boolean getOdaMusait(){
        return odaMusait;
    }
    public void setOdaMusait(boolean odaMusait){
        this.odaMusait = odaMusait;
    }

    public String getAciklama(){
        return aciklama;
    }
    public void setAciklama(String aciklama){
        this.aciklama = aciklama;
    }

    @Override
    public String toString() {
        return "Oda{" +
                "odaNo=" + odaNo +
                ", tip='" + tip + '\'' +
                ", kapasite=" + kapasite +
                ", odaFiyat=" + odaFiyat +
                ", odaMusait=" + odaMusait +
                ", aciklama='" + aciklama + '\'' +
                '}';
    }
}
