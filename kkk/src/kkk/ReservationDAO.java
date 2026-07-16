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
		    boolean freeTime,
		    int roomCost,
		    int guestFee,
		    int totalPrice
		) {

		String sql =
				"INSERT INTO reservations " +
				"(room_name, customer_name, reservation_date, reservation_time, usage_time, number_of_people, free_time, room_cost, guest_fee, total_price) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(5, usageTime);
            ps.setInt(6, people);
            ps.setBoolean(7, freeTime);
            ps.setInt(8, roomCost);
            ps.setInt(9, guestFee);
            ps.setInt(10, totalPrice);
            
            int result = ps.executeUpdate();

            conn.close();

            return result > 0;


        } catch(Exception e) {

            e.printStackTrace();
            return false;

        }
    }
}