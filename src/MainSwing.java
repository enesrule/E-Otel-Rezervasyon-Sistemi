import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

// --- GÖRSELLİK DEĞİŞKENLERİ ---
private static JFrame frame;
private static JPanel anaPanel;
private static JButton aktifButon = null; // Hangi menü butonuna tıklandığını takip ediyoruz bunla

// --- PROFESYONEL RENK PALETİ ---
private static final Color L_BG          = new Color(247, 249, 252); // Çok açık gri-mavi
private static final Color L_SIDEBAR     = new Color(30, 41, 59);    // Lacivert sidebar
private static final Color L_SIDEBAR_HOV = new Color(51, 65, 85);
private static final Color L_SIDEBAR_ACT = new Color(59, 130, 246);  // Aktif buton rengi (mavi)
private static final Color L_CARD        = Color.WHITE;
private static final Color L_TEXT        = new Color(15, 23, 42);
private static final Color L_TEXT_SOFT   = new Color(100, 116, 139);
private static final Color L_BORDER      = new Color(226, 232, 240);
private static final Color L_ACCENT      = new Color(59, 130, 246);  // Mavi vurgu
private static final Color L_ACCENT_HOV  = new Color(37, 99, 235);

// Dinamik renk getter'ları
private static Color getAnaBg()        { return L_BG; }
private static Color getSidebarBg()    { return L_SIDEBAR; }
private static Color getSidebarHover() { return L_SIDEBAR_HOV; }
private static Color getCardBg()       { return L_CARD; }
private static Color getTextColor()    { return L_TEXT; }
private static Color getTextSoft()     { return L_TEXT_SOFT; }
private static Color getBorderColor()  { return L_BORDER; }
private static Color getAccent()       { return L_ACCENT; }
private static Color getAccentHover()  { return L_ACCENT_HOV; }

// --- FONT ---
private static Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
private static Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
private static Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 24);
private static Font FONT_BIG     = new Font("Segoe UI", Font.BOLD, 36);

public static void main(String[] args) {
    // Türkçe karakter desteği için sistem encoding ayarı
    System.setProperty("file.encoding", "UTF-8");

    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {}

    odaVerileriniYukle();
    musteriVerileriYukle();
    rezervasyonVerileriYukle();
    suresiGecmisRezervasyonlariTamamla(); //program açılınca çıkış tarihi geçmiş tüm "Aktif" rezervasyonları tarar, "Tamamlandı" yapar, odayı boşaltır

    SwingUtilities.invokeLater(() -> {
        if (girisEkraniGoster()) {
            arayuzuOlustur();
        } else {
            System.exit(0);
        }
    });
}

// --- GİRİŞ EKRANI ---
private static boolean girisEkraniGoster() {
    JDialog loginDialog = new JDialog((Frame) null, "E-Otel | Giriş", true);
    // GİRİŞ EKRANI İÇİN İKON AYARI
    java.net.URL iconURL = MainSwing.class.getResource("/logo.png");
    if (iconURL != null) {
        loginDialog.setIconImage(new ImageIcon(iconURL).getImage()); // Eğer yukarıdaki değişken adın farklıysa 'girisFrame' yazan yeri onunla değiştir.
    }
    loginDialog.setSize(420, 360);
    loginDialog.setLocationRelativeTo(null);
    loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    loginDialog.setLayout(new BorderLayout());
    loginDialog.getContentPane().setBackground(L_BG);

    JPanel pnlIcerik = new JPanel();
    pnlIcerik.setLayout(new BoxLayout(pnlIcerik, BoxLayout.Y_AXIS));
    pnlIcerik.setBackground(L_BG);
    pnlIcerik.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

    JLabel lblLogo = new JLabel("E-OTEL");
    lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
    lblLogo.setForeground(L_TEXT);
    lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel lblAlt = new JLabel("Rezervasyon Yönetim Sistemi");
    lblAlt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblAlt.setForeground(L_TEXT_SOFT);
    lblAlt.setAlignmentX(Component.CENTER_ALIGNMENT);
    lblAlt.setBorder(BorderFactory.createEmptyBorder(5, 0, 25, 0));

    JLabel lblKul = new JLabel("Kullanıcı Adı");
    lblKul.setFont(FONT_BOLD);
    lblKul.setForeground(L_TEXT);
    lblKul.setAlignmentX(Component.LEFT_ALIGNMENT);

    JTextField txtKullanici = new JTextField();
    txtKullanici.setFont(FONT_REGULAR);
    txtKullanici.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    txtKullanici.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(L_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    txtKullanici.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel lblSifre = new JLabel("Şifre");
    lblSifre.setFont(FONT_BOLD);
    lblSifre.setForeground(L_TEXT);
    lblSifre.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
    lblSifre.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPasswordField txtSifre = new JPasswordField();
    txtSifre.setFont(FONT_REGULAR);
    txtSifre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    txtSifre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(L_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    txtSifre.setAlignmentX(Component.LEFT_ALIGNMENT);

    JButton btnGiris = new JButton("Giriş Yap");
    btnGiris.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnGiris.setBackground(L_ACCENT);
    btnGiris.setForeground(Color.WHITE);
    btnGiris.setFocusPainted(false);
    btnGiris.setBorderPainted(false);
    btnGiris.setOpaque(true);
    btnGiris.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnGiris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    btnGiris.setAlignmentX(Component.LEFT_ALIGNMENT);
    btnGiris.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

    btnGiris.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) { btnGiris.setBackground(L_ACCENT_HOV); }
        public void mouseExited(MouseEvent e)  { btnGiris.setBackground(L_ACCENT); }
    });

    pnlIcerik.add(lblLogo);
    pnlIcerik.add(lblAlt);
    pnlIcerik.add(lblKul);
    pnlIcerik.add(Box.createVerticalStrut(6));
    pnlIcerik.add(txtKullanici);
    pnlIcerik.add(lblSifre);
    pnlIcerik.add(Box.createVerticalStrut(6));
    pnlIcerik.add(txtSifre);
    pnlIcerik.add(Box.createVerticalStrut(20));
    pnlIcerik.add(btnGiris);

    final boolean[] basarili = {false};

    ActionListener girisAction = e -> {
        String kAd = txtKullanici.getText();
        String sifre = new String(txtSifre.getPassword());
        if (kAd.equals("Besiktas") && sifre.equals("1903")) {
            basarili[0] = true;
            loginDialog.dispose();
        } else {
            JOptionPane.showMessageDialog(loginDialog,
                    "Hatalı kullanıcı adı veya şifre!",
                    "Erişim Reddedildi", JOptionPane.ERROR_MESSAGE);
        }
    };
    btnGiris.addActionListener(girisAction);
    txtSifre.addActionListener(girisAction);

    loginDialog.add(pnlIcerik, BorderLayout.CENTER);
    loginDialog.setVisible(true);

    return basarili[0];
}

private static void arayuzuOlustur() {
    if (frame != null) frame.dispose();

    frame = new JFrame("E-Otel | Rezervasyon Yönetim Sistemi");
    // İKON EKLEME KODU
    java.net.URL iconURL = MainSwing.class.getResource("/logo.png");
    if (iconURL != null) {
        frame.setIconImage(new ImageIcon(iconURL).getImage());
    } else {
        System.out.println("HATA: logo.png dosyasi src klasorunde bulunamadi!");
    }
    frame.setSize(1200, 780);
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

    // --- SOL MENÜ (modern sidebar) ---
    JPanel solMenu = new JPanel();
    solMenu.setLayout(new BoxLayout(solMenu, BoxLayout.Y_AXIS));
    solMenu.setBackground(getSidebarBg());
    solMenu.setPreferredSize(new Dimension(240, 0));
    solMenu.setBorder(BorderFactory.createEmptyBorder(25, 18, 25, 18));

    // Logo alanı
    JLabel lblLogo = new JLabel("E-OTEL");
    lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lblLogo.setForeground(Color.WHITE);
    lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 8, 6, 0));

    JLabel lblAltBilgi = new JLabel("Yönetim Paneli");
    lblAltBilgi.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lblAltBilgi.setForeground(new Color(148, 163, 184));
    lblAltBilgi.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblAltBilgi.setBorder(BorderFactory.createEmptyBorder(0, 8, 25, 0));

    solMenu.add(lblLogo);
    solMenu.add(lblAltBilgi);

    // Menü butonları
    JButton btnDashboard      = menuButonuOlustur("Ana Sayfa");
    JButton btnOdaPlani       = menuButonuOlustur("Oda Planı");
    JButton btnOdaEkle        = menuButonuOlustur("Oda Ekle");
    JButton btnMusteriEkle    = menuButonuOlustur("Müşteri Ekle");
    JButton btnRezervasyonYap = menuButonuOlustur("Rezervasyon Yap");
    JButton btnGuncelle       = menuButonuOlustur("Kayıt Güncelle");
    JButton btnListele        = menuButonuOlustur("Sistem Kayıtları");
    JButton btnIptal          = menuButonuOlustur("Rezervasyon İptal");

    for (JButton b : new JButton[]{btnDashboard, btnOdaPlani, btnOdaEkle, btnMusteriEkle,
            btnRezervasyonYap, btnGuncelle, btnListele, btnIptal}) {
        solMenu.add(b);
        solMenu.add(Box.createVerticalStrut(6));
    }

    solMenu.add(Box.createVerticalGlue());

    // --- MERKEZ PANEL ---
    anaPanel = new JPanel(new BorderLayout());
    anaPanel.setBackground(getAnaBg());

    frame.add(solMenu, BorderLayout.WEST);
    frame.add(anaPanel, BorderLayout.CENTER);

    btnDashboard.addActionListener(e -> { menuAktifYap(btnDashboard); paneliDegistir(ekranDashboard()); });
    btnOdaPlani.addActionListener(e -> { menuAktifYap(btnOdaPlani); paneliDegistir(ekranOdaPlani()); });
    btnOdaEkle.addActionListener(e -> { menuAktifYap(btnOdaEkle); paneliDegistir(ekranOdaEkle()); });
    btnMusteriEkle.addActionListener(e -> { menuAktifYap(btnMusteriEkle); paneliDegistir(ekranMusteriEkle()); });
    btnRezervasyonYap.addActionListener(e -> { menuAktifYap(btnRezervasyonYap); paneliDegistir(ekranRezervasyonYap()); });
    btnGuncelle.addActionListener(e -> { menuAktifYap(btnGuncelle); paneliDegistir(ekranGuncelle()); });
    btnListele.addActionListener(e -> { menuAktifYap(btnListele); paneliDegistir(ekranListele()); });
    btnIptal.addActionListener(e -> { menuAktifYap(btnIptal); paneliDegistir(ekranRezervasyonIptal()); });

    // Açılışta Ana Sayfa aktif görünsün
    menuAktifYap(btnDashboard);
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

// --- AKTİF MENÜ BUTONU YÖNETİMİ ---
private static void menuAktifYap(JButton tiklananButon) {
    // Önceki aktif butonun rengini sıfırla
    if (aktifButon != null && aktifButon != tiklananButon) {
        aktifButon.setBackground(getSidebarBg()); // lacivert yap
        aktifButon.setForeground(new Color(226, 232, 240));
        aktifButon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aktifButon.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
    }
    // Yeni aktif butonu vurgula
    aktifButon = tiklananButon;
    aktifButon.setBackground(L_SIDEBAR_ACT);
    aktifButon.setForeground(Color.WHITE);
    aktifButon.setFont(new Font("Segoe UI", Font.BOLD, 13));
    // Sol tarafta beyaz çizgi ekle — aktif göstergesi
    aktifButon.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 3, 0, 0, Color.WHITE),
        BorderFactory.createEmptyBorder(11, 11, 11, 14)
    ));
}

// --- MODERN MENÜ BUTONU ---
private static JButton menuButonuOlustur(String metin) {
    JButton btn = new JButton(metin);
    btn.setForeground(new Color(226, 232, 240));
    btn.setBackground(getSidebarBg());
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setOpaque(true);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    btn.setAlignmentX(Component.LEFT_ALIGNMENT);

    btn.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            // Aktif buton değilse hover efekti uygula
            if (btn != aktifButon) {
                btn.setBackground(getSidebarHover());
                btn.setForeground(Color.WHITE);
            }
        }
        public void mouseExited(MouseEvent e) {
            // Aktif buton değilse normal renge dön
            if (btn != aktifButon) {
                btn.setBackground(getSidebarBg());// lacivert yaptık
                btn.setForeground(new Color(226, 232, 240));
            }
        }
    });
    return btn;
}

// --- AKSİYON (PRIMARY) BUTONU ---
private static JButton aksiyonButonuOlustur(String metin) {
    JButton btn = new JButton(metin);
    btn.setForeground(Color.WHITE);
    btn.setBackground(getAccent());
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setOpaque(true);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

    btn.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) { btn.setBackground(getAccentHover()); }
        public void mouseExited(MouseEvent e)  { btn.setBackground(getAccent()); }
    });
    return btn;
}

private static JLabel etiketOlustur(String metin) {
    JLabel lbl = new JLabel(metin);
    lbl.setForeground(getTextColor());
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
    return lbl;
}

private static JTextField sekilliTextField() {
    JTextField txt = new JTextField();
    txt.setFont(FONT_REGULAR);
    txt.setBackground(getCardBg());
    txt.setForeground(getTextColor());
    txt.setCaretColor(getTextColor());
    txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    return txt;
}

private static JComboBox<String> sekilliComboBox() {
    JComboBox<String> cmb = new JComboBox<>();
    cmb.setFont(FONT_REGULAR);
    cmb.setBackground(getCardBg());
    cmb.setForeground(getTextColor());
    cmb.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1, true));
    return cmb;
}

private static JSpinner sekilliTarihSecici() {
    SpinnerDateModel model = new SpinnerDateModel();
    JSpinner spinner = new JSpinner(model);
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
    spinner.setEditor(editor);
    spinner.setFont(FONT_REGULAR);
    editor.getTextField().setBackground(getCardBg());
    editor.getTextField().setForeground(getTextColor());
    spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
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
    panel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);

    JLabel baslik = new JLabel("Genel Bakış");
    baslik.setFont(FONT_TITLE);
    baslik.setForeground(getTextColor());

    JLabel altBaslik = new JLabel("Otelinizin güncel durumu");
    altBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    altBaslik.setForeground(getTextSoft());

    JPanel headerSol = new JPanel();
    headerSol.setLayout(new BoxLayout(headerSol, BoxLayout.Y_AXIS));
    headerSol.setOpaque(false);
    headerSol.add(baslik);
    headerSol.add(Box.createVerticalStrut(4));
    headerSol.add(altBaslik);

    headerPanel.add(headerSol, BorderLayout.WEST);
    panel.add(headerPanel, BorderLayout.NORTH);

    JPanel pnlKartlar = new JPanel(new GridLayout(2, 2, 20, 20));
    pnlKartlar.setOpaque(false);
    pnlKartlar.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

    int toplamMus = musteriler.size();
    int bosOda = 0, doluOda = 0;
    for (Oda o : odalar.values()) {
        if (o.getOdaMusait()) bosOda++; else doluOda++;
    }
    double toplamCiro = 0;
    for (Rezervasyon r : rezervasyonlar.values()) {
        toplamCiro += r.getToplamUcret();
    }

    pnlKartlar.add(kartOlustur("Toplam Müşteri", String.valueOf(toplamMus),
            "Kayıtlı müşteri sayısı", new Color(59, 130, 246), "\uD83D\uDC64"));
    pnlKartlar.add(kartOlustur("Müsait Odalar", String.valueOf(bosOda),
            "Rezerve edilebilir", new Color(34, 197, 94), "\u2705"));
    pnlKartlar.add(kartOlustur("Dolu Odalar", String.valueOf(doluOda),
            "Aktif konaklama", new Color(239, 68, 68), "\uD83D\uDD12"));
    pnlKartlar.add(kartOlustur("Toplam Ciro", String.format("%.2f \u20BA", toplamCiro),
            "Aktif rezervasyonlardan", new Color(168, 85, 247), "\uD83D\uDCB0"));

    panel.add(pnlKartlar, BorderLayout.CENTER);
    return panel;
}

// --- MODERN İSTATİSTİK KARTI ---
private static JPanel kartOlustur(String baslik, String deger, String aciklama, Color vurguRengi, String ikon) {
    JPanel kart = new JPanel(new BorderLayout());
    kart.setBackground(getCardBg());
    kart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(22, 24, 22, 24)));

    // Üst: ikon + başlık
    JPanel ust = new JPanel(new BorderLayout());
    ust.setOpaque(false);

    JLabel lblIkon = new JLabel(ikon);
    lblIkon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
    lblIkon.setOpaque(true);
    lblIkon.setBackground(new Color(vurguRengi.getRed(), vurguRengi.getGreen(), vurguRengi.getBlue(), 30));
    lblIkon.setHorizontalAlignment(SwingConstants.CENTER);
    lblIkon.setPreferredSize(new Dimension(44, 44));
    lblIkon.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

    JLabel lblBaslik = new JLabel(baslik);
    lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblBaslik.setForeground(getTextSoft());
    lblBaslik.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

    ust.add(lblIkon, BorderLayout.WEST);
    ust.add(lblBaslik, BorderLayout.CENTER);

    // Orta: büyük rakam
    JLabel lblDeger = new JLabel(deger);
    lblDeger.setFont(FONT_BIG);
    lblDeger.setForeground(getTextColor());
    lblDeger.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

    // Alt: açıklama
    JLabel lblAciklama = new JLabel(aciklama);
    lblAciklama.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblAciklama.setForeground(getTextSoft());

    JPanel orta = new JPanel();
    orta.setLayout(new BoxLayout(orta, BoxLayout.Y_AXIS));
    orta.setOpaque(false);
    orta.add(lblDeger);
    orta.add(lblAciklama);

    kart.add(ust, BorderLayout.NORTH);
    kart.add(orta, BorderLayout.CENTER);
    return kart;
}

private static JPanel ekranOdaPlani() {
    JPanel panel = new JPanel(new BorderLayout(10, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

    JLabel baslik = new JLabel("Oda Planı");
    baslik.setFont(FONT_TITLE);
    baslik.setForeground(getTextColor());

    JLabel altBaslik = new JLabel("Tüm odaların gerçek zamanlı durumu");
    altBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    altBaslik.setForeground(getTextSoft());

    JPanel headerSol = new JPanel();
    headerSol.setLayout(new BoxLayout(headerSol, BoxLayout.Y_AXIS));
    headerSol.setOpaque(false);
    headerSol.add(baslik);
    headerSol.add(Box.createVerticalStrut(4));
    headerSol.add(altBaslik);

    // Lejant
    JPanel lejant = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
    lejant.setOpaque(false);
    lejant.add(lejantKutusu(new Color(34, 197, 94), "Müsait"));
    lejant.add(lejantKutusu(new Color(239, 68, 68), "Dolu"));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(headerSol, BorderLayout.WEST);
    header.add(lejant, BorderLayout.EAST);
    header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    panel.add(header, BorderLayout.NORTH);

    JPanel pnlOdalar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
    pnlOdalar.setOpaque(false);

    if (odalar.isEmpty()) {
        JLabel bos = new JLabel("Sistemde henüz kayıtlı oda bulunmuyor.");
        bos.setForeground(getTextSoft());
        bos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlOdalar.add(bos);
    } else {
        for (Oda o : odalar.values()) {
            pnlOdalar.add(odaKartiOlustur(o));
        }
    }

    JScrollPane scroll = new JScrollPane(pnlOdalar);
    scroll.setBorder(null);
    scroll.getViewport().setOpaque(false);
    scroll.setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
}

private static JPanel lejantKutusu(Color renk, String metin) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    p.setOpaque(false);
    JLabel kutu = new JLabel();
    kutu.setOpaque(true);
    kutu.setBackground(renk);
    kutu.setPreferredSize(new Dimension(12, 12));
    JLabel lbl = new JLabel(metin);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lbl.setForeground(getTextSoft());
    p.add(kutu);
    p.add(lbl);
    return p;
}

private static JPanel odaKartiOlustur(Oda o) {
    boolean musait = o.getOdaMusait();
    Color durumRengi = musait ? new Color(34, 197, 94) : new Color(239, 68, 68);

    JPanel kart = new JPanel(new BorderLayout());
    kart.setPreferredSize(new Dimension(160, 130));
    kart.setBackground(getCardBg());
    kart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    kart.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Üst renkli şerit
    JPanel serit = new JPanel();
    serit.setBackground(durumRengi);
    serit.setPreferredSize(new Dimension(0, 5));
    kart.add(serit, BorderLayout.NORTH);

    // İçerik
    JPanel icerik = new JPanel();
    icerik.setLayout(new BoxLayout(icerik, BoxLayout.Y_AXIS));
    icerik.setOpaque(false);
    icerik.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

    JLabel lblNo = new JLabel("Oda " + o.getOdaNo());
    lblNo.setFont(new Font("Segoe UI", Font.BOLD, 20));
    lblNo.setForeground(getTextColor());

    JLabel lblTip = new JLabel(o.getTip());
    lblTip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblTip.setForeground(getTextSoft());

    JLabel lblDurum = new JLabel(musait ? "Müsait" : "Dolu");
    lblDurum.setFont(new Font("Segoe UI", Font.BOLD, 11));
    lblDurum.setForeground(durumRengi);
    lblDurum.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

    JLabel lblFiyat = new JLabel(String.format("%.0f \u20BA / gece", o.getOdaFiyat()));
    lblFiyat.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblFiyat.setForeground(getTextColor());
    lblFiyat.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

    icerik.add(lblNo);
    icerik.add(lblTip);
    icerik.add(lblDurum);
    icerik.add(lblFiyat);

    kart.add(icerik, BorderLayout.CENTER);

    kart.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            odaDetayGoster(o);
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            kart.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(getAccent(), 2, true),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        }
        @Override
        public void mouseExited(MouseEvent e) {
            kart.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(getBorderColor(), 1, true),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        }
    });

    return kart;
}

private static void odaDetayGoster(Oda o) {
    StringBuilder sb = new StringBuilder();
    sb.append("ODA BİLGİLERİ\n");
    sb.append("─────────────────────────────\n");
    sb.append("Oda Numarası  : ").append(o.getOdaNo()).append("\n");
    sb.append("Oda Tipi      : ").append(o.getTip()).append("\n");
    sb.append("Kapasite      : ").append(o.getKapasite()).append(" Kişi\n");
    sb.append("Gecelik Fiyat : ").append(o.getOdaFiyat()).append(" ₺\n");
    sb.append("Açıklama      : ").append(o.getAciklama().isEmpty() ? "Yok" : o.getAciklama()).append("\n\n");

    if (o.getOdaMusait()) {
        sb.append("DURUM: MÜSAİT\n");
        sb.append("Bu oda şu an boş ve rezerve edilmeye hazır.");
        JOptionPane.showMessageDialog(frame, sb.toString(),
                "Oda " + o.getOdaNo(), JOptionPane.INFORMATION_MESSAGE);
    } else {
        sb.append("DURUM: DOLU\n\n");
        sb.append("KONAKLAYAN MÜŞTERİ\n");
        sb.append("─────────────────────────────\n");
        for (Rezervasyon r : rezervasyonlar.values()) {
            if (r.getOda().getOdaNo() == o.getOdaNo() && r.getDurum().equals("Aktif")) {
                sb.append("Adı Soyadı    : ").append(r.getMusteri().getMusteriAdSoyad()).append("\n");
                sb.append("Telefon       : ").append(r.getMusteri().getMusteriTelefon()).append("\n");
                sb.append("Giriş Tarihi  : ").append(r.getGirisTarihi()).append("\n");
                sb.append("Çıkış Tarihi  : ").append(r.getCikisTarihi()).append("\n");
                sb.append("Toplam Tutar  : ").append(r.getToplamUcret()).append(" ₺\n");
                break;
            }
        }
        JOptionPane.showMessageDialog(frame, sb.toString(),
                "Oda " + o.getOdaNo(), JOptionPane.WARNING_MESSAGE);
    }
}

// --- FORM PANELİ YARDIMCISI ---
private static JPanel formPaneliOlustur(String baslikMetin, String altBaslikMetin, JPanel form, JButton kaydetBtn) {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

    JLabel baslik = new JLabel(baslikMetin);
    baslik.setFont(FONT_TITLE);
    baslik.setForeground(getTextColor());

    JLabel altBaslik = new JLabel(altBaslikMetin);
    altBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    altBaslik.setForeground(getTextSoft());

    JPanel header = new JPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
    header.setOpaque(false);
    header.add(baslik);
    header.add(Box.createVerticalStrut(4));
    header.add(altBaslik);
    header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

    // Form kart içinde
    JPanel kart = new JPanel(new BorderLayout());
    kart.setBackground(getCardBg());
    kart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(25, 28, 25, 28)));

    form.setOpaque(false);
    kart.add(form, BorderLayout.CENTER);

    if (kaydetBtn != null) {
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrap.setOpaque(false);
        btnWrap.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        btnWrap.add(kaydetBtn);
        kart.add(btnWrap, BorderLayout.SOUTH);
    }

    panel.add(header, BorderLayout.NORTH);
    panel.add(kart, BorderLayout.CENTER);
    return panel;
}

private static JPanel ekranOdaEkle() {
    JPanel grid = new JPanel(new GridLayout(6, 2, 15, 15));

    JTextField txtOdaNo = sekilliTextField();
    txtOdaNo.setEditable(false);
    int siradakiOdaNo = 101;
    for (int key : odalar.keySet()) if (key >= siradakiOdaNo) siradakiOdaNo = key + 1;
    txtOdaNo.setText(String.valueOf(siradakiOdaNo));

    JTextField txtTip = sekilliTextField();
    JTextField txtKapasite = sekilliTextField(); rakamVeLimitKoy(txtKapasite, 3);
    JTextField txtFiyat = sekilliTextField();

    JCheckBox chkMusait = new JCheckBox("Oda kullanıma uygun", true);
    chkMusait.setOpaque(false);
    chkMusait.setForeground(getTextColor());
    chkMusait.setFont(FONT_REGULAR);

    JTextField txtAciklama = sekilliTextField();

    grid.add(etiketOlustur("Oda Numarası")); grid.add(txtOdaNo);
    grid.add(etiketOlustur("Oda Tipi")); grid.add(txtTip);
    grid.add(etiketOlustur("Kapasite (kişi)")); grid.add(txtKapasite);
    grid.add(etiketOlustur("Gecelik Fiyat (₺)")); grid.add(txtFiyat);
    grid.add(etiketOlustur("Mevcut Durum")); grid.add(chkMusait);
    grid.add(etiketOlustur("Açıklama")); grid.add(txtAciklama);

    JButton btnKaydet = aksiyonButonuOlustur("Odayı Kaydet");
    btnKaydet.addActionListener(e -> {
        try {
            int no = Integer.parseInt(txtOdaNo.getText());
            int kap = Integer.parseInt(txtKapasite.getText());
            double fiyat = Double.parseDouble(txtFiyat.getText());

            Oda oda = new Oda(no, txtTip.getText(), kap, fiyat, chkMusait.isSelected(), txtAciklama.getText());
            odalar.put(no, oda);

            JOptionPane.showMessageDialog(frame,
                    "Oda başarıyla eklendi!\n" + oda.toString(),
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            txtTip.setText(""); txtKapasite.setText(""); txtFiyat.setText("");
            txtAciklama.setText(""); txtOdaNo.setText(String.valueOf(no + 1));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Lütfen sayısal alanları eksiksiz giriniz!",
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    });

    return formPaneliOlustur("Yeni Oda Ekle",
            "Otelinize yeni bir oda kaydı ekleyin", grid, btnKaydet);
}

private static JPanel ekranMusteriEkle() {
    JPanel grid = new JPanel(new GridLayout(6, 2, 15, 15));

    JTextField txtId = sekilliTextField();
    txtId.setEditable(false);
    int siradakiMusNo = 1;
    for (int key : musteriler.keySet()) if (key >= siradakiMusNo) siradakiMusNo = key + 1;
    txtId.setText(String.valueOf(siradakiMusNo));

    JTextField txtAd = sekilliTextField();
    JTextField txtTel = sekilliTextField(); rakamVeLimitKoy(txtTel, 11);
    JTextField txtEmail = sekilliTextField();
    JTextField txtTc = sekilliTextField(); rakamVeLimitKoy(txtTc, 11);
    JTextField txtAdres = sekilliTextField();

    grid.add(etiketOlustur("Müşteri No")); grid.add(txtId);
    grid.add(etiketOlustur("Adı Soyadı")); grid.add(txtAd);
    grid.add(etiketOlustur("Telefon")); grid.add(txtTel);
    grid.add(etiketOlustur("E-Posta")); grid.add(txtEmail);
    grid.add(etiketOlustur("TC Kimlik No")); grid.add(txtTc);
    grid.add(etiketOlustur("Adres")); grid.add(txtAdres);

    JButton btnKaydet = aksiyonButonuOlustur("Müşteriyi Kaydet");
    btnKaydet.addActionListener(e -> {
        try {
            if (txtTc.getText().length() != 11) {
                JOptionPane.showMessageDialog(frame,
                        "TC Kimlik tam 11 haneli olmalıdır!",
                        "Eksik Giriş", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = Integer.parseInt(txtId.getText());
            Musteri m = new Musteri(id, txtAd.getText(), txtTel.getText(),
                    txtEmail.getText(), txtTc.getText(), txtAdres.getText());
            musteriler.put(id, m);

            JOptionPane.showMessageDialog(frame,
                    "Müşteri sisteme eklendi:\n" + m.toString(),
                    "Kayıt Başarılı", JOptionPane.INFORMATION_MESSAGE);
            txtAd.setText(""); txtTel.setText(""); txtEmail.setText("");
            txtTc.setText(""); txtAdres.setText(""); txtId.setText(String.valueOf(id + 1));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Lütfen form alanlarını eksiksiz ve doğru formatta giriniz",
                    "Hatalı Giriş", JOptionPane.ERROR_MESSAGE);
        }
    });

    return formPaneliOlustur("Yeni Müşteri Ekle",
            "Sisteme yeni bir müşteri kaydı oluşturun", grid, btnKaydet);
}

private static JPanel ekranGuncelle() {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

    JLabel baslik = new JLabel("Kayıt Güncelle");
    baslik.setFont(FONT_TITLE);
    baslik.setForeground(getTextColor());

    JLabel altBaslik = new JLabel("Müşteri ve oda bilgilerini düzenleyin");
    altBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    altBaslik.setForeground(getTextSoft());

    JPanel header = new JPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
    header.setOpaque(false);
    header.add(baslik);
    header.add(Box.createVerticalStrut(4));
    header.add(altBaslik);
    header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    panel.add(header, BorderLayout.NORTH);

    JTabbedPane sekmeler = new JTabbedPane();
    sekmeler.setFont(new Font("Segoe UI", Font.BOLD, 13));
    sekmeler.setBackground(getCardBg());

    // ----- Müşteri sekmesi -----
    JPanel pnlMusteri = new JPanel(new BorderLayout(10, 10));
    pnlMusteri.setBackground(getCardBg());
    pnlMusteri.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

    JComboBox<String> cmbMusteri = sekilliComboBox();
    cmbMusteri.addItem("Lütfen güncellenecek müşteriyi seçin...");
    for (Musteri m : musteriler.values())
        cmbMusteri.addItem(m.getMusteriId() + " - " + m.getMusteriAdSoyad());

    JPanel gridMus = new JPanel(new GridLayout(5, 2, 12, 12));
    gridMus.setOpaque(false);
    JTextField gTxtAd = sekilliTextField();
    JTextField gTxtTel = sekilliTextField(); rakamVeLimitKoy(gTxtTel, 11);
    JTextField gTxtEmail = sekilliTextField();
    JTextField gTxtTc = sekilliTextField(); rakamVeLimitKoy(gTxtTc, 11);
    JTextField gTxtAdres = sekilliTextField();

    gridMus.add(etiketOlustur("Adı Soyadı")); gridMus.add(gTxtAd);
    gridMus.add(etiketOlustur("Telefon")); gridMus.add(gTxtTel);
    gridMus.add(etiketOlustur("E-Posta")); gridMus.add(gTxtEmail);
    gridMus.add(etiketOlustur("TC Kimlik")); gridMus.add(gTxtTc);
    gridMus.add(etiketOlustur("Adres")); gridMus.add(gTxtAdres);

    cmbMusteri.addActionListener(e -> {
        if (cmbMusteri.getSelectedIndex() > 0) {
            int mId = Integer.parseInt(cmbMusteri.getSelectedItem().toString().split(" - ")[0]);
            Musteri seciliM = musteriler.get(mId);
            gTxtAd.setText(seciliM.getMusteriAdSoyad());
            gTxtTel.setText(seciliM.getMusteriTelefon());
            gTxtEmail.setText(seciliM.getMusteriEmail());
            gTxtTc.setText(seciliM.getMusteriTcKimlikNo());
            gTxtAdres.setText(seciliM.getMusteriAdres());
        }
    });

    JButton btnMusGuncelle = aksiyonButonuOlustur("Müşteriyi Güncelle");
    btnMusGuncelle.addActionListener(e -> {
        if (cmbMusteri.getSelectedIndex() > 0) {
            int mId = Integer.parseInt(cmbMusteri.getSelectedItem().toString().split(" - ")[0]);
            Musteri m = musteriler.get(mId);
            m.setMusteriAdSoyad(gTxtAd.getText());
            m.setMusteriTelefon(gTxtTel.getText());
            m.setMusteriEmail(gTxtEmail.getText());
            m.setMusteriTcKimlikNo(gTxtTc.getText());
            m.setMusteriAdres(gTxtAdres.getText());
            JOptionPane.showMessageDialog(frame,
                    "Müşteri başarıyla güncellendi!",
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    });

    JPanel musBtnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    musBtnWrap.setOpaque(false);
    musBtnWrap.add(btnMusGuncelle);

    pnlMusteri.add(cmbMusteri, BorderLayout.NORTH);
    pnlMusteri.add(gridMus, BorderLayout.CENTER);
    pnlMusteri.add(musBtnWrap, BorderLayout.SOUTH);

    // ----- Oda sekmesi -----
    JPanel pnlOda = new JPanel(new BorderLayout(10, 10));
    pnlOda.setBackground(getCardBg());
    pnlOda.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

    JComboBox<String> cmbOda = sekilliComboBox();
    cmbOda.addItem("Lütfen güncellenecek odayı seçin...");
    for (Oda o : odalar.values()) cmbOda.addItem(o.getOdaNo() + " - " + o.getTip());

    JPanel gridOda = new JPanel(new GridLayout(5, 2, 12, 12));
    gridOda.setOpaque(false);
    JTextField gTxtTip = sekilliTextField();
    JTextField gTxtKap = sekilliTextField(); rakamVeLimitKoy(gTxtKap, 3);
    JTextField gTxtFiyat = sekilliTextField();
    JCheckBox gChkMusait = new JCheckBox("Oda müsait");
    gChkMusait.setOpaque(false);
    gChkMusait.setForeground(getTextColor());
    gChkMusait.setFont(FONT_REGULAR);
    JTextField gTxtAciklama = sekilliTextField();

    gridOda.add(etiketOlustur("Oda Tipi")); gridOda.add(gTxtTip);
    gridOda.add(etiketOlustur("Kapasite")); gridOda.add(gTxtKap);
    gridOda.add(etiketOlustur("Fiyat (₺)")); gridOda.add(gTxtFiyat);
    gridOda.add(etiketOlustur("Durum")); gridOda.add(gChkMusait);
    gridOda.add(etiketOlustur("Açıklama")); gridOda.add(gTxtAciklama);

    cmbOda.addActionListener(e -> {
        if (cmbOda.getSelectedIndex() > 0) {
            int oId = Integer.parseInt(cmbOda.getSelectedItem().toString().split(" - ")[0]);
            Oda seciliO = odalar.get(oId);
            gTxtTip.setText(seciliO.getTip());
            gTxtKap.setText(String.valueOf(seciliO.getKapasite()));
            gTxtFiyat.setText(String.valueOf(seciliO.getOdaFiyat()));
            gChkMusait.setSelected(seciliO.getOdaMusait());
            gTxtAciklama.setText(seciliO.getAciklama());
        }
    });

    JButton btnOdaGuncelle = aksiyonButonuOlustur("Odayı Güncelle");
    btnOdaGuncelle.addActionListener(e -> {
        if (cmbOda.getSelectedIndex() > 0) {
            int oId = Integer.parseInt(cmbOda.getSelectedItem().toString().split(" - ")[0]);
            Oda o = odalar.get(oId);
            o.setTip(gTxtTip.getText());
            o.setKapasite(Integer.parseInt(gTxtKap.getText()));
            o.setOdaFiyat(Double.parseDouble(gTxtFiyat.getText()));
            o.setOdaMusait(gChkMusait.isSelected());
            o.setAciklama(gTxtAciklama.getText());
            JOptionPane.showMessageDialog(frame,
                    "Oda başarıyla güncellendi!",
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    });

    JPanel odaBtnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    odaBtnWrap.setOpaque(false);
    odaBtnWrap.add(btnOdaGuncelle);

    pnlOda.add(cmbOda, BorderLayout.NORTH);
    pnlOda.add(gridOda, BorderLayout.CENTER);
    pnlOda.add(odaBtnWrap, BorderLayout.SOUTH);

    sekmeler.addTab("  Müşteri Düzenle  ", pnlMusteri);
    sekmeler.addTab("  Oda Düzenle  ", pnlOda);
    panel.add(sekmeler, BorderLayout.CENTER);
    return panel;
}

private static JPanel ekranRezervasyonYap() {
    JPanel grid = new JPanel(new GridLayout(5, 2, 15, 15));

    JTextField txtRezNo = sekilliTextField();
    txtRezNo.setEditable(false);
    int siradakiNo = 1000;
    for (int key : rezervasyonlar.keySet()) if (key >= siradakiNo) siradakiNo = key + 1;
    txtRezNo.setText(String.valueOf(siradakiNo));

    JComboBox<String> cmbMusteri = sekilliComboBox();
    if (musteriler.isEmpty()) {
        cmbMusteri.addItem("Kayıtlı müşteri yok!");
        cmbMusteri.setEnabled(false);
    } else {
        for (Musteri m : musteriler.values())
            cmbMusteri.addItem(m.getMusteriId() + " - " + m.getMusteriAdSoyad());
    }

    JComboBox<String> cmbOda = sekilliComboBox();
    if (odalar.isEmpty()) {
        cmbOda.addItem("Kayıtlı oda yok!");
        cmbOda.setEnabled(false);
    } else {
        for (Oda o : odalar.values())
            cmbOda.addItem(o.getOdaNo() + " - " + o.getTip() + " (" + o.getOdaFiyat() + " ₺)");
    }

    JSpinner spinGiris = sekilliTarihSecici();
    JSpinner spinCikis = sekilliTarihSecici();

    grid.add(etiketOlustur("Rezervasyon No")); grid.add(txtRezNo);
    grid.add(etiketOlustur("Müşteri")); grid.add(cmbMusteri);
    grid.add(etiketOlustur("Oda")); grid.add(cmbOda);
    grid.add(etiketOlustur("Giriş Tarihi")); grid.add(spinGiris);
    grid.add(etiketOlustur("Çıkış Tarihi")); grid.add(spinCikis);

    JButton btnKaydet = aksiyonButonuOlustur("Rezervasyonu Tamamla");
    btnKaydet.addActionListener(e -> {
        try {
            if (musteriler.isEmpty() || odalar.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Rezervasyon için en az 1 müşteri ve oda gereklidir!",
                        "Eksik Veri", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int rNo = Integer.parseInt(txtRezNo.getText());
            int mNo = Integer.parseInt(((String) cmbMusteri.getSelectedItem()).split(" - ")[0]);
            int oNo = Integer.parseInt(((String) cmbOda.getSelectedItem()).split(" - ")[0]);
            LocalDate giris = ((Date) spinGiris.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate cikis = ((Date) spinCikis.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (cikis.isBefore(giris) || cikis.isEqual(giris)) {
                JOptionPane.showMessageDialog(frame,
                        "Çıkış tarihi girişten sonra olmalıdır!",
                        "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Musteri m = musteriler.get(mNo);
            Oda o = odalar.get(oNo);
            IntervalDugumu cakisan = intervalAgaci.cakismaKontrol(oNo,giris, cikis);
            boolean uygun = (cakisan == null);
            double toplamUcret = o.getOdaFiyat() * (cikis.toEpochDay() - giris.toEpochDay());

            if (!uygun) {
                BeklemeListe liste = beklemeListeleri.get(oNo);
                if (liste == null) {
                    liste = new BeklemeListe();
                    beklemeListeleri.put(oNo, liste);
                }
                liste.ekle(rNo, m, oNo, giris, cikis);
                JOptionPane.showMessageDialog(frame,
                        "Tarih çakışması! Rezervasyon bekleme listesine alındı.",
                        "Bekleme Listesi", JOptionPane.WARNING_MESSAGE);
            } else {
                Rezervasyon r = new Rezervasyon(rNo, m, o, giris, cikis, toplamUcret, "Aktif");
                rezervasyonlar.put(rNo, r);
                intervalAgaci.ekle(oNo, giris, cikis);
                o.setOdaMusait(false);

                JOptionPane.showMessageDialog(frame,
                        "Rezervasyon başarıyla yapıldı!\nToplam Tutar: " + toplamUcret + " ₺",
                        "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                txtRezNo.setText(String.valueOf(rNo + 1));

                int faturaCevap = JOptionPane.showConfirmDialog(frame,
                        "Müşteri için fatura/fiş oluşturulsun mu?",
                        "Fatura İşlemi", JOptionPane.YES_NO_OPTION);
                if (faturaCevap == JOptionPane.YES_OPTION) faturaYazdir(r);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Hatalı işlem!",
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    });

    return formPaneliOlustur("Yeni Rezervasyon",
            "Müşteri için yeni bir konaklama oluşturun", grid, btnKaydet);
}

// --- TÜRKÇE KARAKTERLİ FATURA YAZDIRMA ---
private static void faturaYazdir(Rezervasyon r) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Faturayı Kaydet");
    fileChooser.setSelectedFile(new File("Fatura_" + r.getRezervasyonNo() + ".txt"));

    int kullaniciSecimi = fileChooser.showSaveDialog(frame);

    if (kullaniciSecimi == JFileChooser.APPROVE_OPTION) {
        File faturaDosyasi = fileChooser.getSelectedFile();

        // UTF-8 ile yazıyoruz, ayrıca BOM ekliyoruz ki Notepad doğru açsın
        try (BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(faturaDosyasi), StandardCharsets.UTF_8))) {

            // UTF-8 BOM
            out.write('\uFEFF');

            out.write("=========================================\n");
            out.write("           E-OTEL REZERVASYON            \n");
            out.write("                FATURASI                 \n");
            out.write("=========================================\n");
            out.write("İşlem Tarihi   : " + LocalDate.now() + "\n");
            out.write("Rezervasyon No : " + r.getRezervasyonNo() + "\n");
            out.write("-----------------------------------------\n");
            out.write("MÜŞTERİ BİLGİLERİ:\n");
            out.write("Ad Soyad       : " + r.getMusteri().getMusteriAdSoyad() + "\n");
            out.write("TC Kimlik      : " + r.getMusteri().getMusteriTcKimlikNo() + "\n");
            out.write("-----------------------------------------\n");
            out.write("KONAKLAMA BİLGİLERİ:\n");
            out.write("Oda Numarası   : " + r.getOda().getOdaNo() + "\n");
            out.write("Oda Tipi       : " + r.getOda().getTip() + "\n");
            out.write("Giriş Tarihi   : " + r.getGirisTarihi() + "\n");
            out.write("Çıkış Tarihi   : " + r.getCikisTarihi() + "\n");
            long geceSayisi = r.getCikisTarihi().toEpochDay() - r.getGirisTarihi().toEpochDay();
            out.write("Gece Sayısı    : " + geceSayisi + " Gece\n");
            out.write("-----------------------------------------\n");
            out.write("TOPLAM TUTAR   : " + r.getToplamUcret() + " ₺\n");
            out.write("=========================================\n");
            out.write("Bizi tercih ettiğiniz için teşekkür ederiz.\n");

            JOptionPane.showMessageDialog(frame,
                    "Fatura başarıyla kaydedildi!\nKonum: " + faturaDosyasi.getAbsolutePath(),
                    "Fatura Hazır", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Fatura oluşturulurken hata: " + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private static JPanel ekranListele() {
    JPanel panel = new JPanel(new BorderLayout(10, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

    JLabel baslik = new JLabel("Sistem Kayıtları");
    baslik.setFont(FONT_TITLE);
    baslik.setForeground(getTextColor());

    JLabel altBaslik = new JLabel("Tüm sistem verilerini görüntüleyin ve filtreleyin");
    altBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    altBaslik.setForeground(getTextSoft());

    JPanel headerSol = new JPanel();
    headerSol.setLayout(new BoxLayout(headerSol, BoxLayout.Y_AXIS));
    headerSol.setOpaque(false);
    headerSol.add(baslik);
    headerSol.add(Box.createVerticalStrut(4));
    headerSol.add(altBaslik);

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(headerSol, BorderLayout.WEST);
    header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    panel.add(header, BorderLayout.NORTH);

    // Üst kontrol paneli
    JPanel ustPanel = new JPanel(new BorderLayout(10, 10));
    ustPanel.setOpaque(false);

    JPanel butonlar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    butonlar.setOpaque(false);
    JButton btnOdalar = sekmeButonuOlustur("Odalar");
    JButton btnMusteriler = sekmeButonuOlustur("Müşteriler");
    JButton btnRez = sekmeButonuOlustur("Aktif Rezervasyonlar");
    JButton btnGecmis = sekmeButonuOlustur("Tamamlananlar");
    butonlar.add(btnOdalar);
    butonlar.add(btnMusteriler);
    butonlar.add(btnRez);
    butonlar.add(btnGecmis);

    JPanel aramaPaneli = new JPanel(new BorderLayout(8, 0));
    aramaPaneli.setOpaque(false);
    aramaPaneli.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    JLabel lblAra = new JLabel("\uD83D\uDD0D");
    lblAra.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
    JTextField txtArama = sekilliTextField();
    aramaPaneli.add(lblAra, BorderLayout.WEST);
    aramaPaneli.add(txtArama, BorderLayout.CENTER);

    ustPanel.add(butonlar, BorderLayout.NORTH);
    ustPanel.add(aramaPaneli, BorderLayout.SOUTH);

    // Tablo
    DefaultTableModel tabloModel = new DefaultTableModel() {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable tablo = new JTable(tabloModel);
    tablo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tablo.setRowHeight(32);
    tablo.setBackground(getCardBg());
    tablo.setForeground(getTextColor());
    tablo.setShowGrid(false);
    tablo.setIntercellSpacing(new Dimension(0, 0));
    tablo.setSelectionBackground(getAccent());
    tablo.setSelectionForeground(Color.WHITE);

    // Alternatif satır rengi
    tablo.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                        boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if (!sel) {
                c.setBackground(row % 2 == 0 ? getCardBg() :
                        (new Color(248, 250, 252)));
                c.setForeground(getTextColor());
            }
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return c;
        }
    });

    JTableHeader thead = tablo.getTableHeader();
    thead.setFont(new Font("Segoe UI", Font.BOLD, 13));
    thead.setBackground(new Color(241, 245, 249));
    thead.setForeground(getTextColor());
    thead.setPreferredSize(new Dimension(thead.getPreferredSize().width, 38));
    thead.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getBorderColor()));
    ((DefaultTableCellRenderer) thead.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

    JScrollPane scrollPane = new JScrollPane(tablo);
    scrollPane.setBorder(BorderFactory.createLineBorder(getBorderColor(), 1, true));
    scrollPane.getViewport().setBackground(getCardBg());

    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tabloModel);
    tablo.setRowSorter(sorter);

    txtArama.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) { ara(); }
        public void removeUpdate(DocumentEvent e) { ara(); }
        public void changedUpdate(DocumentEvent e) { ara(); }
        private void ara() {
            String text = txtArama.getText();
            if (text.trim().length() == 0) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    });

    btnOdalar.addActionListener(e -> {
        tabloModel.setRowCount(0);
        tabloModel.setColumnIdentifiers(new String[]{"Oda No", "Tip", "Kapasite", "Fiyat", "Durum", "Açıklama"});
        for (Oda o : odalar.values())
            tabloModel.addRow(new Object[]{o.getOdaNo(), o.getTip(), o.getKapasite(),
                    o.getOdaFiyat() + " ₺", o.getOdaMusait() ? "Boş" : "Dolu", o.getAciklama()});
        txtArama.setText("");
    });

    btnMusteriler.addActionListener(e -> {
        tabloModel.setRowCount(0);
        tabloModel.setColumnIdentifiers(new String[]{"Müşteri No", "Ad Soyad", "Telefon", "E-Posta", "TC Kimlik", "Adres"});
        for (Musteri m : musteriler.values())
            tabloModel.addRow(new Object[]{m.getMusteriId(), m.getMusteriAdSoyad(), m.getMusteriTelefon(),
                    m.getMusteriEmail(), m.getMusteriTcKimlikNo(), m.getMusteriAdres()});
        txtArama.setText("");
    });

    btnRez.addActionListener(e -> {
        tabloModel.setRowCount(0);
        tabloModel.setColumnIdentifiers(new String[]{"Rez No", "Müşteri", "Oda", "Giriş", "Çıkış", "Tutar", "Durum"});
        for (Rezervasyon r : rezervasyonlar.values())
            tabloModel.addRow(new Object[]{r.getRezervasyonNo(), r.getMusteri().getMusteriAdSoyad(),
                    r.getOda().getOdaNo(), r.getGirisTarihi(), r.getCikisTarihi(),
                    r.getToplamUcret() + " ₺", r.getDurum()});
        txtArama.setText("");
    });

    btnGecmis.addActionListener(e -> {
        tabloModel.setRowCount(0);
        JOptionPane.showMessageDialog(frame,
                "Geçmiş rezervasyonlar BST yapısı gereği terminale yazdırılmıştır.",
                "Bilgi", JOptionPane.INFORMATION_MESSAGE);
        tamamlananRezervasyonlar.kronolojikListele();
    });

    JPanel orta = new JPanel(new BorderLayout(0, 12));
    orta.setOpaque(false);
    orta.add(ustPanel, BorderLayout.NORTH);
    orta.add(scrollPane, BorderLayout.CENTER);

    panel.add(orta, BorderLayout.CENTER);
    return panel;
}

// Üst sekme/filtre butonu
private static JButton sekmeButonuOlustur(String metin) {
    JButton btn = new JButton(metin);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setBackground(getCardBg());
    btn.setForeground(getTextColor());
    btn.setFocusPainted(false);
    btn.setOpaque(true);
    btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    btn.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            btn.setBackground(getAccent());
            btn.setForeground(Color.WHITE);
        }
        public void mouseExited(MouseEvent e) {
            btn.setBackground(getCardBg());
            btn.setForeground(getTextColor());
        }
    });
    return btn;
}

private static JPanel ekranRezervasyonIptal() {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

    JLabel baslik = new JLabel("Rezervasyon İptal");
    baslik.setFont(FONT_TITLE);
    baslik.setForeground(getTextColor());

    JLabel altBaslik = new JLabel("Aktif rezervasyonları iptal edin");
    altBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    altBaslik.setForeground(getTextSoft());

    JPanel header = new JPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
    header.setOpaque(false);
    header.add(baslik);
    header.add(Box.createVerticalStrut(4));
    header.add(altBaslik);
    header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    panel.add(header, BorderLayout.NORTH);

    JPanel kart = new JPanel(new BorderLayout(10, 10));
    kart.setBackground(getCardBg());
    kart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)));

    JPanel ustPanel = new JPanel(new BorderLayout(10, 0));
    ustPanel.setOpaque(false);
    JComboBox<String> cmbIptal = sekilliComboBox();
    JButton btnIptal = aksiyonButonuOlustur("İptal Et");
    btnIptal.setBackground(new Color(220, 38, 38));

    if (rezervasyonlar.isEmpty()) {
        cmbIptal.addItem("Aktif rezervasyon bulunmuyor!");
        cmbIptal.setEnabled(false);
        btnIptal.setEnabled(false);
    } else {
        for (Rezervasyon r : rezervasyonlar.values())
            cmbIptal.addItem(r.getRezervasyonNo() + " - " + r.getMusteri().getMusteriAdSoyad()
                    + " (Oda: " + r.getOda().getOdaNo() + ")");
    }

    ustPanel.add(cmbIptal, BorderLayout.CENTER);
    ustPanel.add(btnIptal, BorderLayout.EAST);

    JTextArea txtEkran = new JTextArea();
    txtEkran.setEditable(false);
    txtEkran.setFont(new Font("Consolas", Font.PLAIN, 13));
    txtEkran.setBackground(new Color(248, 250, 252));
    txtEkran.setForeground(getTextColor());
    txtEkran.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));
    JScrollPane scrollPane = new JScrollPane(txtEkran);
    scrollPane.setBorder(null);

    btnIptal.addActionListener(e -> {
        try {
            if (rezervasyonlar.isEmpty()) return;
            String secilen = (String) cmbIptal.getSelectedItem();
            int no = Integer.parseInt(secilen.split(" - ")[0]);
            Rezervasyon iptalRez = rezervasyonlar.get(no);

            int onay = JOptionPane.showConfirmDialog(frame,
                    no + " numaralı rezervasyonu iptal etmek istediğinize emin misiniz?",
                    "Onay", JOptionPane.YES_NO_OPTION);
            if (onay == JOptionPane.YES_OPTION) {
                Oda iptalOda = iptalRez.getOda();
                rezervasyonlar.remove(no);
                iptalRez.setDurum("İptal");
                tamamlananRezervasyonlar.ekle(iptalRez);
                intervalAgaci.sil(iptalOda.getOdaNo(), iptalRez.getGirisTarihi(), iptalRez.getCikisTarihi());

                StringBuilder sonucMesaji = new StringBuilder("> Rezervasyon İptal Edildi (No: " + no + ")\n");
                BeklemeListe liste = beklemeListeleri.get(iptalOda.getOdaNo());
                if (liste != null && !liste.bosMu()) {
                    BeklemeDugumu siradaki = liste.cikar();
                    double yeniUcret = iptalOda.getOdaFiyat()
                            * (siradaki.cikisTarihi.toEpochDay() - siradaki.girisTarihi.toEpochDay());
                    Rezervasyon yeniRez = new Rezervasyon(siradaki.rezervasyonNo, siradaki.musteri,
                            iptalOda, siradaki.girisTarihi, siradaki.cikisTarihi, yeniUcret, "Aktif");
                    rezervasyonlar.put(yeniRez.getRezervasyonNo(), yeniRez);
                    iptalOda.setOdaMusait(false);
                    sonucMesaji.append("\n> Bekleme listesinden yeni rezervasyon oluşturuldu!")
                            .append("\n  Müşteri: ").append(siradaki.musteri.getMusteriAdSoyad());
                } else {
                    iptalOda.setOdaMusait(true);
                    sonucMesaji.append("\n> Bekleme listesi boş. Oda müsait duruma getirildi.");
                }
                txtEkran.setText(sonucMesaji.toString());

                cmbIptal.removeAllItems();
                if (rezervasyonlar.isEmpty()) {
                    cmbIptal.addItem("Aktif rezervasyon yok!");
                    cmbIptal.setEnabled(false);
                    btnIptal.setEnabled(false);
                } else {
                    for (Rezervasyon r : rezervasyonlar.values())
                        cmbIptal.addItem(r.getRezervasyonNo() + " - " + r.getMusteri().getMusteriAdSoyad()
                                + " (Oda: " + r.getOda().getOdaNo() + ")");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "İptal sırasında hata oluştu!",
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    });

    kart.add(ustPanel, BorderLayout.NORTH);
    kart.add(scrollPane, BorderLayout.CENTER);

    panel.add(kart, BorderLayout.CENTER);
    return panel;
}
//  SÜRESİ GEÇMİŞ REZERVASYON KONTROLÜ
// Program açılınca çıkış tarihi bugünden önce olan aktif rezervasyonları
// otomatik olarak "Tamamlandı" yapar ve odayı müsaite döndürür. O(n)
private static void suresiGecmisRezervasyonlariTamamla() {
    LocalDate bugun = LocalDate.now();
    // ConcurrentModificationException'dan kaçınmak için ayrı listeye al
    java.util.List<Integer> tamamlanacaklar = new java.util.ArrayList<>();

    for (Rezervasyon r : rezervasyonlar.values()) {
        if (r.getDurum().equals("Aktif") && r.getCikisTarihi().isBefore(bugun)) {
            tamamlanacaklar.add(r.getRezervasyonNo());
        }
    }

    for (int no : tamamlanacaklar) {
        Rezervasyon r = rezervasyonlar.get(no);
        rezervasyonlar.remove(no);          // aktif listeden çıkar
        r.setDurum("Tamamlandı");
        tamamlananRezervasyonlar.ekle(r);   // BST'ye ekle

        // Odayı müsaite al (bekleme listesi varsa otomatik aktifleştir)
        Oda oda = r.getOda();
        BeklemeListe liste = beklemeListeleri.get(oda.getOdaNo());
        if (liste != null && !liste.bosMu()) {
            BeklemeDugumu siradaki = liste.cikar();
            double yeniUcret = oda.getOdaFiyat()
                    * (siradaki.cikisTarihi.toEpochDay() - siradaki.girisTarihi.toEpochDay());
            Rezervasyon yeniRez = new Rezervasyon(
                    siradaki.rezervasyonNo, siradaki.musteri, oda,
                    siradaki.girisTarihi, siradaki.cikisTarihi, yeniUcret, "Aktif");
            rezervasyonlar.put(yeniRez.getRezervasyonNo(), yeniRez);
            // Oda hâlâ dolu — bekleme listesinden biri aldı
        } else {
            oda.setOdaMusait(true); // kimse beklemiyorsa oda boşaldı
        }
    }

    if (!tamamlanacaklar.isEmpty()) {
        System.out.println(tamamlanacaklar.size()
                + " rezervasyon süresi dolduğu için otomatik tamamlandı.");
    }
}

// ==========================================
//  DOSYA OKUMA / YAZMA — TÜRKÇE KARAKTER GÜVENLİ (UTF-8)
// ==========================================

public static void odaVerileriKaydet() {
    try (PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(new FileOutputStream("odalar.csv"), StandardCharsets.UTF_8))) {
        for (Oda o : odalar.values())
            writer.println(o.getOdaNo() + "," + o.getTip() + "," + o.getKapasite() + ","
                    + o.getOdaFiyat() + "," + o.getOdaMusait() + "," + o.getAciklama());
    } catch (IOException e) { /* sessiz */ }
}

public static void odaVerileriniYukle() {
    File file = new File("odalar.csv");
    if (!file.exists()) return;
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
        String satir;
        while ((satir = reader.readLine()) != null) {
            // Olası UTF-8 BOM'u temizle
            if (satir.startsWith("\uFEFF")) satir = satir.substring(1);
            if (satir.trim().isEmpty()) continue;
            String[] parca = satir.split(",", -1);
            if (parca.length < 6) continue;
            Oda oda = new Oda(Integer.parseInt(parca[0]), parca[1],
                    Integer.parseInt(parca[2]), Double.parseDouble(parca[3]),
                    Boolean.parseBoolean(parca[4]), parca[5]);
            oda.setOdaMusait(Boolean.parseBoolean(parca[4]));
            odalar.put(Integer.parseInt(parca[0]), oda);
        }
    } catch (IOException e) { /* sessiz */ }
}

public static void musteriVerileriKaydet() {
    try (PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(new FileOutputStream("musteriler.csv"), StandardCharsets.UTF_8))) {
        for (Musteri m : musteriler.values())
            writer.println(m.getMusteriId() + "," + m.getMusteriAdSoyad() + "," + m.getMusteriTelefon()
                    + "," + m.getMusteriEmail() + "," + m.getMusteriTcKimlikNo() + "," + m.getMusteriAdres());
    } catch (IOException e) { /* sessiz */ }
}

public static void musteriVerileriYukle() {
    File file = new File("musteriler.csv");
    if (!file.exists()) return;
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
        String satir;
        while ((satir = reader.readLine()) != null) {
            if (satir.startsWith("\uFEFF")) satir = satir.substring(1);
            if (satir.trim().isEmpty()) continue;
            String[] parca = satir.split(",", -1);
            if (parca.length < 6) continue;
            Musteri musteri = new Musteri(Integer.parseInt(parca[0]),
                    parca[1], parca[2], parca[3], parca[4], parca[5]);
            musteriler.put(Integer.parseInt(parca[0]), musteri);
        }
    } catch (IOException e) { /* sessiz */ }
}

public static void rezervasyonVerileriKaydet() {
    try (PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(new FileOutputStream("rezervasyonlar.csv"), StandardCharsets.UTF_8))) {
        for (Rezervasyon r : rezervasyonlar.values())
            writer.println(r.getRezervasyonNo() + "," + r.getMusteri().getMusteriId() + ","
                    + r.getOda().getOdaNo() + "," + r.getGirisTarihi() + "," + r.getCikisTarihi()
                    + "," + r.getToplamUcret() + "," + r.getDurum());
    } catch (IOException e) { /* sessiz */ }
}

public static void rezervasyonVerileriYukle() {
    File file = new File("rezervasyonlar.csv");
    if (!file.exists()) return;
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
        String satir;
        while ((satir = reader.readLine()) != null) {
            if (satir.startsWith("\uFEFF")) satir = satir.substring(1);
            if (satir.trim().isEmpty()) continue;
            String[] parca = satir.split(",", -1);
            if (parca.length < 7) continue;
            int rNo = Integer.parseInt(parca[0]);
            int mId = Integer.parseInt(parca[1]);
            int oNo = Integer.parseInt(parca[2]);
            Musteri musteri = musteriler.get(mId);
            Oda oda = odalar.get(oNo);
            if (musteri != null && oda != null) {
                Rezervasyon rezervasyon = new Rezervasyon(rNo, musteri, oda,
                        LocalDate.parse(parca[3]), LocalDate.parse(parca[4]),
                        Double.parseDouble(parca[5]), parca[6]);
                rezervasyonlar.put(rNo, rezervasyon);
            }
        }
    } catch (Exception e) { /* sessiz */ }
}

}