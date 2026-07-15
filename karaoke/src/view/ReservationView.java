package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ReservationView extends JFrame {


    JTextField nameField;
    JTextField phoneField;
    JTextField dateField;

    JComboBox<String> timeBox;
    JComboBox<String> durationBox;
    JComboBox<Integer> peopleBox;
    JComboBox<String> drinkBox;
    JComboBox<String> machineBox;


    public ReservationView() {


        setTitle("カラオケ予約画面");

        setSize(450,600);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(8,2,10,10));


        // 名前
        panel.add(new JLabel("名前"));

        nameField = new JTextField();

        panel.add(nameField);



        // 電話番号
        panel.add(new JLabel("電話番号"));

        phoneField = new JTextField();

        panel.add(phoneField);



        // 利用日
        panel.add(new JLabel("利用日(yyyy-mm-dd)"));

        dateField = new JTextField();

        panel.add(dateField);



        // 開始時間
        panel.add(new JLabel("開始時間"));

        timeBox = new JComboBox<String>(
                new String[]{
                    "10:00",
                    "10:15",
                    "10:30",
                    "10:45",
                    "11:00",
                    "11:15",
                    "11:30",
                    "11:45",
                    "12:00",
                    "12:15",
                    "12:30",
                    "12:45",
                    "13:00",
                    "13:15",
                    "13:30",
                    "13:45",
                    "14:00",
                    "14:15",
                    "14:30",
                    "14:45",
                    "15:00",
                    "15:15",
                    "15:30",
                    "15:45",
                    "16:00",
                    "16:15",
                    "16:30",
                    "16:45",
                    "17:00",
                    "17:15",
                    "17:30",
                    "17:45",
                    "18:00",
                    "18:15",
                    "18:30",
                    "18:45",
                    "19:00",
                    "19:15",
                    "19:30",
                    "19:45",
                    "20:00",
                    "20:15",
                    "20:30",
                    "20:45",
                    "21:00",
                    "21:15",
                    "21:30",
                    "21:45",
                    "22:00",
                    "22:15",
                    "22:30",
                    "22:45",
                    "23:00",
                    "23:15",
                    "23:30",
                    "23:45",
                    "00:00",
                    "00:15",
                    "00:30",
                    "00:45",
                    "01:00",
                    "01:15",
                    "01:30",
                    "01:45",
                    "02:00",
                    "02:15",
                    "02:30",
                    "02:45",
                    "03:00",
                    "03:15",
                    "03:30",
                    "03:45",
                    "04:00"
                }
        );

        panel.add(timeBox);



        // 利用時間
        panel.add(new JLabel("利用時間"));

        durationBox = new JComboBox<String>(
                new String[]{
                    "1時間",
                    "2時間",
                    "3時間",
                    "4時間",
                    "5時間",
                    "フリータイム"
                }
        );

        panel.add(durationBox);



        // 利用人数
        panel.add(new JLabel("利用人数"));

        peopleBox = new JComboBox<Integer>(
                new Integer[]{
                    1,2,3,4,5,6,8,10
                }
        );

        panel.add(peopleBox);



        // 機種
        panel.add(new JLabel("機種"));

        machineBox = new JComboBox<String>(
                new String[]{
                    "DAM",
                    "JOYSOUND"
                }
        );

        panel.add(machineBox);



        // ドリンクバー
        panel.add(new JLabel("ドリンクバー"));

        drinkBox = new JComboBox<String>(
                new String[]{
                    "あり",
                    "なし"
                }
        );

        panel.add(drinkBox);



        // 登録ボタン
        JButton registerButton =
                new JButton("予約登録");



        registerButton.addActionListener(e -> {


            String message =
                    "予約内容\n\n"
                    + "名前：" + nameField.getText()
                    + "\n電話：" + phoneField.getText()
                    + "\n日付：" + dateField.getText()
                    + "\n開始：" + timeBox.getSelectedItem()
                    + "\n利用時間：" + durationBox.getSelectedItem()
                    + "\n人数：" + peopleBox.getSelectedItem()
                    + "\n機種：" + machineBox.getSelectedItem()
                    + "\nドリンクバー：" + drinkBox.getSelectedItem();



            JOptionPane.showMessageDialog(
                    this,
                    message + "\n\n予約登録しました"
            );


        });



        add(panel, BorderLayout.CENTER);

        add(registerButton, BorderLayout.SOUTH);



        setLocationRelativeTo(null);

        setVisible(true);

    }

}