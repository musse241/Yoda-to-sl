package zelongames.yodatosl;

import java.io.Serializable;

public class Destination implements Serializable{
    String fromStation;
    String toStation;

    public void Destination(){

    }
    public void Destination(String fromStation, String toStation){
        setFromStation(fromStation);
        setToStation(toStation);
    }

    //SET

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }


    //GET

    public String getToStation() {
        return toStation;
    }

    public String getFromStation() {
        return fromStation;
    }
}