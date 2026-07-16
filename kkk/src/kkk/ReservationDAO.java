package kkk;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ReservationDAO {

	public boolean insert(
		    String roomName,
		    String customerName,
		    String date,
		    String time,
		    int usageTime,
		    int people,
		    int totalPrice
		) {

		String sql =
				"INSERT INTO reservations " +
				"(room_name, customer_name, reservation_date, reservation_time, usage_time, number_of_people, total_price) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, roomName);
            ps.setString(2, customerName);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setInt(5, usageTime);
            ps.setInt(6, people);
            ps.setInt(7, totalPrice);
            
            int result = ps.executeUpdate();

            conn.close();

            return result > 0;


        } catch(Exception e) {

            e.printStackTrace();
            return false;

        }
    }
}