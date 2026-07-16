package kkk;

import java.util.ArrayList;

public class TestDB {

    public static void main(String[] args) {

        RoomDAO roomDao = new RoomDAO();

        ArrayList<ttt.Room> rooms = roomDao.findAll();

        for (ttt.Room r : rooms) {
            System.out.println(r.getName());
        }


        MenuDAO menuDao = new MenuDAO();

        ArrayList<ttt.MenuItem> menuItems = menuDao.findAll();

        for (ttt.MenuItem item : menuItems) {
            System.out.println(
                item.name + " " + item.price + "円"
            );
        }

        ReservationDAO reservationDao = new ReservationDAO();

        boolean result = reservationDao.insert(
        	    "スモールルーム",
        	    "ヤマダ",
        	    "2026-07-16",
        	    "18:00:00",
        	    2,
        	    3,
        	    3000
        	);

        System.out.println("予約登録：" + result);

        System.out.println("予約登録：" + result);
    }
}