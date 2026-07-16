package kkk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MenuDAO {

    public ArrayList<ttt.MenuItem> findAll() {

        ArrayList<ttt.MenuItem> menuItems = new ArrayList<>();

        String sql =
            "SELECT food_drink_id, item_name, price, category, image_path " +
            "FROM food_drink";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

            	ttt.MenuItem item = new ttt.MenuItem(
            		    rs.getInt("food_drink_id"),
            		    rs.getString("item_name"),
            		    rs.getInt("price"),
            		    rs.getString("category"),
            		    rs.getString("image_path"),
            		    rs.getInt("stock")
            		);

                menuItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuItems;
    }


    // 在庫更新
    public boolean updateStock(int foodDrinkId, int qty) {

        System.out.println(
            "在庫更新 ID=" + foodDrinkId + " 数=" + qty
        );

        String sql =
            "UPDATE food_drink " +
            "SET stock = stock - ? " +
            "WHERE food_drink_id = ? " +
            "AND stock >= ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, qty);
            ps.setInt(2, foodDrinkId);
            ps.setInt(3, qty);

            int result = ps.executeUpdate();

            // 更新成功ならtrue
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}