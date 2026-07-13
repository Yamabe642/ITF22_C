package Customer;


public class ValidationUtil {


    public static boolean checkDuration(int duration){

        if(duration < 1 || duration > 5){
            return false;
        }

        return true;
    }


    public static boolean checkPeople(int people,int capacity){

        return people <= capacity;

    }

}