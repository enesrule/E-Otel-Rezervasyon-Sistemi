import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class MainSwing {

    // --- VERİ YAPILARI ---
    static HashMap<Integer, Oda> odalar = new HashMap<>();
    static HashMap<Integer, Musteri> musteriler = new HashMap<>();
    static HashMap<Integer, Rezervasyon> rezervasyonlar = new HashMap<>();
    static HashMap<Integer, BeklemeListe> beklemeListeleri = new HashMap<>();
    static RezervasyonBST tamamlananRezervasyonlar = new RezervasyonBST();
    static IntervalAgaci intervalAgaci = new IntervalAgaci();

    // --- GÖRSELLİK VE TEMA DEĞİŞKENLERİ ---
    private static JFrame frame;
    private static JPanel anaPanel;
    private static boolean isDarkMode = false; // Temayı kontrol eden ana şalter

    // Dinamik Renk Metotları
    private static Color getAnaBg() { return isDarkMode ? new Color(40, 42, 54) : new Color(245, 240, 240); }
    private static Color getMenuBg() { return isDarkMode ? new Color(25, 26, 33) : new Color(20, 20, 20); }
    private static Color getBaslikColor() { return isDarkMode ? new Color(255, 121, 198) : new Color(128, 0, 32); }
    private static Color getTextColor() { return isDarkMode ? new Color(248, 248, 242) : Color.BLACK; }
    private static Color getTextBg() { return isDarkMode ? new Color(68, 71, 90) : Color.WHITE; }
    private static Color getBtnBg() { return isDarkMode ? new Color(98, 114, 164) : new Color(128, 0, 32); }

    public static void main(String[] args) {
        odaVerileriniYukle();
        musteriVerileriYukle();
        rezervasyonVerileriYukle();
        
        SwingUtilities.invokeLater(() -> {
            if (girisEkraniGoster()) {
                arayuzuOlustur();
            } else {
                System.exit(0);
            }
        });
    }

    // --- GİRİŞ EKRANI SİSTEMİ ---
    private static boolean girisEkraniGoster() {
        JDialog loginDialog = new JDialog((Frame)null, "E-Otel Güvenlik Duvarı", true);
        loginDialog.setSize(400, 250);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.setLayout(new BorderLayout());

        JPanel pnlOrta = new JPanel(new GridLayout(2, 2, 10, 20));
        pnlOrta.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        pnlOrta.setBackground(new Color(20, 20, 20)); 

        JLabel lblKul = new JLabel("Kullanıcı Adı:");
        lblKul.setForeground(Color.WHITE);
        lblKul.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField txtKullanici = new JTextField();
        txtKullanici.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblSifre = new JLabel("Şifre:");
        lblSifre.setForeground(Color.WHITE);
        lblSifre.setFont(new Font("Arial", Font.BOLD, 14));
        JPasswordField txtSifre = new JPasswordField();
        txtSifre.setFont(new Font("Arial", Font.BOLD, 14));

        pnlOrta.add(lblKul); pnlOrta.add(txtKullanici);
        pnlOrta.add(lblSifre); pnlOrta.add(txtSifre);

        JButton btnGiris = new JButton("SİSTEME GİRİŞ YAP");
        btnGiris.setBackground(new Color(128, 0, 32));
        btnGiris.setForeground(Color.WHITE);
        btnGiris.setFont(new Font("Arial", Font.BOLD, 15));
        
        final boolean[] basarili = {false};

        btnGiris.addActionListener(e -> {
            String kAd = txtKullanici.getText();
            String sifre = new String(txtSifre.getPassword());
            if (kAd.equals("Besiktas") && sifre.equals("1903")) {
                basarili[0] = true;
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Hatalı Kullanıcı Adı veya Şifre!", "Erişim Reddedildi", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginDialog.add(pnlOrta, BorderLayout.CENTER);
        loginDialog.add(btnGiris, BorderLayout.SOUTH);
        loginDialog.setVisible(true);

        return basarili[0];
    }

    private static void arayuzuOlustur() {
        if(frame != null) frame.dispose(); // Tema değişirse eskisini silip yenisini yaratıyoruz

        frame = new JFrame("E-Otel Rezervasyon Sistemi | Ultimate Görsel Sürüm");
        frame.setSize(1100, 750);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null); 
        frame.getContentPane().setBackground(getAnaBg());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                odaVerileriKaydet();
                musteriVerileriKaydet();
                rezervasyonVerileriKaydet();
                System.exit(0);
            }
        });

        frame.setLayout(new BorderLayout());

        // --- SOL MENÜ ---
        JPanel solMenu = new JPanel();
        solMenu.setLayout(new GridLayout(10, 1, 10, 10)); // Buton sayısı 10 oldu
        solMenu.setBackground(getMenuBg()); 
        solMenu.setPreferredSize(new Dimension(250, 0));
        solMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnDashboard = menuButonuOlustur("Ana Sayfa (Özet)");
        JButton btnOdaPlani = menuButonuOlustur("Görsel Oda Planı");
        JButton btnOdaEkle = menuButonuOlustur("Oda Ekle");
        JButton btnMusteriEkle = menuButonuOlustur("Müşteri Ekle");
        JButton btnRezervasyonYap = menuButonuOlustur("Rezervasyon Yap");
        JButton btnGuncelle = menuButonuOlustur("Kayıt Güncelle"); 
        JButton btnListele = menuButonuOlustur("Sistem Kayıtları");
        JButton btnIptal = menuButonuOlustur("Rezervasyon İptal");
        
        // TEMAYI DEĞİŞTİR BUTONU
        JButton btnTema = menuButonuOlustur(isDarkMode ? "☀️ Aydınlık Mod'a Geç" : "🌙 Karanlık Mod'a Geç");
        btnTema.setBackground(isDarkMode ? new Color(241, 196, 15) : new Color(44, 62, 80)); // Sarı veya Koyu Mavi

        solMenu.add(btnDashboard);
        solMenu.add(btnOdaPlani);
        solMenu.add(btnOdaEkle);
        solMenu.add(btnMusteriEkle);
        solMenu.add(btnRezervasyonYap);
        solMenu.add(btnGuncelle);
        solMenu.add(btnListele);
        solMenu.add(btnIptal);
        solMenu.add(new JLabel("")); // Boşluk yaratmak için
        solMenu.add(btnTema);

        // --- MERKEZ PANEL ---
        anaPanel = new JPanel();
        anaPanel.setLayout(new BorderLayout());
        anaPanel.setBackground(getAnaBg()); 
        anaPanel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, getBaslikColor())); 
        
        frame.add(solMenu, BorderLayout.WEST);
        frame.add(anaPanel, BorderLayout.CENTER);

        btnDashboard.addActionListener(e -> paneliDegistir(ekranDashboard()));
        btnOdaPlani.addActionListener(e -> paneliDegistir(ekranOdaPlani()));
        btnOdaEkle.addActionListener(e -> paneliDegistir(ekranOdaEkle()));
        btnMusteriEkle.addActionListener(e -> paneliDegistir(ekranMusteriEkle()));
        btnRezervasyonYap.addActionListener(e -> paneliDegistir(ekranRezervasyonYap()));
        btnGuncelle.addActionListener(e -> paneliDegistir(ekranGuncelle()));
        btnListele.addActionListener(e -> paneliDegistir(ekranListele()));
        btnIptal.addActionListener(e -> paneliDegistir(ekranRezervasyonIptal()));
        
        btnTema.addActionListener(e -> {
            isDarkMode = !isDarkMode; // Şalteri İndir/Kaldır
            arayuzuOlustur(); // Ekranı yeni renklerle baştan çiz
        });

        paneliDegistir(ekranDashboard());
        frame.setVisible(true);
    }

    private static void paneliDegistir(JPanel yeniPanel) {
        anaPanel.removeAll();
        yeniPanel.setBackground(getAnaBg()); 
        anaPanel.add(yeniPanel, BorderLayout.CENTER);
        anaPanel.revalidate();
        anaPanel.repaint();
    }

    // --- DİNAMİK GÖRSEL TASARIM METOTLARI ---
    private static JButton menuButonuOlustur(String metin) {
        JButton btn = new JButton(metin);
        btn.setForeground(Color.WHITE); 
        btn.setBackground(getBtnBg()); 
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); 
        btn.setOpaque(true);
        return btn;
    }

    private static JLabel etiketOlustur(String metin) {
        JLabel lbl = new JLabel(metin);
        lbl.setForeground(getTextColor());
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        return lbl;
    }

    private static JTextField sekilliTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Arial", Font.BOLD, 14));
        txt.setBackground(getTextBg());
        txt.setForeground(getTextColor());
        txt.setCaretColor(getTextColor()); // Yanıp sönen imleç rengi
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return txt;
    }

    private static JComboBox<String> sekilliComboBox() {
        JComboBox<String> cmb = new JComboBox<>();
        cmb.setFont(new Font("Arial", Font.BOLD, 14));
        cmb.setBackground(getTextBg());
        cmb.setForeground(getTextColor());
        cmb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return cmb;
    }

    private static JSpinner sekilliTarihSecici() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Arial", Font.BOLD, 14));
        editor.getTextField().setBackground(getTextBg());
        editor.getTextField().setForeground(getTextColor());
        spinner.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2), BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        return spinner;
    }

    private static void rakamVeLimitKoy(JTextField textField, int limit) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (text.isEmpty()) { super.replace(fb, offset, length, text, attrs); return; }
                if ((fb.getDocument().getLength() + text.length() - length) <= limit && text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                } else { Toolkit.getDefaultToolkit().beep(); }
            }
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= limit && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                } else { Toolkit.getDefaultToolkit().beep(); }
            }
        });
    }

    // ==========================================
    //            ARAYÜZ FORMLARI
    // ==========================================

    private static JPanel ekranDashboard() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel baslik = new JLabel("SİSTEM ÖZETİ (DASHBOARD)", SwingConstants.CENTER);
        baslik.setFont(new Font("Arial", Font.BOLD, 28));
        baslik.setForeground(getBaslikColor());
        panel.add(baslik, BorderLayout.NORTH);

        JPanel pnlKartlar = new JPanel(new GridLayout(2, 2, 25, 25)); // Kutular arası boşluğu biraz açtık
        pnlKartlar.setOpaque(false);

        int toplamMus = musteriler.size();
        int bosOda = 0, doluOda = 0;
        for (Oda o : odalar.values()) {
            if(o.getOdaMusait()) bosOda++; else doluOda++;
        }
        double toplamCiro = 0;
        for(Rezervasyon r : rezervasyonlar.values()) {
            toplamCiro += r.getToplamUcret();
        }

        // Renkleri artık sadece ince bir vurgu çizgisi için kullanacağız
        pnlKartlar.add(kartOlustur("Toplam Müşteri", String.valueOf(toplamMus), new Color(41, 128, 185))); // Mavi
        pnlKartlar.add(kartOlustur("Müsait Odalar", String.valueOf(bosOda), new Color(39, 174, 96))); // Yeşil
        pnlKartlar.add(kartOlustur("Dolu Odalar", String.valueOf(doluOda), new Color(192, 57, 43))); // Kırmızı
        pnlKartlar.add(kartOlustur("Aktif Ciro", toplamCiro + " TL", new Color(142, 68, 173))); // Mor

        panel.add(pnlKartlar, BorderLayout.CENTER);
        return panel;
    }

    // --- YENİLENMİŞ ŞIK KART TASARIMI ---
    private static JPanel kartOlustur(String baslik, String deger, Color vurguRengi) {
        JPanel kart = new JPanel(new BorderLayout());
        kart.setBackground(getTextBg()); // Kartın içi temaya göre temiz Beyaz veya Koyu Gri olacak
        
        // Profesyonel Kurumsal Çerçeve Tasarımı: 
        // Etrafında çok ince pastel bir çizgi ve SADECE sol tarafta kalın renkli bir şerit (MatteBorder)
        Color cerceveRengi = isDarkMode ? new Color(100, 100, 100) : new Color(220, 220, 220);
        kart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cerceveRengi, 1),
            BorderFactory.createMatteBorder(0, 8, 0, 0, vurguRengi)
        ));
        
        // Başlık (Sol Üst)
        JLabel lblBaslik = new JLabel(baslik);
        lblBaslik.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblBaslik.setForeground(isDarkMode ? new Color(200, 200, 200) : new Color(100, 100, 100)); // Hafif soluk profesyonel gri
        lblBaslik.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        // Değer (Orta Sol, Büyük Net Font)
        JLabel lblDeger = new JLabel(deger);
        lblDeger.setFont(new Font("SansSerif", Font.BOLD, 42));
        lblDeger.setForeground(getTextColor()); // Sayılar temaya göre net siyah veya beyaz
        lblDeger.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        kart.add(lblBaslik, BorderLayout.NORTH);
        kart.add(lblDeger, BorderLayout.CENTER);
        return kart;
    }

    private static JPanel ekranOdaPlani() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel baslik = new JLabel("GÖRSEL ODA PLANI", SwingConstants.CENTER);
        baslik.setFont(new Font("Arial", Font.BOLD, 24));
        baslik.setForeground(getBaslikColor());
        panel.add(baslik, BorderLayout.NORTH);

        JPanel pnlOdalar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pnlOdalar.setOpaque(false);

        if(odalar.isEmpty()){
            pnlOdalar.add(etiketOlustur("Sistemde henüz kayıtlı oda bulunmuyor."));
        } else {
            for (Oda o : odalar.values()) {
                JButton btnOda = new JButton("<html><center>Oda<br><b>" + o.getOdaNo() + "</b></center></html>");
                btnOda.setPreferredSize(new Dimension(100, 100));
                btnOda.setFont(new Font("Arial", Font.PLAIN, 16));
                btnOda.setForeground(Color.WHITE);
                btnOda.setFocusPainted(false);
                btnOda.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                btnOda.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                if (o.getOdaMusait()) {
                    btnOda.setBackground(new Color(39, 174, 96)); 
                    btnOda.setToolTipText("MÜSAİT - Detaylar için tıklayın");
                } else {
                    btnOda.setBackground(new Color(192, 57, 43)); 
                    btnOda.setToolTipText("DOLU - Detaylar için tıklayın");
                }
                
                btnOda.addActionListener(e -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("🏨 ODA BİLGİLERİ\n");
                    sb.append("----------------------------\n");
                    sb.append("Oda Numarası : ").append(o.getOdaNo()).append("\n");
                    sb.append("Oda Tipi     : ").append(o.getTip()).append("\n");
                    sb.append("Kapasite     : ").append(o.getKapasite()).append(" Kişi\n");
                    sb.append("Gecelik Fiyat: ").append(o.getOdaFiyat()).append(" TL\n");
                    sb.append("Açıklama     : ").append(o.getAciklama().isEmpty() ? "Yok" : o.getAciklama()).append("\n\n");

                    if (o.getOdaMusait()) {
                        sb.append("✅ DURUM: MÜSAİT\n");
                        sb.append("Bu oda şu an boş ve rezerve edilmeye hazır.");
                        JOptionPane.showMessageDialog(frame, sb.toString(), "Oda " + o.getOdaNo() + " Detayları", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        sb.append("❌ DURUM: DOLU\n\n");
                        sb.append("👤 KONAKLAYAN MÜŞTERİ\n");
                        sb.append("----------------------------\n");
                        for(Rezervasyon r : rezervasyonlar.values()){
                            if(r.getOda().getOdaNo() == o.getOdaNo() && r.getDurum().equals("Aktif")){
                                sb.append("Adı Soyadı   : ").append(r.getMusteri().getMusteriAdSoyad()).append("\n");
                                sb.append("Telefon      : ").append(r.getMusteri().getMusteriTelefon()).append("\n");
                                sb.append("Giriş Tarihi : ").append(r.getGirisTarihi()).append("\n");
                                sb.append("Çıkış Tarihi : ").append(r.getCikisTarihi()).append("\n");
                                sb.append("Toplam Tutar : ").append(r.getToplamUcret()).append(" TL\n");
                                break;
                            }
                        }
                        JOptionPane.showMessageDialog(frame, sb.toString(), "Oda " + o.getOdaNo() + " Detayları", JOptionPane.WARNING_MESSAGE);
                    }
                });
                pnlOdalar.add(btnOda);
            }
        }

        JScrollPane scroll = new JScrollPane(pnlOdalar);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel ekranOdaEkle() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel baslik = new JLabel("YENİ ODA EKLE");
        baslik.setFont(new Font("Arial", Font.BOLD, 22));
        baslik.setForeground(getBaslikColor()); 
        panel.add(baslik, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(6, 2, 15, 15));
        grid.setOpaque(false);
        
        JTextField txtOdaNo = sekilliTextField();
        txtOdaNo.setEditable(false); 
        
        int siradakiOdaNo = 101; 
        for (int key : odalar.keySet()) {
            if (key >= siradakiOdaNo) siradakiOdaNo = key + 1;
        }
        txtOdaNo.setText(String.valueOf(siradakiOdaNo));

        JTextField txtTip = sekilliTextField();
        JTextField txtKapasite = sekilliTextField(); rakamVeLimitKoy(txtKapasite, 3); 
        JTextField txtFiyat = sekilliTextField();
        
        JCheckBox chkMusait = new JCheckBox("Oda Kullanıma Uygun", true); 
        chkMusait.setOpaque(false); 
        chkMusait.setForeground(getTextColor());
        chkMusait.setFont(new Font("Arial", Font.BOLD, 14));
        
        JTextField txtAciklama = sekilliTextField();

        grid.add(etiketOlustur("Oda Numarası (Otomatik):")); grid.add(txtOdaNo);
        grid.add(etiketOlustur("Oda Tipi (Örn: Kral Dairesi):")); grid.add(txtTip);
        grid.add(etiketOlustur("Kapasite (Kişi):")); grid.add(txtKapasite);
        grid.add(etiketOlustur("Gecelik Fiyat (TL):")); grid.add(txtFiyat);
        grid.add(etiketOlustur("Mevcut Durum:")); grid.add(chkMusait);
        grid.add(etiketOlustur("Ekstra Açıklama:")); grid.add(txtAciklama);

        JButton btnKaydet = menuButonuOlustur("ODAYI KAYDET");
        btnKaydet.addActionListener(e -> {
            try {
                int no = Integer.parseInt(txtOdaNo.getText());
                int kap = Integer.parseInt(txtKapasite.getText());
                double fiyat = Double.parseDouble(txtFiyat.getText());

                Oda oda = new Oda(no, txtTip.getText(), kap, fiyat, chkMusait.isSelected(), txtAciklama.getText());
                odalar.put(no, oda);
                
                JOptionPane.showMessageDialog(frame, "Oda başarıyla eklendi!\n" + oda.toString(), "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                txtTip.setText(""); txtKapasite.setText(""); txtFiyat.setText(""); txtAciklama.setText(""); txtOdaNo.setText(String.valueOf(no + 1));
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Lütfen sayısal alanları eksiksiz giriniz!", "Hata", JOptionPane.ERROR_MESSAGE); }
        });

        panel.add(grid, BorderLayout.CENTER); panel.add(btnKaydet, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel ekranMusteriEkle() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel baslik = new JLabel("MÜŞTERİ KAYIT FORMU");
        baslik.setFont(new Font("Arial", Font.BOLD, 22));
        baslik.setForeground(getBaslikColor());
        panel.add(baslik, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(6, 2, 15, 15));
        grid.setOpaque(false);
        
        JTextField txtId = sekilliTextField(); txtId.setEditable(false); 
        int siradakiMusNo = 1; for (int key : musteriler.keySet()) if (key >= siradakiMusNo) siradakiMusNo = key + 1;
        txtId.setText(String.valueOf(siradakiMusNo));

        JTextField txtAd = sekilliTextField();
        JTextField txtTel = sekilliTextField(); rakamVeLimitKoy(txtTel, 11); 
        JTextField txtEmail = sekilliTextField();
        JTextField txtTc = sekilliTextField(); rakamVeLimitKoy(txtTc, 11); 
        JTextField txtAdres = sekilliTextField();

        grid.add(etiketOlustur("Müşteri No (Otomatik):")); grid.add(txtId);
        grid.add(etiketOlustur("Adı Soyadı:")); grid.add(txtAd);
        grid.add(etiketOlustur("Telefon (Örn: 05551234567):")); grid.add(txtTel);
        grid.add(etiketOlustur("E-Posta Adresi:")); grid.add(txtEmail);
        grid.add(etiketOlustur("TC Kimlik Numarası (11 Hane):")); grid.add(txtTc);
        grid.add(etiketOlustur("Açık Adres:")); grid.add(txtAdres);

        JButton btnKaydet = menuButonuOlustur("MÜŞTERİYİ KAYDET");
        btnKaydet.addActionListener(e -> {
            try {
                if (txtTc.getText().length() != 11) { JOptionPane.showMessageDialog(frame, "HATA: TC Kimlik tam 11 haneli olmalıdır!", "Eksik Giriş", JOptionPane.ERROR_MESSAGE); return; }
                int id = Integer.parseInt(txtId.getText());
                Musteri m = new Musteri(id, txtAd.getText(), txtTel.getText(), txtEmail.getText(), txtTc.getText(), txtAdres.getText());
                musteriler.put(id, m);
                
                JOptionPane.showMessageDialog(frame, "Müşteri sisteme eklendi:\n" + m.toString(), "Kayıt Başarılı", JOptionPane.INFORMATION_MESSAGE);
                txtAd.setText(""); txtTel.setText(""); txtEmail.setText(""); txtTc.setText(""); txtAdres.setText(""); txtId.setText(String.valueOf(id + 1));
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Lütfen alanları doldurunuz!", "Hata", JOptionPane.ERROR_MESSAGE); }
        });

        panel.add(grid, BorderLayout.CENTER); panel.add(btnKaydet, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel ekranGuncelle() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.setFont(new Font("Arial", Font.BOLD, 14));

        // Müşteri Güncelleme
        JPanel pnlMusteri = new JPanel(new BorderLayout(10, 10)); 
        pnlMusteri.setBackground(getAnaBg());
        
        JComboBox<String> cmbMusteri = sekilliComboBox();
        cmbMusteri.addItem("Lütfen Güncellenecek Müşteriyi Seçin...");
        for (Musteri m : musteriler.values()) cmbMusteri.addItem(m.getMusteriId() + " - " + m.getMusteriAdSoyad());

        JPanel gridMus = new JPanel(new GridLayout(5, 2, 10, 10)); gridMus.setOpaque(false);
        JTextField gTxtAd = sekilliTextField(); JTextField gTxtTel = sekilliTextField(); rakamVeLimitKoy(gTxtTel, 11);
        JTextField gTxtEmail = sekilliTextField(); JTextField gTxtTc = sekilliTextField(); rakamVeLimitKoy(gTxtTc, 11);
        JTextField gTxtAdres = sekilliTextField();

        gridMus.add(etiketOlustur("Adı Soyadı:")); gridMus.add(gTxtAd); gridMus.add(etiketOlustur("Telefon:")); gridMus.add(gTxtTel);
        gridMus.add(etiketOlustur("E-Posta:")); gridMus.add(gTxtEmail); gridMus.add(etiketOlustur("TC Kimlik:")); gridMus.add(gTxtTc);
        gridMus.add(etiketOlustur("Adres:")); gridMus.add(gTxtAdres);

        cmbMusteri.addActionListener(e -> {
            if(cmbMusteri.getSelectedIndex() > 0) {
                int mId = Integer.parseInt(cmbMusteri.getSelectedItem().toString().split(" - ")[0]);
                Musteri seciliM = musteriler.get(mId);
                gTxtAd.setText(seciliM.getMusteriAdSoyad()); gTxtTel.setText(seciliM.getMusteriTelefon());
                gTxtEmail.setText(seciliM.getMusteriEmail()); gTxtTc.setText(seciliM.getMusteriTcKimlikNo());
                gTxtAdres.setText(seciliM.getMusteriAdres());
            }
        });

        JButton btnMusGuncelle = menuButonuOlustur("MÜŞTERİYİ GÜNCELLE");
        btnMusGuncelle.addActionListener(e -> {
            if(cmbMusteri.getSelectedIndex() > 0) {
                int mId = Integer.parseInt(cmbMusteri.getSelectedItem().toString().split(" - ")[0]);
                Musteri m = musteriler.get(mId);
                m.setMusteriAdSoyad(gTxtAd.getText()); m.setMusteriTelefon(gTxtTel.getText());
                m.setMusteriEmail(gTxtEmail.getText()); m.setMusteriTcKimlikNo(gTxtTc.getText()); m.setMusteriAdres(gTxtAdres.getText());
                JOptionPane.showMessageDialog(frame, "Müşteri başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        pnlMusteri.add(cmbMusteri, BorderLayout.NORTH); pnlMusteri.add(gridMus, BorderLayout.CENTER); pnlMusteri.add(btnMusGuncelle, BorderLayout.SOUTH);

        // Oda Güncelleme
        JPanel pnlOda = new JPanel(new BorderLayout(10, 10)); 
        pnlOda.setBackground(getAnaBg());
        
        JComboBox<String> cmbOda = sekilliComboBox();
        cmbOda.addItem("Lütfen Güncellenecek Odayı Seçin...");
        for (Oda o : odalar.values()) cmbOda.addItem(o.getOdaNo() + " - " + o.getTip());

        JPanel gridOda = new JPanel(new GridLayout(5, 2, 10, 10)); gridOda.setOpaque(false);
        JTextField gTxtTip = sekilliTextField(); JTextField gTxtKap = sekilliTextField(); rakamVeLimitKoy(gTxtKap, 3);
        JTextField gTxtFiyat = sekilliTextField(); 
        JCheckBox gChkMusait = new JCheckBox("Oda Müsait"); gChkMusait.setOpaque(false); gChkMusait.setForeground(getTextColor());
        JTextField gTxtAciklama = sekilliTextField();

        gridOda.add(etiketOlustur("Oda Tipi:")); gridOda.add(gTxtTip); gridOda.add(etiketOlustur("Kapasite:")); gridOda.add(gTxtKap);
        gridOda.add(etiketOlustur("Fiyat (TL):")); gridOda.add(gTxtFiyat); gridOda.add(etiketOlustur("Durum:")); gridOda.add(gChkMusait);
        gridOda.add(etiketOlustur("Açıklama:")); gridOda.add(gTxtAciklama);

        cmbOda.addActionListener(e -> {
            if(cmbOda.getSelectedIndex() > 0) {
                int oId = Integer.parseInt(cmbOda.getSelectedItem().toString().split(" - ")[0]);
                Oda seciliO = odalar.get(oId);
                gTxtTip.setText(seciliO.getTip()); gTxtKap.setText(String.valueOf(seciliO.getKapasite()));
                gTxtFiyat.setText(String.valueOf(seciliO.getOdaFiyat())); gChkMusait.setSelected(seciliO.getOdaMusait());
                gTxtAciklama.setText(seciliO.getAciklama());
            }
        });

        JButton btnOdaGuncelle = menuButonuOlustur("ODAYI GÜNCELLE");
        btnOdaGuncelle.addActionListener(e -> {
            if(cmbOda.getSelectedIndex() > 0) {
                int oId = Integer.parseInt(cmbOda.getSelectedItem().toString().split(" - ")[0]);
                Oda o = odalar.get(oId);
                o.setTip(gTxtTip.getText()); o.setKapasite(Integer.parseInt(gTxtKap.getText()));
                o.setOdaFiyat(Double.parseDouble(gTxtFiyat.getText())); o.setOdaMusait(gChkMusait.isSelected()); o.setAciklama(gTxtAciklama.getText());
                JOptionPane.showMessageDialog(frame, "Oda başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        pnlOda.add(cmbOda, BorderLayout.NORTH); pnlOda.add(gridOda, BorderLayout.CENTER); pnlOda.add(btnOdaGuncelle, BorderLayout.SOUTH);

        sekmeler.addTab("Müşteri Düzenle", pnlMusteri); sekmeler.addTab("Oda Düzenle", pnlOda);
        panel.add(sekmeler, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel ekranRezervasyonYap() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel baslik = new JLabel("YENİ REZERVASYON OLUŞTUR");
        baslik.setFont(new Font("Arial", Font.BOLD, 22));
        baslik.setForeground(getBaslikColor());
        panel.add(baslik, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(5, 2, 15, 15));
        grid.setOpaque(false);
        
        JTextField txtRezNo = sekilliTextField(); txtRezNo.setEditable(false); 
        int siradakiNo = 1000; for (int key : rezervasyonlar.keySet()) if (key >= siradakiNo) siradakiNo = key + 1;
        txtRezNo.setText(String.valueOf(siradakiNo));

        JComboBox<String> cmbMusteri = sekilliComboBox();
        if (musteriler.isEmpty()) { cmbMusteri.addItem("Kayıtlı Müşteri Yok!"); cmbMusteri.setEnabled(false); } 
        else { for (Musteri m : musteriler.values()) cmbMusteri.addItem(m.getMusteriId() + " - " + m.getMusteriAdSoyad()); }

        JComboBox<String> cmbOda = sekilliComboBox();
        if (odalar.isEmpty()) { cmbOda.addItem("Kayıtlı Oda Yok!"); cmbOda.setEnabled(false); } 
        else { for (Oda o : odalar.values()) cmbOda.addItem(o.getOdaNo() + " - " + o.getTip() + " (" + o.getOdaFiyat() + " TL)"); }
        
        JSpinner spinGiris = sekilliTarihSecici(); JSpinner spinCikis = sekilliTarihSecici();

        grid.add(etiketOlustur("Rezervasyon No (Otomatik):")); grid.add(txtRezNo);
        grid.add(etiketOlustur("Müşteri Seçin:")); grid.add(cmbMusteri);
        grid.add(etiketOlustur("Oda Seçin:")); grid.add(cmbOda);
        grid.add(etiketOlustur("Giriş Tarihi:")); grid.add(spinGiris);
        grid.add(etiketOlustur("Çıkış Tarihi:")); grid.add(spinCikis);

        JButton btnKaydet = menuButonuOlustur("REZERVASYONU TAMAMLA");
        btnKaydet.addActionListener(e -> {
            try {
                if (musteriler.isEmpty() || odalar.isEmpty()) { JOptionPane.showMessageDialog(frame, "Rezervasyon için en az 1 Müşteri ve Oda gereklidir!", "Eksik Veri", JOptionPane.ERROR_MESSAGE); return; }

                int rNo = Integer.parseInt(txtRezNo.getText());
                int mNo = Integer.parseInt(((String) cmbMusteri.getSelectedItem()).split(" - ")[0]);
                int oNo = Integer.parseInt(((String) cmbOda.getSelectedItem()).split(" - ")[0]);
                LocalDate giris = ((Date) spinGiris.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate cikis = ((Date) spinCikis.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if(cikis.isBefore(giris) || cikis.isEqual(giris)) { JOptionPane.showMessageDialog(frame, "HATA: Çıkış tarihi girişten sonra olmalıdır!", "Tarih Hatası", JOptionPane.ERROR_MESSAGE); return; }

                Musteri m = musteriler.get(mNo); Oda o = odalar.get(oNo);
                IntervalDugumu cakisan = intervalAgaci.cakismaKontrol(giris, cikis);
                boolean uygun = (cakisan == null);
                double toplamUcret = o.getOdaFiyat() * (cikis.toEpochDay() - giris.toEpochDay());

                if (!uygun) {
                    BeklemeListe liste = beklemeListeleri.get(oNo);
                    if (liste == null) { liste = new BeklemeListe(); beklemeListeleri.put(oNo, liste); }
                    liste.ekle(rNo, m, oNo, giris, cikis);
                    JOptionPane.showMessageDialog(frame, "Tarih çakışması! Rezervasyon bekleme listesine alındı.", "Bekleme Listesi", JOptionPane.WARNING_MESSAGE);
                } else {
                    Rezervasyon r = new Rezervasyon(rNo, m, o, giris, cikis, toplamUcret, "Aktif");
                    rezervasyonlar.put(rNo, r); intervalAgaci.ekle(oNo, giris, cikis); o.setOdaMusait(false);
                    
                    JOptionPane.showMessageDialog(frame, "Rezervasyon başarıyla yapıldı!\nToplam Tutar: " + toplamUcret + " TL", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    txtRezNo.setText(String.valueOf(rNo + 1));

                    int faturaCevap = JOptionPane.showConfirmDialog(frame, "Müşteri için Fatura/Fiş yazılsın mı?", "Fatura İşlemi", JOptionPane.YES_NO_OPTION);
                    if(faturaCevap == JOptionPane.YES_OPTION) faturaYazdir(r);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Hatalı işlem!", "Kritik Hata", JOptionPane.ERROR_MESSAGE); }
        });

        panel.add(grid, BorderLayout.CENTER); panel.add(btnKaydet, BorderLayout.SOUTH);
        return panel;
    }

    // --- YENİLENMİŞ, SORUNSUZ FATURA YAZDIRMA METODU ---
    private static void faturaYazdir(Rezervasyon r) {
        // Profesyonel "Farklı Kaydet" Ekranı Açılır
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Faturayı Nereye Kaydetmek İstersiniz?");
        fileChooser.setSelectedFile(new File("Fatura_" + r.getRezervasyonNo() + ".txt")); // Varsayılan isim

        int kullaniciSecimi = fileChooser.showSaveDialog(frame);

        if (kullaniciSecimi == JFileChooser.APPROVE_OPTION) {
            File faturaDosyasi = fileChooser.getSelectedFile();
            
            try (PrintWriter out = new PrintWriter(new FileWriter(faturaDosyasi))) {
                out.println("=========================================");
                out.println("           E-OTEL REZERVASYON            ");
                out.println("                FATURASI                 ");
                out.println("=========================================");
                out.println("İşlem Tarihi   : " + LocalDate.now());
                out.println("Rezervasyon No : " + r.getRezervasyonNo());
                out.println("-----------------------------------------");
                out.println("MÜŞTERİ BİLGİLERİ:");
                out.println("Ad Soyad       : " + r.getMusteri().getMusteriAdSoyad());
                out.println("TC Kimlik      : " + r.getMusteri().getMusteriTcKimlikNo());
                out.println("-----------------------------------------");
                out.println("KONAKLAMA BİLGİLERİ:");
                out.println("Oda Numarası   : " + r.getOda().getOdaNo());
                out.println("Oda Tipi       : " + r.getOda().getTip());
                out.println("Giriş Tarihi   : " + r.getGirisTarihi());
                out.println("Çıkış Tarihi   : " + r.getCikisTarihi());
                long geceSayisi = r.getCikisTarihi().toEpochDay() - r.getGirisTarihi().toEpochDay();
                out.println("Gece Sayısı    : " + geceSayisi + " Gece");
                out.println("-----------------------------------------");
                out.println("TOPLAM TUTAR   : " + r.getToplamUcret() + " TL");
                out.println("=========================================");
                out.println("Bizi tercih ettiğiniz için teşekkür ederiz.");
                
                JOptionPane.showMessageDialog(frame, "Fatura başarıyla kaydedildi!\nKonum: " + faturaDosyasi.getAbsolutePath(), "Fatura Hazır", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(frame, "Fatura oluşturulurken hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }

    private static JPanel ekranListele() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel ustPanel = new JPanel(new BorderLayout(10, 10)); ustPanel.setOpaque(false);
        JPanel butonlar = new JPanel(new FlowLayout()); butonlar.setOpaque(false);
        JButton btnOdalar = menuButonuOlustur("Odalar"); JButton btnMusteriler = menuButonuOlustur("Müşteriler");
        JButton btnRez = menuButonuOlustur("Aktif Rezervasyonlar"); JButton btnGecmis = menuButonuOlustur("Tamamlananlar"); 
        butonlar.add(btnOdalar); butonlar.add(btnMusteriler); butonlar.add(btnRez); butonlar.add(btnGecmis);

        JPanel aramaPaneli = new JPanel(new BorderLayout(5, 5)); aramaPaneli.setOpaque(false);
        JLabel lblAra = etiketOlustur("🔍 Tabloda Ara: "); lblAra.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField txtArama = sekilliTextField();
        aramaPaneli.add(lblAra, BorderLayout.WEST); aramaPaneli.add(txtArama, BorderLayout.CENTER);

        ustPanel.add(butonlar, BorderLayout.NORTH); ustPanel.add(aramaPaneli, BorderLayout.SOUTH);

        DefaultTableModel tabloModel = new DefaultTableModel() { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable tablo = new JTable(tabloModel);
        tablo.setFont(new Font("Arial", Font.PLAIN, 14)); tablo.setRowHeight(25);
        tablo.setBackground(getTextBg());
        tablo.setForeground(getTextColor());
        tablo.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        tablo.getTableHeader().setBackground(getBtnBg()); tablo.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(tablo); scrollPane.setBorder(BorderFactory.createLineBorder(getBaslikColor(), 2));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tabloModel); tablo.setRowSorter(sorter);

        txtArama.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { ara(); } public void removeUpdate(DocumentEvent e) { ara(); } public void changedUpdate(DocumentEvent e) { ara(); }
            private void ara() {
                String text = txtArama.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null); else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); 
            }
        });

        btnOdalar.addActionListener(e -> {
            tabloModel.setRowCount(0); tabloModel.setColumnIdentifiers(new String[]{"Oda No", "Tip", "Kapasite", "Fiyat", "Durum", "Açıklama"});
            for (Oda o : odalar.values()) tabloModel.addRow(new Object[]{o.getOdaNo(), o.getTip(), o.getKapasite(), o.getOdaFiyat() + " TL", o.getOdaMusait() ? "Boş" : "Dolu", o.getAciklama()});
            txtArama.setText("");
        });

        btnMusteriler.addActionListener(e -> {
            tabloModel.setRowCount(0); tabloModel.setColumnIdentifiers(new String[]{"Müşteri No", "Ad Soyad", "Telefon", "E-Posta", "TC Kimlik", "Adres"});
            for (Musteri m : musteriler.values()) tabloModel.addRow(new Object[]{m.getMusteriId(), m.getMusteriAdSoyad(), m.getMusteriTelefon(), m.getMusteriEmail(), m.getMusteriTcKimlikNo(), m.getMusteriAdres()});
            txtArama.setText("");
        });

        btnRez.addActionListener(e -> {
            tabloModel.setRowCount(0); tabloModel.setColumnIdentifiers(new String[]{"Rez No", "Müşteri", "Oda", "Giriş", "Çıkış", "Tutar", "Durum"});
            for (Rezervasyon r : rezervasyonlar.values()) tabloModel.addRow(new Object[]{r.getRezervasyonNo(), r.getMusteri().getMusteriAdSoyad(), r.getOda().getOdaNo(), r.getGirisTarihi(), r.getCikisTarihi(), r.getToplamUcret() + " TL", r.getDurum()});
            txtArama.setText("");
        });

        btnGecmis.addActionListener(e -> {
            tabloModel.setRowCount(0);
            JOptionPane.showMessageDialog(frame, "Geçmiş rezervasyonlar BST yapısı gereği terminale yazdırılmıştır.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            tamamlananRezervasyonlar.kronolojikListele();
        });

        panel.add(ustPanel, BorderLayout.NORTH); panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel ekranRezervasyonIptal() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel ustPanel = new JPanel(new FlowLayout()); ustPanel.setOpaque(false);
        JComboBox<String> cmbIptal = sekilliComboBox();
        JButton btnIptal = menuButonuOlustur("İPTAL ET"); btnIptal.setBackground(Color.BLACK); 

        if (rezervasyonlar.isEmpty()) {
            cmbIptal.addItem("Aktif rezervasyon bulunmuyor!"); cmbIptal.setEnabled(false); btnIptal.setEnabled(false); 
        } else {
            for (Rezervasyon r : rezervasyonlar.values()) cmbIptal.addItem(r.getRezervasyonNo() + " - " + r.getMusteri().getMusteriAdSoyad() + " (Oda: " + r.getOda().getOdaNo() + ")");
        }

        ustPanel.add(etiketOlustur("İptal Edilecek Rezervasyon: ")); ustPanel.add(cmbIptal); ustPanel.add(btnIptal);

        JTextArea txtEkran = new JTextArea(); txtEkran.setEditable(false); txtEkran.setFont(new Font("Arial", Font.BOLD, 14));
        txtEkran.setBackground(getTextBg()); txtEkran.setForeground(getTextColor());
        txtEkran.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3)); JScrollPane scrollPane = new JScrollPane(txtEkran);

        btnIptal.addActionListener(e -> {
            try {
                if (rezervasyonlar.isEmpty()) return;
                String secilen = (String) cmbIptal.getSelectedItem();
                int no = Integer.parseInt(secilen.split(" - ")[0]);
                Rezervasyon iptalRez = rezervasyonlar.get(no);

                int onay = JOptionPane.showConfirmDialog(frame, no + " numaralı rezervasyonu iptal etmek?", "Onay", JOptionPane.YES_NO_OPTION);
                if(onay == JOptionPane.YES_OPTION) {
                    Oda iptalOda = iptalRez.getOda(); rezervasyonlar.remove(no); iptalRez.setDurum("İptal");
                    tamamlananRezervasyonlar.ekle(iptalRez); intervalAgaci.sil(iptalOda.getOdaNo(), iptalRez.getGirisTarihi(), iptalRez.getCikisTarihi());

                    StringBuilder sonucMesaji = new StringBuilder(">> Rezervasyon İptal Edildi (No: " + no + ")\n");
                    BeklemeListe liste = beklemeListeleri.get(iptalOda.getOdaNo());
                    if (liste != null && !liste.bosMu()) {
                        BeklemeDugumu siradaki = liste.cikar();
                        double yeniUcret = iptalOda.getOdaFiyat() * (siradaki.cikisTarihi.toEpochDay() - siradaki.girisTarihi.toEpochDay());
                        Rezervasyon yeniRez = new Rezervasyon(siradaki.rezervasyonNo, siradaki.musteri, iptalOda, siradaki.girisTarihi, siradaki.cikisTarihi, yeniUcret, "Aktif");
                        rezervasyonlar.put(yeniRez.getRezervasyonNo(), yeniRez); iptalOda.setOdaMusait(false);
                        sonucMesaji.append("\n>> BİLGİ: Bekleme listesinden yeni rezervasyon oluşturuldu!\n   Müşteri: ").append(siradaki.musteri.getMusteriAdSoyad());
                    } else { iptalOda.setOdaMusait(true); sonucMesaji.append("\n>> BİLGİ: Bekleme listesi boş. Oda müsait duruma getirildi."); }
                    txtEkran.setText(sonucMesaji.toString());

                    cmbIptal.removeAllItems();
                    if (rezervasyonlar.isEmpty()) { cmbIptal.addItem("Aktif rezervasyon yok!"); cmbIptal.setEnabled(false); btnIptal.setEnabled(false); } 
                    else { for (Rezervasyon r : rezervasyonlar.values()) cmbIptal.addItem(r.getRezervasyonNo() + " - " + r.getMusteri().getMusteriAdSoyad() + " (Oda: " + r.getOda().getOdaNo() + ")"); }
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "İptal sırasında hata!", "Hata", JOptionPane.ERROR_MESSAGE); }
        });

        panel.add(ustPanel, BorderLayout.NORTH); panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    //    DOSYA OKUMA VE YAZMA METOTLARI
    // ==========================================

    public static void odaVerileriKaydet(){
        try(PrintWriter writer = new PrintWriter(new FileWriter("odalar.csv"))){
            for(Oda o: odalar.values()) writer.println(o.getOdaNo() + ","+ o.getTip() + ","+ o.getKapasite() + ","+ o.getOdaFiyat() + ","+ o.getOdaMusait() + ","+ o.getAciklama());
        } catch (IOException e){ }
    }
    public static void odaVerileriniYukle(){
        File file = new File("odalar.csv"); if(!file.exists()) return; 
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String satir; while((satir = reader.readLine()) != null){
                String[] parca = satir.split(",");
                Oda oda = new Oda(Integer.parseInt(parca[0]), parca[1], Integer.parseInt(parca[2]), Double.parseDouble(parca[3]), Boolean.parseBoolean(parca[4]), parca[5]); 
                oda.setOdaMusait(Boolean.parseBoolean(parca[4])); odalar.put(Integer.parseInt(parca[0]), oda); 
            }
        } catch (IOException e){ }
    }
    public static void musteriVerileriKaydet(){
        try(PrintWriter writer = new PrintWriter(new FileWriter("musteriler.csv"))){
            for(Musteri m: musteriler.values()) writer.println(m.getMusteriId() + ","+ m.getMusteriAdSoyad() + ","+ m.getMusteriTelefon() + ","+ m.getMusteriEmail() + ","+ m.getMusteriTcKimlikNo() + ","+ m.getMusteriAdres());
        } catch (IOException e){ }
    }
    public static void musteriVerileriYukle(){
        File file = new File("musteriler.csv"); if(!file.exists()) return;
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String satir; while((satir = reader.readLine()) != null){
                String[] parca = satir.split(",");
                Musteri musteri = new Musteri(Integer.parseInt(parca[0]), parca[1], parca[2], parca[3], parca[4], parca[5]); 
                musteriler.put(Integer.parseInt(parca[0]), musteri); 
            }
        } catch (IOException e){ }
    }
    public static void rezervasyonVerileriKaydet(){
        try(PrintWriter writer = new PrintWriter(new FileWriter("rezervasyonlar.csv"))){ 
            for(Rezervasyon r: rezervasyonlar.values()) writer.println(r.getRezervasyonNo() +","+ r.getMusteri().getMusteriId() +","+ r.getOda().getOdaNo() +","+ r.getGirisTarihi() +","+ r.getCikisTarihi() +","+ r.getToplamUcret() +","+ r.getDurum());
        } catch (IOException e){ }
    }
    public static void rezervasyonVerileriYukle(){
        File file = new File("rezervasyonlar.csv"); if(!file.exists()) return; 
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){ 
            String satir; while((satir = reader.readLine()) != null){ 
                String[] parca = satir.split(","); 
                int rNo = Integer.parseInt(parca[0]); int mId = Integer.parseInt(parca[1]); int oNo = Integer.parseInt(parca[2]);
                Musteri musteri = musteriler.get(mId); Oda oda = odalar.get(oNo);
                if(musteri != null && oda != null){
                    Rezervasyon rezervasyon = new Rezervasyon(rNo, musteri, oda, LocalDate.parse(parca[3]), LocalDate.parse(parca[4]), Double.parseDouble(parca[5]), parca[6]); 
                    rezervasyonlar.put(rNo, rezervasyon);
                }
            }
        } catch(Exception e){ }
    }
}