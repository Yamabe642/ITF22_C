package model;

import java.sql.Date;
import java.sql.Time;


public class Reservation {

    private int reservationId;
    private int roomId;
    private String name;
    private String phone;
    private Date reservationDate;
    private Time startTime;
    private int duration;
    private int people;
    private boolean drink;
    private String status;


    public int getReservationId() {
        return reservationId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getPeople() {
        return people;
    }

    public boolean isDrink() {
        return drink;
    }

    public String getStatus() {
        return status;
    }


    public void setRoomId(int roomId){
        this.roomId = roomId;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }
}