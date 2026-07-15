package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MainMenuView extends JFrame {

    public MainMenuView() {

        setTitle("カラオケ予約システム");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 縦並びにする
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        JButton reserveButton = new JButton("予約");

        JButton searchButton = new JButton("空き状況確認");

        JButton confirmButton = new JButton("予約内容確認");

        JButton changeButton = new JButton("予約変更");

        JButton cancelButton = new JButton("予約キャンセル");

        JButton exitButton = new JButton("終了");


        add(reserveButton);
        add(searchButton);
        add(confirmButton);
        add(changeButton);
        add(cancelButton);
        add(exitButton);


        // 終了ボタン処理
        exitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                System.exit(0);

            }

        });


        // 予約ボタン処理（仮）
        reserveButton.addActionListener(e -> {

            new ReservationView();

        });


        // 空き状況確認（仮）
        searchButton.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "空き状況確認画面を開きます"
            );

        });


        setLocationRelativeTo(null);

        setVisible(true);

    }

}