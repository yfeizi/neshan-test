
package task.feizi.navsimulator.model.pojo;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step implements Parcelable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("instruction")
    @Expose
    private String instruction;
    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("polyline")
    @Expose
    private String polyline;
    @SerializedName("maneuver")
    @Expose
    private String maneuver;
    @SerializedName("start_location")
    @Expose
    private List<Double> startLocation = new ArrayList<Double>();
    public final static Creator<Step> CREATOR = new Creator<Step>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Step createFromParcel(android.os.Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return (new Step[size]);
        }

    }
    ;

    protected Step(android.os.Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.instruction = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((Distance) in.readValue((Distance.class.getClassLoader())));
        this.duration = ((Duration) in.readValue((Duration.class.getClassLoader())));
        this.polyline = ((String) in.readValue((String.class.getClassLoader())));
        this.maneuver = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.startLocation, (Double.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Step() {
    }

    /**
     * 
     * @param duration
     * @param distance
     * @param startLocation
     * @param instruction
     * @param name
     * @param polyline
     * @param maneuver
     */
    public Step(String name, String instruction, Distance distance, Duration duration, String polyline, String maneuver, List<Double> startLocation) {
        super();
        this.name = name;
        this.instruction = instruction;
        this.distance = distance;
        this.duration = duration;
        this.polyline = polyline;
        this.maneuver = maneuver;
        this.startLocation = startLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    public List<Double> getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(List<Double> startLocation) {
        this.startLocation = startLocation;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(instruction);
        dest.writeValue(distance);
        dest.writeValue(duration);
        dest.writeValue(polyline);
        dest.writeValue(maneuver);
        dest.writeList(startLocation);
    }

    public int describeContents() {
        return  0;
    }

}
