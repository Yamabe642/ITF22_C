package Customer;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class RoomDao {


    public ArrayList<Room> findAvailable(){

        ArrayList<Room> list=new ArrayList<>();


        String sql=
        "SELECT * FROM rooms WHERE available=1";


        try(Connection con=DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement(sql)){


            ResultSet rs=ps.executeQuery();


            while(rs.next()){


                Room room=new Room(
                    rs.getInt("room_id"),
                    rs.getInt("capacity"),
                    rs.getString("machine"),
                    rs.getBoolean("available")
                );


                list.add(room);
            }


        }catch(Exception e){

            e.printStackTrace();

        }


        return list;
    }

}