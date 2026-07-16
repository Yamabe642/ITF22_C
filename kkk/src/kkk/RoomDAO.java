package kkk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import kkk.ttt.Room;

public class RoomDAO {

    public ArrayList<Room> findAll() {

        ArrayList<Room> rooms = new ArrayList<>();

        String sql = "SELECT * FROM room";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();


            while (rs.next()) {

                String imagePath = "";

                String roomName = rs.getString("room_name");


                if (roomName.equals("スモールルーム")) {
                    imagePath = "images/ルーム画像/スモールルーム.png";

                } else if (roomName.equals("ミディアムルーム")) {
                    imagePath = "images/ルーム画像/ミディアムルーム.png";

                } else if (roomName.equals("VIPパーティールーム")) {
                    imagePath = "images/ルーム画像/パーティールーム.png";
                }


                ttt.Room room = new ttt.Room(
                    rs.getInt("room_id"),
                    roomName,
                    rs.getInt("min_capacity") + "〜"
                        + rs.getInt("max_capacity") + "名",
                    rs.getInt("price_per_hour"),
                    imagePath,
                    rs.getInt("min_capacity"),
                    rs.getInt("max_capacity")
                );

                rooms.add(room);
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }
}