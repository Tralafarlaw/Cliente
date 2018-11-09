package tralafarlaw.miguel.find;

public class User {
    private String email;
    private double lat;
    private double lon;
    private boolean visible;

    public User() {
        email = null;
        setLat(0.0);
        setLon(0.0);
        visible = true;
    }

    public User(String email, double longitude, double latitude, boolean visible){
        this.email = email;
        this.lat = latitude;
        this.lon = longitude;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
