package kkk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MenuDAO {

    public ArrayList<ttt.MenuItem> findAll() {

        ArrayList<ttt.MenuItem> menuItems = new ArrayList<>();

        String sql = "SELECT * FROM food_drink";

        try {

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	
            	

                ttt.MenuItem item = new ttt.MenuItem(
                	    rs.getInt("food_drink_id"),
                	    rs.getString("item_name"),
                	    rs.getInt("price"),
                	    rs.getString("category"),
                	    rs.getString("image_path")
                	);
                
               
                menuItems.add(item);
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuItems;
    }
}