package kkk;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ReservationDAO {

    public boolean insert(
            String roomName,
            String date,
            String time,
            int usageTime,
            int people,
            int totalPrice) {

        String sql =
            "INSERT INTO reservations " +
            "(room_name, reservation_date, reservation_time, " +
            "usage_time, number_of_people, total_price) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(1, roomName);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setInt(4, usageTime);
            ps.setInt(5, people);
            ps.setInt(6, totalPrice);

            int result = ps.executeUpdate();

            conn.close();

            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}