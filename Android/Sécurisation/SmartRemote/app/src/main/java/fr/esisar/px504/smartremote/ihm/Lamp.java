package fr.esisar.px504.smartremote.ihm;

/**
 * Created by valentin on 02/11/2017.
 */

public class Lamp {

    private String state;
    private int brightness;
    private String location;

    public Lamp(String location, String state, int brightness) {
        this.location = location;
        this.state = state;
        this.brightness = brightness;

    }


    public String getLocation() {
        return this.location;
    }

    public String getState() {
        return this.state;
    }

    public int getbrightness() {
        return brightness;
    }

}
