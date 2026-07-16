package kkk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class RoomDAO {


    // 部屋一覧取得
    public ArrayList<ttt.Room> findAll() {

        ArrayList<ttt.Room> rooms = new ArrayList<>();

        String sql = "SELECT * FROM room";


        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ResultSet rs =
                ps.executeQuery();


            while(rs.next()) {

                ttt.Room room = new ttt.Room(
                    rs.getInt("room_id"),
                    rs.getString("room_name"),
                    rs.getInt("min_capacity")
                    + "〜"
                    + rs.getInt("max_capacity")
                    + "名",
                    rs.getInt("price_per_hour"),
                    rs.getString("image_path"),
                    rs.getInt("min_capacity"),
                    rs.getInt("max_capacity")
                );


                rooms.add(room);
            }


            conn.close();


        } catch(Exception e) {

            e.printStackTrace();

        }


        return rooms;
    }

    public boolean checkStock(int roomId) {

        String sql =
            "SELECT stock FROM room WHERE room_id = ?";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setInt(1, roomId);

            ResultSet rs = ps.executeQuery();


            if(rs.next()) {

                int stock = rs.getInt("stock");

                if(stock > 0) {
                    return true;
                }
            }

            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 部屋在庫を1減らす
    public void updateStock(int roomId) {

        String sql =
            "UPDATE room SET stock = stock - 1 WHERE room_id = ?";


        try {

            Connection conn =
                DBConnection.getConnection();


            PreparedStatement ps =
                conn.prepareStatement(sql);


            ps.setInt(1, roomId);


            ps.executeUpdate();


            conn.close();


            System.out.println(
                "部屋在庫更新成功 ID=" + roomId
            );


        } catch(Exception e) {

            e.printStackTrace();

        }
    }
}