package kkk;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * カラオケ予約システム — Java Swing GUI
 * ------------------------------------------------------
 * 画面遷移: メニュー -> ルーム一覧 -> 予約フォーム -> 予約完了 -> 予約確認（一覧・キャンセル）
 *
 * ★画像対応版
 *   images/rooms/*.png  … ルーム画像
 *   images/menu/*.png   … メニュー画像
 *   クラスパス優先、なければプロジェクト直下の images/ を読む。
 *   見つからない場合は「NO IMAGE」のプレースホルダを表示する（落ちない）。
 */
public class ttt extends JFrame {

    // ================= THEME =================
    static final Color BG_WHITE     = new Color(0x07, 0x0B, 0x18);
    static final Color PANEL_GRAY   = new Color(0x12, 0x18, 0x2B);
    static final Color PANEL_ALT    = new Color(0x1A, 0x21, 0x39);
    static final Color BORDER_GRAY  = new Color(0x32, 0x3D, 0x62);
    static final Color TEXT_DARK    = new Color(0xF5, 0xF7, 0xFF);
    static final Color TEXT_MUTED   = new Color(0x9B, 0xA7, 0xC9);
    static final Color ACCENT       = new Color(0xFF, 0x4F, 0xB8);
    static final Color ACCENT_HOVER = new Color(0x4A, 0xD8, 0xFF);
    static final Color SUCCESS      = new Color(0x58, 0xF3, 0xC0);
    static final Color GLOW_PURPLE  = new Color(0x64, 0x52, 0xD9);
    static final Color GLOW_CYAN    = new Color(0x63, 0xE8, 0xFF);
    static final Font  FONT_H1      = new Font("Yu Gothic UI", Font.BOLD, 28);
    static final Font  FONT_H2      = new Font("Yu Gothic UI", Font.BOLD, 18);
    static final Font  FONT_BODY    = new Font("Yu Gothic UI", Font.PLAIN, 14);
    static final Font  FONT_LABEL   = new Font("Yu Gothic UI", Font.PLAIN, 12);

    // ================= IMAGE SIZE =================
    static final int ROOM_IMG_W = 140, ROOM_IMG_H = 96;
    static final int MENU_IMG_W = 160, MENU_IMG_H = 110;
    static final int MENU_CARD_W = 220, MENU_CARD_H = 250;

    // ================= NAVIGATION =================
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContainer = new JPanel(cardLayout);

    // ================= APP STATE =================
    private final Database db = new Database();
    private Room selectedRoom;
    private Booking lastBooking;
    private String currentUser = "ゲスト";

    // 予約フォーム用フィールド
    private JTextField nameField, phoneField, dateField;
    private JComboBox<String> timeCombo;
    private JSpinner durationSpinner, guestsSpinner;
    private JLabel bookingRoomLabel, totalPriceLabel, bookingStatusLabel;
    private JLabel bookingRoomImage;
    private JLabel priceBreakdownLabel;   // ★追加: 室料金／人数料金の内訳表示
    private JButton bookingConfirmBtn;
    private Integer editingBookingId = null;
    private String screenBeforeBooking = "DASHBOARD";

    // 営業時間: 朝10:00 〜 翌5:00 (分単位, 翌日分は+24時間して表現)
    private static final int BUSINESS_OPEN_MIN  = 10 * 60;        // 10:00
    private static final int BUSINESS_CLOSE_MIN = (24 + 5) * 60;  // 翌5:00

    // ★変更: フリータイム条件 — 開始時刻に関係なく、利用時間が3時間を超えれば適用。
    //          3時間分の室料で頭打りにする（4時間でも8時間でも同額）
    private static final int FREE_TIME_MIN_HOURS_EXCLUSIVE = 3;   // 3時間"超"で適用
    private static final int FREE_TIME_CAP_HOURS = 3;             // 頭打り時間数

    // ★変更: 人数料金は2種類に分岐
    //   ・フリータイム中: 800円×人数（定額、時間に関係なし）＝従来仕様のまま
    //   ・フリータイム以外: 800円×人数×利用時間（実際のカラオケの時間制料金に近づけた）
    private static final int GUEST_FEE_PER_PERSON = 800;        // フリータイム用（定額）
    private static final int GUEST_HOURLY_RATE = 800;           // 通常時用（1人1時間あたり）

    // ★追加: お名前はカタカナ（全角）のみ、電話番号は半角数字とハイフンのみに制限する
    private static final String NAME_ALLOWED_CHAR_REGEX  = "[ァ-ヴー\\s　]"; // 全角カタカナ＋長音＋区切りの空白
    private static final String PHONE_ALLOWED_CHAR_REGEX = "[0-9\\-]";       // 半角数字とハイフンのみ（全角・文字は不可）

    // 予約確認テーブル
    private DefaultTableModel bookingsTableModel;

    // 予約番号検索（キャンセル・変更）画面用フィールド
    private JTextField lookupIdField;
    private JPanel lookupResultPanel;
    private JLabel lookupStatusLabel;

    // 注文画面用フィールド
    private String screenBeforeOrder = "DASHBOARD";
    private final Map<MenuItem, JSpinner> orderSpinners = new LinkedHashMap<>();
    private JLabel orderTotalLabel;
    private JPanel orderHistoryContainer;

    public ttt() {
        setTitle("MIDNIGHT RADIO LOUNGE | カラオケ予約システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setMinimumSize(new Dimension(980, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_WHITE);

        mainContainer.setBackground(BG_WHITE);

        mainContainer.add(buildMenuScreen(), "MENU");
        mainContainer.add(buildDashboardScreen(), "DASHBOARD");
        mainContainer.add(buildBookingScreen(), "BOOKING");
        mainContainer.add(buildConfirmationScreen(), "CONFIRMATION");
        mainContainer.add(buildMyBookingsScreen(), "MYBOOKINGS");
        mainContainer.add(buildLookupScreen(), "LOOKUP");
        mainContainer.add(buildOrderScreen(), "ORDER");
        mainContainer.add(buildOrderHistoryScreen(), "ORDER_HISTORY");

        add(mainContainer);
        cardLayout.show(mainContainer, "MENU");
        setVisible(true);
    }

    private void goTo(String screen) {
        cardLayout.show(mainContainer, screen);
    }

    // =========================================================
    // IMAGE LOADER（キャッシュ付き・見つからなくても落ちない）
    // =========================================================
    private static final Map<String, ImageIcon> ICON_CACHE = new HashMap<>();

    /**
     * 画像を読み込んで指定サイズにスケールしたアイコンを返す。
     * @param path "images/menu/beer.png" のようなプロジェクト相対パス
     */
    static ImageIcon loadIcon(String path, int w, int h) {
        String key = path + "@" + w + "x" + h;
        if (ICON_CACHE.containsKey(key)) return ICON_CACHE.get(key);

        System.out.println("読み込み画像パス：" + path);
        BufferedImage src = null;
        try {
            // 1) クラスパス（binフォルダにコピーされる場合）
            InputStream in = ttt.class.getResourceAsStream("/" + path);
            if (in != null) {
                src = ImageIO.read(in);
                in.close();
            }
            // 2) 実行ディレクトリ（プロジェクト直下）
            if (src == null) {
                File f = new File(path);
                if (f.exists()) src = ImageIO.read(f);
            }
        } catch (Exception ex) {
            System.err.println("画像の読み込みに失敗: " + path + " -> " + ex.getMessage());
        }

        ImageIcon icon = null;
        if (src != null) {
            Image scaled = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        } else {
            System.err.println("画像が見つかりません: " + path);
        }
        ICON_CACHE.put(key, icon);
        return icon;
    }

    /** 画像ラベル。画像が無い場合は「NO IMAGE」枠を表示する。 */
    private JLabel imageLabel(String path, int w, int h) {
        JLabel lbl = new JLabel();
        lbl.setPreferredSize(new Dimension(w, h));
        lbl.setMinimumSize(new Dimension(w, h));
        lbl.setMaximumSize(new Dimension(w, h));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(PANEL_ALT);
        lbl.setBorder(new LineBorder(BORDER_GRAY, 1, true));

        ImageIcon icon = loadIcon(path, w, h);
        if (icon != null) {
            lbl.setIcon(icon);
        } else {
            lbl.setText("NO IMAGE");
            lbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 10));
            lbl.setForeground(TEXT_MUTED);
            lbl.setBackground(PANEL_ALT);
        }
        return lbl;
    }

    private JLabel neonChip(String text, Color bg, Color fg) {
        JLabel chip = new JLabel(text);
        chip.setOpaque(true);
        chip.setBackground(bg);
        chip.setForeground(fg);
        chip.setFont(new Font("Yu Gothic UI", Font.BOLD, 11));
        chip.setBorder(new CompoundBorder(new LineBorder(bg.brighter(), 1, true), new EmptyBorder(4, 10, 4, 10)));
        return chip;
    }

    private String roomMood(Room room) {
        if (room.id == 1) return "少人数向けのネオンブース。ラジオDJ席のように会話が映える空間です。";
        if (room.id == 2) return "夜景ラウンジを意識した中規模ルーム。フード選びもしやすい万能タイプです。";
        return "深夜ラジオの公開収録をイメージしたVIP空間。大人数でも盛り上がれます。";
    }

    static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;

        GradientPanel(java.awt.LayoutManager layout, Color start, Color end) {
            super(layout);
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // SCREEN 1.5 — MENU（予約する／キャンセル・変更／注文履歴）
    // =========================================================
    private JPanel buildMenuScreen() {
        GradientPanel wrap = new GradientPanel(new GridBagLayout(), new Color(0x08, 0x0C, 0x18), new Color(0x1D, 0x13, 0x33));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_GRAY);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x48, 0x5A, 0x87), 1, true),
                new EmptyBorder(32, 34, 32, 34)
        ));
        card.setPreferredSize(new Dimension(620, 430));

        JLabel badge = neonChip("MIDNIGHT RADIO LOUNGE", new Color(0x22, 0x2B, 0x46), ACCENT_HOVER);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logo = new JLabel("夜の街を流れるネオン予約体験");
        logo.setFont(new Font("Yu Gothic UI", Font.BOLD, 30));
        logo.setForeground(TEXT_DARK);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(18, 0, 4, 0));

        JLabel sub = new JLabel("ルーム選択とフード選択を、深夜ラジオ番組のような世界観で再構成しました。");
        sub.setFont(FONT_BODY);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(0, 0, 22, 0));

        JButton roomBtn = accentButton("ROOM SELECT  |  ルームを選ぶ");
        roomBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        roomBtn.addActionListener(e -> goTo("DASHBOARD"));

        JButton foodBtn = ghostButton("FOOD SELECT  |  フードを選ぶ");
        foodBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        foodBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        foodBtn.addActionListener(e -> {
            screenBeforeOrder = "MENU";
            goTo("ORDER");
        });

        JPanel utilityRow = new JPanel(new GridLayout(1, 2, 10, 0));
        utilityRow.setBackground(PANEL_GRAY);
        utilityRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        utilityRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton lookupBtn = ghostButton("予約のキャンセル・変更");
        lookupBtn.addActionListener(e -> {
            if (lookupIdField != null) {
                lookupIdField.setText("例: 1001");
                lookupIdField.setForeground(TEXT_MUTED);
            }
            if (lookupResultPanel != null) {
                lookupResultPanel.removeAll();
                lookupResultPanel.revalidate();
                lookupResultPanel.repaint();
            }
            if (lookupStatusLabel != null) lookupStatusLabel.setText(" ");
            goTo("LOOKUP");
        });

        JButton historyBtn = ghostButton("注文履歴を見る");
        historyBtn.addActionListener(e -> {
            refreshOrderHistory();
            goTo("ORDER_HISTORY");
        });

        utilityRow.add(lookupBtn);
        utilityRow.add(historyBtn);

        JLabel foot = new JLabel("ROOM・FOOD のカードをタップしながら、ライブ会場のように直感的に選択できます。");
        foot.setFont(FONT_LABEL);
        foot.setForeground(ACCENT_HOVER);
        foot.setAlignmentX(Component.LEFT_ALIGNMENT);
        foot.setBorder(new EmptyBorder(18, 0, 0, 0));

        card.add(badge);
        card.add(logo);
        card.add(sub);
        card.add(roomBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(foodBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(utilityRow);
        card.add(Box.createVerticalGlue());
        card.add(foot);

        wrap.add(card);
        return wrap;
    }

    // =========================================================
    // SCREEN 2 — DASHBOARD (room list)
    // =========================================================
    private JPanel buildDashboardScreen() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(BG_WHITE);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(BG_WHITE);
        JLabel title = new JLabel("ROOM SELECT");
        title.setFont(FONT_H1);
        title.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("夜の街にあるラジオブースのような部屋から、お好みの空間を選択してください");
        sub.setFont(FONT_BODY);
        sub.setForeground(TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(sub);

        JButton menuBtn = ghostButton("≡ メニューに戻る");
        menuBtn.addActionListener(e -> goTo("MENU"));

        JButton myBookingsBtn = ghostButton("予約の確認・変更");
        myBookingsBtn.addActionListener(e -> {
            refreshBookingsTable();
            goTo("MYBOOKINGS");
        });

        JButton orderBtn = ghostButton("FOOD SELECT");
        orderBtn.addActionListener(e -> {
            screenBeforeOrder = "DASHBOARD";
            goTo("ORDER");
        });

        JButton historyBtn = ghostButton("📋 注文履歴");
        historyBtn.addActionListener(e -> {
            refreshOrderHistory();
            goTo("ORDER_HISTORY");
        });

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerButtons.setBackground(BG_WHITE);
        headerButtons.add(menuBtn);
        headerButtons.add(historyBtn);
        headerButtons.add(orderBtn);
        headerButtons.add(myBookingsBtn);

        headerRow.add(titleBlock, BorderLayout.WEST);
        headerRow.add(headerButtons, BorderLayout.EAST);

        JPanel roomList = new JPanel();
        roomList.setLayout(new BoxLayout(roomList, BoxLayout.Y_AXIS));
        roomList.setBackground(BG_WHITE);
        roomList.setBorder(new EmptyBorder(20, 0, 0, 0));

        for (Room room : db.getRooms()) {
            roomList.add(buildRoomCard(room));
            roomList.add(Box.createVerticalStrut(14));
        }

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        outer.add(headerRow, BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    // ★変更点: ルームカードに画像（左側）を追加
    // ★変更点: 「選択する」ボタンを廃止し、カード全体をクリック可能にした
    private JPanel buildRoomCard(Room room) {
        JPanel card = new JPanel(new BorderLayout(18, 0));
        card.setBackground(PANEL_GRAY);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_GRAY, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 156));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel thumb = imageLabel(room.imagePath, ROOM_IMG_W, ROOM_IMG_H);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(PANEL_GRAY);

        JPanel headRow = new JPanel(new BorderLayout());
        headRow.setBackground(PANEL_GRAY);

        JLabel nameLbl = new JLabel(room.name);
        nameLbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 20));
        nameLbl.setForeground(TEXT_DARK);

        JLabel selectChip = neonChip("ON AIR ROOM", new Color(0x22, 0x2B, 0x46), ACCENT_HOVER);

        headRow.add(nameLbl, BorderLayout.WEST);
        headRow.add(selectChip, BorderLayout.EAST);

        String roomFeeText = room.pricePerHour == 0
                ? "室料無料 / 人数×時間の料金制"
                : "1時間あたり " + formatYen(room.pricePerHour);

        JLabel capLbl = new JLabel("定員 " + room.capacity + "  ・  " + roomFeeText);
        capLbl.setFont(FONT_BODY);
        capLbl.setForeground(TEXT_MUTED);
        capLbl.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel moodLbl = new JLabel("<html><body style='width:520px'>" + roomMood(room) + "</body></html>");
        moodLbl.setFont(FONT_LABEL);
        moodLbl.setForeground(new Color(0xCF, 0xD7, 0xFF));
        moodLbl.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel hintLbl = new JLabel("このカードをクリックして予約フォームへ");
        hintLbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        hintLbl.setForeground(ACCENT);
        hintLbl.setBorder(new EmptyBorder(10, 0, 0, 0));

        info.add(headRow);
        info.add(capLbl);
        info.add(moodLbl);
        info.add(Box.createVerticalGlue());
        info.add(hintLbl);

        MouseAdapter cardClick = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { startNewBooking(room); }
            public void mouseEntered(MouseEvent e) { card.setBackground(PANEL_ALT); setChildrenBackground(card, PANEL_ALT); }
            public void mouseExited(MouseEvent e)  { card.setBackground(PANEL_GRAY); setChildrenBackground(card, PANEL_GRAY); }
        };
        card.addMouseListener(cardClick);
        info.addMouseListener(cardClick);
        headRow.addMouseListener(cardClick);
        nameLbl.addMouseListener(cardClick);
        capLbl.addMouseListener(cardClick);
        moodLbl.addMouseListener(cardClick);
        hintLbl.addMouseListener(cardClick);
        thumb.addMouseListener(cardClick);

        card.add(thumb, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // ホバー時にカード配下の非画像パネル背景も一緒に色を変える
    private void setChildrenBackground(JComponent parent, Color color) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(color);
                setChildrenBackground((JPanel) c, color);
            }
        }
    }

    // =========================================================
    // SCREEN 3 — BOOKING FORM
    // =========================================================
    private JPanel buildBookingScreen() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_WHITE);

        JButton backBtn = ghostButton("← 戻る");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> goTo(screenBeforeBooking));

        JLabel title = new JLabel("予約詳細の入力");
        title.setFont(FONT_H1);
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 0, 0, 0));

        // ★変更点: 選択中ルームの画像＋名前を横並びで表示
        bookingRoomImage = new JLabel();
        bookingRoomImage.setPreferredSize(new Dimension(ROOM_IMG_W, ROOM_IMG_H));
        bookingRoomImage.setBorder(new LineBorder(BORDER_GRAY, 1, true));
        bookingRoomImage.setHorizontalAlignment(SwingConstants.CENTER);

        bookingRoomLabel = new JLabel("ルームが選択されていません");
        bookingRoomLabel.setFont(FONT_BODY);
        bookingRoomLabel.setForeground(ACCENT);

        JPanel roomHeaderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        roomHeaderRow.setBackground(BG_WHITE);
        roomHeaderRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomHeaderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROOM_IMG_H + 20));
        roomHeaderRow.setBorder(new EmptyBorder(8, 0, 12, 0));
        roomHeaderRow.add(bookingRoomImage);
        roomHeaderRow.add(bookingRoomLabel);

        header.add(backBtn);
        header.add(title);
        header.add(roomHeaderRow);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG_WHITE);

        // ★変更: 名前はカタカナのみ、電話番号は半角数字のみに制限するため、
        //          プレースホルダも制限文字種に合わせて変更（"例:"の漢字を除去）
        nameField = styledTextField("ヤマダ タロウ");
        restrictInputToCharPattern(nameField, NAME_ALLOWED_CHAR_REGEX);
        phoneField = styledTextField("09012345678");
        restrictInputToCharPattern(phoneField, PHONE_ALLOWED_CHAR_REGEX);
        dateField = dateDisplayField("日付を選択してください");

        JButton calBtn = ghostButton("📅");
        calBtn.setPreferredSize(new Dimension(44, 38));
        calBtn.setMaximumSize(new Dimension(44, 38));
        calBtn.addActionListener(e -> showDatePicker(dateField, dateField));
        dateField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showDatePicker(dateField, dateField); }
        });

        JPanel dateFieldWrap = new JPanel(new BorderLayout(6, 0));
        dateFieldWrap.setBackground(BG_WHITE);
        dateFieldWrap.add(dateField, BorderLayout.CENTER);
        dateFieldWrap.add(calBtn, BorderLayout.EAST);

        timeCombo = new JComboBox<>(buildBusinessHourSlots());
        timeCombo.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        timeCombo.setBackground(PANEL_ALT);
        timeCombo.setForeground(TEXT_DARK);
        timeCombo.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(2, 8, 2, 8)));
        timeCombo.setFocusable(false);
        timeCombo.addActionListener(e -> updateTotal());

        form.add(fieldBlock("お名前（カタカナのみ／フルネーム）", nameField));
        form.add(Box.createVerticalStrut(14));
        form.add(fieldBlock("電話番号（半角数字のみ・ハイフンなし可）", phoneField));
        form.add(Box.createVerticalStrut(14));

        JPanel dateTimeRow = new JPanel(new GridLayout(1, 2, 14, 0));
        dateTimeRow.setBackground(BG_WHITE);
        dateTimeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateTimeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        dateTimeRow.add(fieldBlock("ご利用日", dateFieldWrap));
        dateTimeRow.add(fieldBlock("ご利用開始時間（営業時間 10:00〜翌5:00）", timeCombo));
        form.add(dateTimeRow);
        form.add(Box.createVerticalStrut(14));

        durationSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 8, 1));
        styleSpinner(durationSpinner);
        durationSpinner.addChangeListener(e -> updateTotal());

        guestsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 15, 1));
        styleSpinner(guestsSpinner);
        guestsSpinner.addChangeListener(e -> updateTotal());   // ★追加: 人数変更でも再計算

        JPanel durGuestRow = new JPanel(new GridLayout(1, 2, 14, 0));
        durGuestRow.setBackground(BG_WHITE);
        durGuestRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        durGuestRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        durGuestRow.add(fieldBlock("ご利用時間 (時間)", durationSpinner));
        durGuestRow.add(fieldBlock("ご利用人数", guestsSpinner));
        form.add(durGuestRow);
        form.add(Box.createVerticalStrut(10));

        // ★追加: 室料金／フリータイム／人数料金の内訳表示
        priceBreakdownLabel = new JLabel(" ");
        priceBreakdownLabel.setFont(FONT_LABEL);
        priceBreakdownLabel.setForeground(TEXT_MUTED);
        priceBreakdownLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceBreakdownLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        form.add(priceBreakdownLabel);

        JPanel summary = new JPanel(new BorderLayout());
        summary.setBackground(PANEL_GRAY);
        summary.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(14, 16, 14, 16)));
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        JLabel summaryLbl = new JLabel("概算合計金額");
        summaryLbl.setFont(FONT_BODY);
        summaryLbl.setForeground(TEXT_MUTED);
        totalPriceLabel = new JLabel("0円");
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalPriceLabel.setForeground(ACCENT);
        summary.add(summaryLbl, BorderLayout.WEST);
        summary.add(totalPriceLabel, BorderLayout.EAST);
        form.add(summary);
        form.add(Box.createVerticalStrut(14));

        bookingStatusLabel = new JLabel(" ");
        bookingStatusLabel.setFont(FONT_LABEL);
        bookingStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(bookingStatusLabel);
        form.add(Box.createVerticalStrut(6));

        bookingConfirmBtn = accentButton("予約を確定する");
        bookingConfirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookingConfirmBtn.addActionListener(e -> handleBookingSubmit());
        form.add(bookingConfirmBtn);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);

        outer.add(header, BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    // ★追加: 予約フォームのルーム画像を差し替える
    private void setBookingRoomHeader(Room room) {
        String roomFeeSuffix = room.pricePerHour == 0 ? "（室料無料）" : "  (" + formatYen(room.pricePerHour) + "/時)";
        bookingRoomLabel.setText(room.name + "  ［定員 " + room.capacity + "］" + roomFeeSuffix);
        if (bookingRoomImage != null) {
            ImageIcon icon = loadIcon(room.imagePath, ROOM_IMG_W, ROOM_IMG_H);
            if (icon != null) {
                bookingRoomImage.setIcon(icon);
                bookingRoomImage.setText("");
            } else {
                bookingRoomImage.setIcon(null);
                bookingRoomImage.setText("NO IMAGE");
                bookingRoomImage.setFont(new Font("SansSerif", Font.PLAIN, 10));
                bookingRoomImage.setForeground(TEXT_MUTED);
            }
        }
        configureGuestsSpinnerForRoom(room);   // ★追加: 部屋の定員に合わせて人数スピナーの範囲を更新
    }

    // ★追加: 選択中ルームの定員（minGuests〜maxGuests）に合わせて人数スピナーの範囲を切り替える
    private void configureGuestsSpinnerForRoom(Room room) {
        if (guestsSpinner == null) return;
        int current;
        try {
            current = (Integer) guestsSpinner.getValue();
        } catch (Exception ex) {
            current = room.minGuests;
        }
        int clamped = Math.max(room.minGuests, Math.min(room.maxGuests, current));
        guestsSpinner.setModel(new SpinnerNumberModel(clamped, room.minGuests, room.maxGuests, 1));
        styleSpinner(guestsSpinner);
        // 注意: guestsSpinner.addChangeListener は buildBookingScreen() で登録済みで、
        //       setModel() を呼んでも spinner 本体のリスナーは保持されるため、ここで再登録すると二重発火する。
    }

    // ★追加: 料金内訳の計算結果を保持する小さなクラス
    static class PriceBreakdown {
        final boolean freeTime;
        final int billedHours;   // フリータイム時は頭打りの時間数
        final int roomCost;
        final int guestFee;
        final int total;

        PriceBreakdown(boolean freeTime, int billedHours, int roomCost, int guestFee) {
            this.freeTime = freeTime;
            this.billedHours = billedHours;
            this.roomCost = roomCost;
            this.guestFee = guestFee;
            this.total = roomCost + guestFee;
        }
    }

    // ★変更: 室料金＋フリータイム頭打り＋人数料金をまとめて計算する
    //          フリータイムは開始時刻を問わず「利用時間が3時間を超えるか」だけで判定する
    private PriceBreakdown computePrice(Room room, String time, int hours, int guests) {
        boolean freeTime = hours > FREE_TIME_MIN_HOURS_EXCLUSIVE;

        int billedHours = freeTime ? FREE_TIME_CAP_HOURS : hours;
        int roomCost = room.pricePerHour * billedHours;

        // ★変更: フリータイムは従来通り定額、それ以外は「人数×時間」の実カラオケ方式
        int guestFee = freeTime
                ? GUEST_FEE_PER_PERSON * guests
                : GUEST_HOURLY_RATE * guests * hours;

        return new PriceBreakdown(freeTime, billedHours, roomCost, guestFee);
    }

    private void updateTotal() {
        if (selectedRoom == null || totalPriceLabel == null) return;
        int hours = (Integer) durationSpinner.getValue();
        int guests = (Integer) guestsSpinner.getValue();
        String time = (String) timeCombo.getSelectedItem();

        PriceBreakdown pb = computePrice(selectedRoom, time, hours, guests);

        StringBuilder html = new StringBuilder("<html>");
        if (pb.freeTime) {
            html.append("室料金: ").append(pb.roomCost == 0 ? "無料" : formatYen(pb.roomCost))
                .append(" 〈フリータイム／3時間分で頭打り〉<br>");
            html.append("人数料金: ").append(formatYen(GUEST_FEE_PER_PERSON))
                .append(" × ").append(guests).append("名 = ").append(formatYen(pb.guestFee))
                .append("（定額）");
        } else {
            html.append("室料金: ").append(pb.roomCost == 0 ? "無料" : formatYen(pb.roomCost))
                .append(" （").append(hours).append("時間）<br>");
            html.append("人数料金: ").append(formatYen(GUEST_HOURLY_RATE))
                .append(" × ").append(guests).append("名 × ").append(hours).append("時間 = ")
                .append(formatYen(pb.guestFee));
        }
        html.append("</html>");
        priceBreakdownLabel.setText(html.toString());

        totalPriceLabel.setText(formatYen(pb.total));
    }

    private void handleBookingSubmit() {
        if (selectedRoom == null) {
            bookingStatusLabel.setForeground(ACCENT);
            bookingStatusLabel.setText("最初にルームを選択してください。");
            return;
        }
        String name = textOrEmpty(nameField, "ヤマダ タロウ");
        String phone = textOrEmpty(phoneField, "09012345678");
        String date = textOrEmpty(dateField, "日付を選択してください");
        String time = (String) timeCombo.getSelectedItem();
        int hours = (Integer) durationSpinner.getValue();

        // ★変更: 名前はカタカナのみを許可（入力自体もフィルタ済みだが、念のため空文字などをここで弾く）
        boolean nameOk = !name.trim().isEmpty() && name.matches("^[ァ-ヴー\\s　]+$");
        // ★変更: 電話番号は半角数字のみ（全角・文字は入力フィルタで既にブロック済み）
        boolean phoneOk = phone.matches("^(0\\d{1,4}-\\d{1,4}-\\d{4}|0\\d{9,10})$");
        boolean dateOk = date.matches("^\\d{4}-\\d{2}-\\d{2}$");
        boolean timeOk = time != null && isWithinBusinessHours(time, hours);

        markValid(nameField, "ヤマダ タロウ", nameOk);
        markValid(phoneField, "09012345678", phoneOk);
        markValid(dateField, "日付を選択してください", dateOk);

        if (!(nameOk && phoneOk && dateOk)) {
            bookingStatusLabel.setForeground(ACCENT);
            bookingStatusLabel.setText("入力内容に不備があります。赤枠のフィールドを確認してください。");
            return;
        }
        if (!timeOk) {
            bookingStatusLabel.setForeground(ACCENT);
            bookingStatusLabel.setText("営業時間（10:00〜翌5:00）を超えるご予約はできません。開始時間かご利用時間を調整してください。");
            return;
        }

        int guests = (Integer) guestsSpinner.getValue();

        // ★追加: 人数制限のチェック（スピナーの範囲でも制御しているが、念のため明示チェック）
        boolean guestsOk = guests >= selectedRoom.minGuests && guests <= selectedRoom.maxGuests;
        if (!guestsOk) {
            bookingStatusLabel.setForeground(ACCENT);
            bookingStatusLabel.setText(selectedRoom.name + "のご利用人数は " + selectedRoom.capacity + " です。人数を調整してください。");
            return;
        }

        PriceBreakdown pb = computePrice(selectedRoom, time, hours, guests);   // ★変更: 内訳込みで計算

        if (editingBookingId != null) {
            int updatedId = editingBookingId;
            db.updateBooking(updatedId, selectedRoom, name, phone, date, time, hours, guests,
                    pb.freeTime, pb.roomCost, pb.guestFee, pb.total);
            editingBookingId = null;
            bookingConfirmBtn.setText("予約を確定する");
            bookingStatusLabel.setText(" ");
            refreshBookingsTable();
            JOptionPane.showMessageDialog(this,
                    "予約番号 #" + updatedId + " の内容を更新しました。",
                    "予約の変更", JOptionPane.INFORMATION_MESSAGE);
            goTo("MYBOOKINGS");
            return;
        }
        
        RoomDAO roomDAO = new RoomDAO();

        boolean available =
            roomDAO.checkStock(selectedRoom.id);


        if(!available){

            JOptionPane.showMessageDialog(
                this,
                "申し訳ありません。\n選択された部屋は現在空きがありません。",
                "予約エラー",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Booking booking = new Booking(
                db.nextBookingId(), selectedRoom, name, phone, date, time, hours, guests,
                pb.freeTime, pb.roomCost, pb.guestFee, pb.total
        );
        db.addBooking(booking);
        ReservationDAO dao = new ReservationDAO();
        
        boolean result = dao.insert(
        	    selectedRoom.name,
        	    name,
        	    date,
        	    time,
        	    hours,
        	    guests,
        	    pb.total
        	);


        if(result){

            roomDAO.updateStock(selectedRoom.id);

            JOptionPane.showMessageDialog(
                this,
                "予約完了しました"
            );
        }

        System.out.println("予約DB登録：" + result);
        lastBooking = booking;

        bookingStatusLabel.setText(" ");
        populateConfirmationScreen();
        goTo("CONFIRMATION");
    }

    // ダッシュボードから新規予約を開始する際にフォームをリセットする
    private void startNewBooking(Room room) {
        editingBookingId = null;
        screenBeforeBooking = "DASHBOARD";
        selectedRoom = room;
        setBookingRoomHeader(room);   // ★画像も更新

        nameField.setText("ヤマダ タロウ");
        nameField.setForeground(TEXT_MUTED);
        phoneField.setText("09012345678");
        phoneField.setForeground(TEXT_MUTED);
        dateField.setText("日付を選択してください");
        dateField.setForeground(TEXT_MUTED);
        timeCombo.setSelectedIndex(0);
        durationSpinner.setValue(2);
        guestsSpinner.setValue(room.minGuests);   // ★修正: 固定値2だとミディアム/VIPの下限を下回り例外になるため

        bookingStatusLabel.setText(" ");
        if (bookingConfirmBtn != null) bookingConfirmBtn.setText("予約を確定する");
        updateTotal();
        goTo("BOOKING");
    }

    // 予約番号検索画面から、既存予約の内容を予約フォームに読み込んで変更モードにする
    private void startEditBooking(Booking booking, String returnScreen) {
        editingBookingId = booking.id;
        screenBeforeBooking = returnScreen;
        selectedRoom = booking.room;
        setBookingRoomHeader(booking.room);   // ★画像も更新

        nameField.setText(booking.customerName);
        nameField.setForeground(TEXT_DARK);
        phoneField.setText(booking.phone);
        phoneField.setForeground(TEXT_DARK);
        dateField.setText(booking.date);
        dateField.setForeground(TEXT_DARK);
        timeCombo.setSelectedItem(booking.time);
        durationSpinner.setValue(booking.durationHours);
        guestsSpinner.setValue(booking.guests);

        bookingStatusLabel.setText(" ");
        if (bookingConfirmBtn != null) bookingConfirmBtn.setText("変更を保存する");
        updateTotal();
        goTo("BOOKING");
    }

    // =========================================================
    // SCREEN 4 — CONFIRMATION
    // =========================================================
    private JLabel confirmSummaryLabel;
    private JLabel confirmRoomImage;

    private JPanel buildConfirmationScreen() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG_WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(36, 36, 36, 36)));
        card.setPreferredSize(new Dimension(460, 500));

        JLabel check = new JLabel("✓");
        check.setFont(new Font("SansSerif", Font.BOLD, 36));
        check.setForeground(SUCCESS);
        check.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("予約が完了しました");
        title.setFont(FONT_H1);
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(6, 0, 12, 0));

        // ★追加: 予約完了画面にもルーム画像
        confirmRoomImage = new JLabel();
        confirmRoomImage.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmRoomImage.setPreferredSize(new Dimension(ROOM_IMG_W, ROOM_IMG_H));
        confirmRoomImage.setMaximumSize(new Dimension(ROOM_IMG_W, ROOM_IMG_H));
        confirmRoomImage.setBorder(new LineBorder(BORDER_GRAY, 1, true));
        confirmRoomImage.setHorizontalAlignment(SwingConstants.CENTER);

        confirmSummaryLabel = new JLabel("<html></html>");
        confirmSummaryLabel.setFont(FONT_BODY);
        confirmSummaryLabel.setForeground(TEXT_MUTED);
        confirmSummaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmSummaryLabel.setBorder(new EmptyBorder(12, 0, 24, 0));

        JButton viewBookingsBtn = accentButton("予約一覧を確認する");
        viewBookingsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewBookingsBtn.addActionListener(e -> {
            refreshBookingsTable();
            goTo("MYBOOKINGS");
        });

        JButton orderFoodBtn = accentButton("料理・ドリンクを注文する");
        orderFoodBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        orderFoodBtn.addActionListener(e -> {
            screenBeforeOrder = "CONFIRMATION";
            goTo("ORDER");
        });

        JButton doneBtn = ghostButton("続けて別のルームを予約する");
        doneBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        doneBtn.addActionListener(e -> goTo("DASHBOARD"));

        card.add(check);
        card.add(title);
        card.add(confirmRoomImage);
        card.add(confirmSummaryLabel);
        card.add(viewBookingsBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(orderFoodBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(doneBtn);

        wrap.add(card);
        return wrap;
    }

    // ★追加: 人数料金の表示テキストをフリータイム/通常で出し分ける（複数画面で共通利用）
    private String guestFeeDisplayText(Booking b) {
        if (b.freeTime) {
            return formatYen(GUEST_FEE_PER_PERSON) + " × " + b.guests + "名 = " + formatYen(b.guestFee) + "（定額）";
        } else {
            return formatYen(GUEST_HOURLY_RATE) + " × " + b.guests + "名 × " + b.durationHours + "時間 = " + formatYen(b.guestFee);
        }
    }

    private String roomCostDisplayText(Booking b) {
        return b.roomCost == 0 ? "無料" : formatYen(b.roomCost);
    }

    private void populateConfirmationScreen() {
        if (lastBooking == null) return;

        ImageIcon icon = loadIcon(lastBooking.room.imagePath, ROOM_IMG_W, ROOM_IMG_H);
        if (confirmRoomImage != null) {
            if (icon != null) { confirmRoomImage.setIcon(icon); confirmRoomImage.setText(""); }
            else { confirmRoomImage.setIcon(null); confirmRoomImage.setText("NO IMAGE"); }
        }

        String freeTimeTag = lastBooking.freeTime ? "　〈フリータイム〉" : "";
        String html = "<html><body style='width:320px'>"
                + "予約番号: #" + lastBooking.id + " <br>予約名義: <b>" + escape(lastBooking.customerName) + "</b> 様<br>"
                + "部屋タイプ: " + lastBooking.room.name + "<br>日時: " + lastBooking.date + " " + lastBooking.time + "〜<br>"
                + "利用時間: " + lastBooking.durationHours + " 時間" + freeTimeTag + ", 人数: " + lastBooking.guests + " 名<br>"
                + "室料金: " + roomCostDisplayText(lastBooking) + "<br>"
                + "人数料金: " + guestFeeDisplayText(lastBooking) + "<br>"
                + "合計金額: <b>" + formatYen(lastBooking.totalPrice) + "</b>"
                + "</body></html>";
        confirmSummaryLabel.setText(html);
    }

    // =========================================================
    // SCREEN 5 — MY BOOKINGS
    // =========================================================
    private JPanel buildMyBookingsScreen() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_WHITE);

        JButton backBtn = ghostButton("← ルーム一覧に戻る");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> goTo("DASHBOARD"));

        JLabel title = new JLabel("現在の予約一覧");
        title.setFont(FONT_H1);
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 0, 16, 0));

        header.add(backBtn);
        header.add(title);

        String[] columns = {"予約ID", "ルーム名", "予約日", "時間", "時間(h)", "人数", "室料金", "人数料金", "合計金額"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(bookingsTableModel);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER_GRAY);
        table.setBackground(PANEL_ALT);
        table.setSelectionBackground(new Color(0x2B, 0x3A, 0x63));
        table.setSelectionForeground(TEXT_DARK);
        table.getTableHeader().setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(PANEL_GRAY);
        table.getTableHeader().setForeground(TEXT_MUTED);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(BORDER_GRAY, 1, true));

        JButton editBtn = accentButton("選択した予約を変更する");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (Integer) bookingsTableModel.getValueAt(row, 0);
                Booking b = db.findBookingById(id);
                if (b != null) startEditBooking(b, "MYBOOKINGS");
            } else {
                JOptionPane.showMessageDialog(this, "変更する予約を一覧から選択してください。");
            }
        });

        JButton cancelBtn = ghostButton("選択した予約をキャンセル");
        cancelBtn.setBorder(new CompoundBorder(new LineBorder(ACCENT, 1, true), new EmptyBorder(9, 16, 9, 16)));
        cancelBtn.setForeground(ACCENT);
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int result = JOptionPane.showConfirmDialog(this, "選択した予約をキャンセルしますか？", "予約のキャンセル", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    int id = (Integer) bookingsTableModel.getValueAt(row, 0);
                    db.cancelBooking(id);
                    refreshBookingsTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "キャンセルする予約を一覧から選択してください。");
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(BG_WHITE);
        footer.setBorder(new EmptyBorder(14, 0, 0, 0));
        footer.add(editBtn);
        footer.add(cancelBtn);

        outer.add(header, BorderLayout.NORTH);
        outer.add(tableScroll, BorderLayout.CENTER);
        outer.add(footer, BorderLayout.SOUTH);
        return outer;
    }

    // =========================================================
    // SCREEN 5.5 — LOOKUP（予約番号でキャンセル・変更）
    // =========================================================
    private JPanel buildLookupScreen() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_WHITE);

        JButton backBtn = ghostButton("← メニューに戻る");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> goTo("MENU"));

        JLabel title = new JLabel("予約のキャンセル・変更");
        title.setFont(FONT_H1);
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel sub = new JLabel("予約番号を入力して、予約内容を確認してください");
        sub.setFont(FONT_BODY);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(4, 0, 16, 0));

        header.add(backBtn);
        header.add(title);
        header.add(sub);

        lookupIdField = styledTextField("例: 1001");

        JButton searchBtn = accentButton("予約を検索する");
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBtn.addActionListener(e -> handleLookupSearch());

        lookupStatusLabel = new JLabel(" ");
        lookupStatusLabel.setFont(FONT_LABEL);
        lookupStatusLabel.setForeground(ACCENT);
        lookupStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lookupStatusLabel.setBorder(new EmptyBorder(8, 0, 10, 0));

        JPanel searchBlock = new JPanel();
        searchBlock.setLayout(new BoxLayout(searchBlock, BoxLayout.Y_AXIS));
        searchBlock.setBackground(BG_WHITE);
        searchBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBlock.add(fieldBlock("予約番号", lookupIdField));
        searchBlock.add(Box.createVerticalStrut(10));
        searchBlock.add(searchBtn);
        searchBlock.add(lookupStatusLabel);

        lookupResultPanel = new JPanel();
        lookupResultPanel.setLayout(new BoxLayout(lookupResultPanel, BoxLayout.Y_AXIS));
        lookupResultPanel.setBackground(BG_WHITE);
        lookupResultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lookupResultPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG_WHITE);
        form.add(searchBlock);
        form.add(lookupResultPanel);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);

        outer.add(header, BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    private void handleLookupSearch() {
        lookupResultPanel.removeAll();
        String idText = textOrEmpty(lookupIdField, "例: 1001").trim();

        if (!idText.matches("^\\d+$")) {
            lookupStatusLabel.setForeground(ACCENT);
            lookupStatusLabel.setText("予約番号は数字で入力してください。");
            lookupResultPanel.revalidate();
            lookupResultPanel.repaint();
            return;
        }

        int id = Integer.parseInt(idText);
        Booking booking = db.findBookingById(id);

        if (booking == null) {
            lookupStatusLabel.setForeground(ACCENT);
            lookupStatusLabel.setText("該当する予約が見つかりませんでした。予約番号をご確認ください。");
            lookupResultPanel.revalidate();
            lookupResultPanel.repaint();
            return;
        }

        lookupStatusLabel.setForeground(ACCENT);
        lookupStatusLabel.setText(" ");

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_GRAY);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(16, 18, 16, 18)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JLabel roomLbl = new JLabel(booking.room.name + "（予約番号 #" + booking.id + "）");
        roomLbl.setFont(FONT_H2);
        roomLbl.setForeground(TEXT_DARK);
        roomLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ★追加: 検索結果にもルーム画像
        JLabel thumb = imageLabel(booking.room.imagePath, ROOM_IMG_W, ROOM_IMG_H);
        thumb.setAlignmentX(Component.LEFT_ALIGNMENT);
        thumb.setBackground(PANEL_GRAY);
        JPanel thumbWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        thumbWrap.setBackground(PANEL_GRAY);
        thumbWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        thumbWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROOM_IMG_H + 12));
        thumbWrap.setBorder(new EmptyBorder(8, 0, 0, 0));
        thumbWrap.add(thumb);

        String lookupFreeTimeTag = booking.freeTime ? "　〈フリータイム〉" : "";
        JLabel detail = new JLabel("<html>"
                + "予約名義: " + escape(booking.customerName) + " 様<br>"
                + "ご利用日: " + booking.date + "<br>"
                + "開始時間: " + booking.time + "<br>"
                + "利用時間: " + booking.durationHours + " 時間" + lookupFreeTimeTag + "<br>"
                + "人数: " + booking.guests + " 名<br>"
                + "室料金: " + roomCostDisplayText(booking) + "<br>"
                + "人数料金: " + guestFeeDisplayText(booking) + "<br>"
                + "合計金額: " + formatYen(booking.totalPrice)
                + "</html>");
        detail.setFont(FONT_BODY);
        detail.setForeground(TEXT_MUTED);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);
        detail.setBorder(new EmptyBorder(8, 0, 14, 0));

        JButton editBtn = accentButton("この予約を変更する");
        editBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        editBtn.addActionListener(e -> startEditBooking(booking, "LOOKUP"));

        JButton cancelBtn = ghostButton("この予約をキャンセルする");
        cancelBtn.setForeground(ACCENT);
        cancelBtn.setBorder(new CompoundBorder(new LineBorder(ACCENT, 1, true), new EmptyBorder(9, 16, 9, 16)));
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "予約番号 #" + booking.id + " をキャンセルしますか？",
                    "予約のキャンセル", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                db.cancelBooking(booking.id);
                refreshBookingsTable();
                lookupResultPanel.removeAll();
                lookupStatusLabel.setForeground(SUCCESS);
                lookupStatusLabel.setText("予約番号 #" + booking.id + " をキャンセルしました。");
                lookupResultPanel.revalidate();
                lookupResultPanel.repaint();
            }
        });

        card.add(roomLbl);
        card.add(thumbWrap);
        card.add(detail);
        card.add(editBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(cancelBtn);

        lookupResultPanel.add(card);
        lookupResultPanel.revalidate();
        lookupResultPanel.repaint();
    }

    // =========================================================
    // SCREEN 6 — ORDER (フード・ドリンク注文)
    // =========================================================
    private JPanel buildOrderScreen() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_WHITE);

        JButton backBtn = ghostButton("← 戻る");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> goTo(screenBeforeOrder));

        JLabel title = new JLabel("FOOD SELECT");
        title.setFont(FONT_H1);
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel sub = new JLabel("深夜ラジオのプレイリスト感覚で、フードとドリンクを横スクロールで選べる構成にしています");
        sub.setFont(FONT_BODY);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(4, 0, 16, 0));

        header.add(backBtn);
        header.add(title);
        header.add(sub);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_WHITE);

        orderSpinners.clear();

        Map<String, List<MenuItem>> groupedItems = new LinkedHashMap<>();
        groupedItems.put("ドリンク", new ArrayList<>());
        groupedItems.put("フード", new ArrayList<>());
        groupedItems.put("デザート", new ArrayList<>());

        for (MenuItem item : db.getMenuItems()) {
            groupedItems.computeIfAbsent(item.category, k -> new ArrayList<>()).add(item);
        }

        content.add(buildMenuCategorySection(
        	    "DRINK STATION",
        	    "まずは乾杯。ネオンバーのようにドリンクを並べて確認できます",
        	    groupedItems.get("Drink")
        	));

        	content.add(Box.createVerticalStrut(18));

        	content.add(buildMenuCategorySection(
        	    "FOOD STAGE",
        	    "人気の軽食・ごはんものを、ラジオ番組の選曲一覧のように比較できます",
        	    groupedItems.get("Food")
        	));

        	content.add(Box.createVerticalStrut(18));

        	content.add(buildMenuCategorySection(
        	    "SWEET ENCORE",
        	    "食後やシメに選びやすいスイーツを、アンコール枠として配置しています",
        	    groupedItems.get("Dessert")
        	));
        	
        JScrollPane scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel summary = new JPanel(new BorderLayout());
        summary.setBackground(PANEL_GRAY);
        summary.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(14, 16, 14, 16)));
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        JLabel summaryLbl = new JLabel("注文合計金額");
        summaryLbl.setFont(FONT_BODY);
        summaryLbl.setForeground(TEXT_MUTED);
        orderTotalLabel = new JLabel("0円");
        orderTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        orderTotalLabel.setForeground(ACCENT);
        summary.add(summaryLbl, BorderLayout.WEST);
        summary.add(orderTotalLabel, BorderLayout.EAST);

        JButton confirmOrderBtn = accentButton("注文を確定する");
        confirmOrderBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmOrderBtn.addActionListener(e -> handleOrderSubmit());

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(BG_WHITE);
        bottom.setBorder(new EmptyBorder(14, 0, 0, 0));
        bottom.add(summary);
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(confirmOrderBtn);

        outer.add(header, BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        outer.add(bottom, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildMenuCategorySection(String categoryName, String description, List<MenuItem> items) {
        JPanel section = new JPanel(new BorderLayout(0, 12));
        section.setBackground(PANEL_GRAY);
        section.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(18, 20, 18, 20)));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PANEL_GRAY);

        JLabel title = new JLabel(categoryName);
        title.setFont(new Font("Yu Gothic UI", Font.BOLD, 19));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel(description + "  /  横スクロールで選択できます");
        sub.setFont(FONT_LABEL);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(5, 0, 0, 0));

        header.add(title);
        header.add(sub);

        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.X_AXIS));
        cards.setBackground(PANEL_GRAY);

        if (items != null) {
            for (MenuItem item : items) {
                cards.add(buildMenuItemCard(item));
                cards.add(Box.createHorizontalStrut(12));
            }
        }

        JScrollPane horizontalScroll = new JScrollPane(cards, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        horizontalScroll.setBorder(null);
        horizontalScroll.getViewport().setBackground(PANEL_GRAY);
        horizontalScroll.getHorizontalScrollBar().setUnitIncrement(20);
        horizontalScroll.setPreferredSize(new Dimension(0, MENU_CARD_H + 46));

        section.add(header, BorderLayout.NORTH);
        section.add(horizontalScroll, BorderLayout.CENTER);
        return section;
    }

    private JPanel buildMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(PANEL_ALT);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(14, 14, 14, 14)));
        card.setPreferredSize(new Dimension(MENU_CARD_W, MENU_CARD_H));
        card.setMinimumSize(new Dimension(MENU_CARD_W, MENU_CARD_H));
        card.setMaximumSize(new Dimension(MENU_CARD_W, MENU_CARD_H));

        JLabel thumb = imageLabel(item.imagePath, MENU_IMG_W, MENU_IMG_H);
        thumb.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(PANEL_ALT);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(PANEL_ALT);

        JLabel nameLbl = new JLabel(item.name);
        nameLbl.setFont(FONT_H2);
        nameLbl.setForeground(TEXT_DARK);

        JLabel categoryLbl = neonChip(item.category, new Color(0x23, 0x2C, 0x47), ACCENT_HOVER);

        topRow.add(nameLbl, BorderLayout.WEST);
        topRow.add(categoryLbl, BorderLayout.EAST);

        JLabel priceLbl = new JLabel(formatYen(item.price));
        priceLbl.setFont(new Font("Yu Gothic UI", Font.BOLD, 16));
        priceLbl.setForeground(ACCENT);
        priceLbl.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel captionLbl = new JLabel("ON AIR MENU  /  すぐ選べる人気メニュー");
        captionLbl.setFont(FONT_LABEL);
        captionLbl.setForeground(TEXT_MUTED);
        captionLbl.setBorder(new EmptyBorder(4, 0, 0, 0));

        body.add(topRow);
        body.add(priceLbl);
        body.add(captionLbl);
        body.add(Box.createVerticalGlue());

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        styleSpinner(qtySpinner);
        qtySpinner.setPreferredSize(new Dimension(92, 34));
        qtySpinner.addChangeListener(e -> updateOrderTotal());
        orderSpinners.put(item, qtySpinner);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PANEL_ALT);
        JLabel qtyLbl = new JLabel("SELECT QTY");
        qtyLbl.setFont(FONT_LABEL);
        qtyLbl.setForeground(TEXT_MUTED);
        footer.add(qtyLbl, BorderLayout.WEST);
        footer.add(qtySpinner, BorderLayout.EAST);

        card.add(thumb, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private void updateOrderTotal() {
        if (orderTotalLabel == null) return;
        int total = 0;
        for (Map.Entry<MenuItem, JSpinner> entry : orderSpinners.entrySet()) {
            int qty = (Integer) entry.getValue().getValue();
            total += entry.getKey().price * qty;
        }
        orderTotalLabel.setText(formatYen(total));
    }

    private void handleOrderSubmit() {
        List<String> lines = new ArrayList<>();
        List<OrderLine> orderLines = new ArrayList<>();
        int total = 0;
        for (Map.Entry<MenuItem, JSpinner> entry : orderSpinners.entrySet()) {

            int qty = (Integer) entry.getValue().getValue();

            if (qty > 0) {

                // 在庫チェック
                if (qty > entry.getKey().stock) {
                    JOptionPane.showMessageDialog(
                        this,
                        entry.getKey().name + " は在庫不足です。\n現在の在庫: " + entry.getKey().stock,
                        "注文エラー",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                lines.add(entry.getKey().name + " × " + qty 
                        + " = " + formatYen(entry.getKey().price * qty));

                orderLines.add(new OrderLine(entry.getKey(), qty));

                total += entry.getKey().price * qty;
            }
        }
        if (lines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "商品を1つ以上選択してください。", "注文エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String placedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Order order = new Order(db.nextOrderId(), orderLines, total, placedAt);
        db.addOrder(order);
        
        MenuDAO menuDAO = new MenuDAO();

        for (OrderLine line : orderLines) {
            menuDAO.updateStock(
                line.item.id,
                line.qty
            );
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ご注文ありがとうございます！（注文番号 #").append(order.id).append("）\n\n");
        for (String l : lines) sb.append(l).append("\n");
        sb.append("\n合計金額: ").append(formatYen(total));
        JOptionPane.showMessageDialog(this, sb.toString(), "注文確定", JOptionPane.INFORMATION_MESSAGE);

        for (JSpinner s : orderSpinners.values()) s.setValue(0);
        updateOrderTotal();
        goTo(screenBeforeOrder);
    }

    // =========================================================
    // SCREEN 7 — ORDER HISTORY（注文履歴）
    // =========================================================
    private JPanel buildOrderHistoryScreen() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_WHITE);

        JButton backBtn = ghostButton("← 戻る");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> goTo("MENU"));

        JLabel title = new JLabel("注文履歴");
        title.setFont(FONT_H1);
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 0, 16, 0));

        header.add(backBtn);
        header.add(title);

        orderHistoryContainer = new JPanel();
        orderHistoryContainer.setLayout(new BoxLayout(orderHistoryContainer, BoxLayout.Y_AXIS));
        orderHistoryContainer.setBackground(BG_WHITE);

        JScrollPane scroll = new JScrollPane(orderHistoryContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);

        outer.add(header, BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    private void refreshOrderHistory() {
        if (orderHistoryContainer == null) return;
        orderHistoryContainer.removeAll();

        List<Order> orders = db.getOrders();
        if (orders.isEmpty()) {
            JLabel empty = new JLabel("まだご注文はありません。");
            empty.setFont(FONT_BODY);
            empty.setForeground(TEXT_MUTED);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            orderHistoryContainer.add(empty);
        } else {
            for (int i = orders.size() - 1; i >= 0; i--) {
                orderHistoryContainer.add(buildOrderHistoryCard(orders.get(i)));
                orderHistoryContainer.add(Box.createVerticalStrut(10));
            }
        }
        orderHistoryContainer.revalidate();
        orderHistoryContainer.repaint();
    }

    private JPanel buildOrderHistoryCard(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_GRAY);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(14, 16, 14, 16)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel head = new JLabel("注文番号 #" + order.id + "　" + order.placedAt);
        head.setFont(FONT_H2);
        head.setForeground(TEXT_DARK);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);

        StringBuilder sb = new StringBuilder("<html>");
        for (OrderLine line : order.lines) {
            sb.append(escape(line.item.name)).append(" × ").append(line.qty)
              .append(" = ").append(formatYen(line.item.price * line.qty)).append("<br>");
        }
        sb.append("</html>");
        JLabel linesLbl = new JLabel(sb.toString());
        linesLbl.setFont(FONT_BODY);
        linesLbl.setForeground(TEXT_MUTED);
        linesLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        linesLbl.setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel totalLbl = new JLabel("合計: " + formatYen(order.total));
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLbl.setForeground(ACCENT);
        totalLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(head);
        card.add(linesLbl);
        card.add(totalLbl);
        return card;
    }

    private void refreshBookingsTable() {
        if (bookingsTableModel == null) return;
        bookingsTableModel.setRowCount(0);
        for (Booking b : db.getBookings()) {
            String hoursDisplay = b.durationHours + (b.freeTime ? "（フリータイム）" : "");
            bookingsTableModel.addRow(new Object[]{
                    b.id, b.room.name, b.date, b.time, hoursDisplay, b.guests,
                    roomCostDisplayText(b), formatYen(b.guestFee), formatYen(b.totalPrice)
            });
        }
    }

    // =========================================================
    // STYLE HELPERS
    // =========================================================
    private JLabel labelFor(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 2, 6, 0));
        return l;
    }

    private JPanel fieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBackground(BG_WHITE);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        block.add(labelFor(labelText));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(180, 40));
        block.add(field);
        return block;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        field.setBackground(PANEL_ALT);
        field.setForeground(TEXT_MUTED);
        field.setText(placeholder);
        field.setCaretColor(ACCENT_HOVER);
        field.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(8, 12, 8, 12)));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
                field.setBorder(new CompoundBorder(new LineBorder(ACCENT_HOVER, 1, true), new EmptyBorder(8, 12, 8, 12)));
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_MUTED);
                }
                field.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(8, 12, 8, 12)));
            }
        });
        return field;
    }

    // ★追加: フィールドに入力できる文字を1文字単位の正規表現で制限する（貼り付け・setTextも含めて全て通る）
    //          プレースホルダ文字列もこの制限に適合させておくこと（例: カタカナ制限ならプレースホルダもカタカナのみにする）
    private void restrictInputToCharPattern(JTextField field, String allowedCharRegex) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs) throws BadLocationException {
                super.insertString(fb, offset, filterChars(text, allowedCharRegex), attrs);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                super.replace(fb, offset, length, filterChars(text, allowedCharRegex), attrs);
            }
        });
    }

    private String filterChars(String text, String allowedCharRegex) {
        if (text == null || text.isEmpty()) return "";
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (String.valueOf(c).matches(allowedCharRegex)) sb.append(c);
        }
        return sb.toString();
    }

    // クリックでカレンダーを開く、直接入力不可の日付表示フィールド
    private JTextField dateDisplayField(String placeholder) {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        field.setBackground(PANEL_ALT);
        field.setForeground(TEXT_MUTED);
        field.setText(placeholder);
        field.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(8, 12, 8, 12)));
        field.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return field;
    }

    // 営業時間 (10:00〜翌4:00開始, 30分刻み) の選択肢を生成
    private String[] buildBusinessHourSlots() {
        List<String> slots = new ArrayList<>();
        for (int h = 10; h <= 23; h++) {
            slots.add(String.format("%02d:00", h));
            slots.add(String.format("%02d:30", h));
        }
        for (int h = 0; h <= 4; h++) {
            slots.add(String.format("%02d:00", h));
            if (h < 4) slots.add(String.format("%02d:30", h));
        }
        return slots.toArray(new String[0]);
    }

    // 開始時刻(HH:MM)＋利用時間が営業時間(10:00〜翌5:00)内に収まっているか判定
    private boolean isWithinBusinessHours(String time, int durationHours) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int startExt = (hour < 10 ? hour + 24 : hour) * 60 + minute; // 0〜9時は翌日扱いに変換
        int endExt = startExt + durationHours * 60;
        return startExt >= BUSINESS_OPEN_MIN && endExt <= BUSINESS_CLOSE_MIN;
    }

    // カレンダーポップアップを表示し、選択した日付をtargetFieldにセットする
    private void showDatePicker(JTextField targetField, Component invoker) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(BG_WHITE);
        popup.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(10, 10, 10, 10)));

        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setBackground(BG_WHITE);
        container.setPreferredSize(new Dimension(260, 250));

        LocalDate today = LocalDate.now();
        YearMonth[] current = { YearMonth.from(today) };
        String existing = targetField.getText();
        if (existing.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            current[0] = YearMonth.from(LocalDate.parse(existing));
        }

        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(FONT_H2);
        monthLabel.setForeground(TEXT_DARK);

        JPanel daysGrid = new JPanel(new GridLayout(0, 7, 4, 4));
        daysGrid.setBackground(BG_WHITE);

        Runnable[] render = new Runnable[1];
        render[0] = () -> {
            monthLabel.setText(current[0].getYear() + "年 " + current[0].getMonthValue() + "月");
            daysGrid.removeAll();

            String[] weekLabels = {"月", "月", "火", "水", "木", "金", "金"};
            for (String w : weekLabels) {
                JLabel wl = new JLabel(w, SwingConstants.CENTER);
                wl.setFont(FONT_LABEL);
                wl.setForeground(TEXT_MUTED);
                daysGrid.add(wl);
            }

            LocalDate firstOfMonth = current[0].atDay(1);
            int leadingBlanks = firstOfMonth.getDayOfWeek().getValue() % 7; // 日曜=0始まりに変換
            for (int i = 0; i < leadingBlanks; i++) {
                daysGrid.add(new JLabel(""));
            }

            int daysInMonth = current[0].lengthOfMonth();
            for (int d = 1; d <= daysInMonth; d++) {
                LocalDate date = current[0].atDay(d);
                JButton dayBtn = new JButton(String.valueOf(d));
                dayBtn.setFont(FONT_BODY);
                dayBtn.setFocusPainted(false);
                dayBtn.setBorder(new EmptyBorder(6, 4, 6, 4));

                if (date.isBefore(today)) {
                    dayBtn.setEnabled(false);
                    dayBtn.setForeground(TEXT_MUTED);
                    dayBtn.setBackground(BG_WHITE);
                } else {
                    dayBtn.setBackground(date.isEqual(today) ? PANEL_GRAY : BG_WHITE);
                    dayBtn.setForeground(TEXT_DARK);
                    dayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    dayBtn.addActionListener(e -> {
                        targetField.setText(date.toString());
                        targetField.setForeground(TEXT_DARK);
                        targetField.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(8, 12, 8, 12)));
                        popup.setVisible(false);
                        updateTotal();
                    });
                }
                daysGrid.add(dayBtn);
            }
            daysGrid.revalidate();
            daysGrid.repaint();
        };

        JButton prevBtn = ghostButton("‹");
        JButton nextBtn = ghostButton("›");
        prevBtn.addActionListener(e -> { current[0] = current[0].minusMonths(1); render[0].run(); });
        nextBtn.addActionListener(e -> { current[0] = current[0].plusMonths(1); render[0].run(); });

        JPanel navRow = new JPanel(new BorderLayout());
        navRow.setBackground(BG_WHITE);
        navRow.add(prevBtn, BorderLayout.WEST);
        navRow.add(monthLabel, BorderLayout.CENTER);
        navRow.add(nextBtn, BorderLayout.EAST);

        render[0].run();

        container.add(navRow, BorderLayout.NORTH);
        container.add(daysGrid, BorderLayout.CENTER);
        popup.add(container);
        popup.show(invoker, 0, invoker.getHeight() + 4);
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Yu Gothic UI", Font.PLAIN, 14));
        field.setBackground(PANEL_ALT);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(ACCENT_HOVER);
        field.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(8, 12, 8, 12)));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
        spinner.getEditor().setBackground(PANEL_ALT);
        spinner.setBackground(PANEL_ALT);
        spinner.setForeground(TEXT_DARK);
        spinner.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(2, 6, 2, 6)));
    }

    private JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0xFF, 0x6B, 0xC6)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    private JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_DARK);
        btn.setBackground(PANEL_ALT);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(10, 16, 10, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0x24, 0x2E, 0x49)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(PANEL_ALT); }
        });
        return btn;
    }

    private String textOrEmpty(JTextField field, String placeholder) {
        String t = field.getText();
        return t.equals(placeholder) ? "" : t;
    }

    private void markValid(JTextField field, String placeholder, boolean valid) {
        Color c = valid ? BORDER_GRAY : ACCENT;
        field.setBorder(new CompoundBorder(new LineBorder(c, 1, true), new EmptyBorder(8, 12, 8, 12)));
    }

    // 日本円表記 (¥表記または円表記)
    private static String formatYen(int amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.JAPAN);
        return nf.format(amount) + "円";
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(ttt::new);
    }

    // =========================================================
    // MODEL CLASSES
    // =========================================================

    public static class Room {

        final int id;
        final String name;
        final String capacity;
        final int pricePerHour;
        final String imagePath;
        final int minGuests;
        final int maxGuests;

        public Room(
            int id,
            String name,
            String capacity,
            int pricePerHour,
            String imagePath,
            int minGuests,
            int maxGuests
        ) {
            this.id = id;
            this.name = name;
            this.capacity = capacity;
            this.pricePerHour = pricePerHour;
            this.imagePath = imagePath;
            this.minGuests = minGuests;
            this.maxGuests = maxGuests;
        }

        public String getName() {
            return name;
        }

        public int getPricePerHour() {
            return pricePerHour;
        }

        public int getMinGuests() {
            return minGuests;
        }

        public int getMaxGuests() {
            return maxGuests;
        }

        public String getCapacity() {
            return capacity;
        }
    }
    static class MenuItem {
        final int id;
        final String name;
        final int price;
        final String category;
        final String imagePath;
        final int stock;   // 追加

        MenuItem(
            int id,
            String name,
            int price,
            String category,
            String imagePath,
            int stock
        ) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
            this.imagePath = imagePath;
            this.stock = stock;
        }
    }

    static class OrderLine {
        final MenuItem item;
        final int qty;

        OrderLine(MenuItem item, int qty) {
            this.item = item;
            this.qty = qty;
        }
    }

    static class Order {
        final int id;
        final List<OrderLine> lines;
        final int total;
        final String placedAt;

        Order(int id, List<OrderLine> lines, int total, String placedAt) {
            this.id = id;
            this.lines = lines;
            this.total = total;
            this.placedAt = placedAt;
        }
    }

    static class Booking {
        final int id;
        final Room room;
        final String customerName;
        final String phone;
        final String date;
        final String time;
        final int durationHours;
        final int guests;
        final boolean freeTime;    // ★追加: フリータイム適用の有無
        final int roomCost;        // ★追加: 室料金（フリータイム時は頭打り後の額）
        final int guestFee;        // ★追加: 人数料金
        final int totalPrice;
        String status = "CONFIRMED";

        Booking(int id, Room room, String customerName, String phone, String date, String time,
                int durationHours, int guests,
                boolean freeTime, int roomCost, int guestFee, int totalPrice) {
            this.id = id;
            this.room = room;
            this.customerName = customerName;
            this.phone = phone;
            this.date = date;
            this.time = time;
            this.durationHours = durationHours;
            this.guests = guests;
            this.freeTime = freeTime;
            this.roomCost = roomCost;
            this.guestFee = guestFee;
            this.totalPrice = totalPrice;
        }
    }

    static class Database {
        private final List<Room> rooms = new ArrayList<>();
        private final List<Booking> bookings = new ArrayList<>();
        private final List<MenuItem> menuItems = new ArrayList<>();
        private final List<Order> orders = new ArrayList<>();
        private int bookingCounter = 1000;
        private int orderCounter = 5000;

        Database() {

            // DBから部屋情報取得
            RoomDAO roomDAO = new RoomDAO();

            ArrayList<Room> dbRooms = roomDAO.findAll();

            rooms.addAll(dbRooms);


            // DBからメニュー情報取得
            MenuDAO menuDAO = new MenuDAO();

            ArrayList<MenuItem> dbMenuItems = menuDAO.findAll();

            menuItems.addAll(dbMenuItems);

        }

        List<Room> getRooms() { return rooms; }

        List<MenuItem> getMenuItems() { return menuItems; }

        List<Booking> getBookings() {
            List<Booking> active = new ArrayList<>();
            for (Booking b : bookings) {
                if (!b.status.equals("CANCELLED")) active.add(b);
            }
            return active;
        }

        Booking findBookingById(int id) {
            for (Booking b : bookings) {
                if (b.id == id && !b.status.equals("CANCELLED")) return b;
            }
            return null;
        }

        void addBooking(Booking b) { bookings.add(b); }

        int nextBookingId() { return ++bookingCounter; }

        void cancelBooking(int id) {
            for (Booking b : bookings) {
                if (b.id == id) { b.status = "CANCELLED"; break; }
            }
        }

        void updateBooking(int id, Room room, String customerName, String phone, String date, String time,
                            int durationHours, int guests,
                            boolean freeTime, int roomCost, int guestFee, int totalPrice) {
            for (int i = 0; i < bookings.size(); i++) {
                Booking existing = bookings.get(i);
                if (existing.id == id) {
                    Booking updated = new Booking(id, room, customerName, phone, date, time,
                            durationHours, guests, freeTime, roomCost, guestFee, totalPrice);
                    updated.status = existing.status;
                    bookings.set(i, updated);
                    break;
                }
            }
        }

        int nextOrderId() { return ++orderCounter; }

        void addOrder(Order o) { orders.add(o); }

        List<Order> getOrders() { return orders; }
    }
}
