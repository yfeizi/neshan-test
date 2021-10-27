
package task.feizi.navsimulator.model.pojo;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Duration implements Parcelable
{

    @SerializedName("value")
    @Expose
    private Double value;
    @SerializedName("text")
    @Expose
    private String text;
    public final static Creator<Duration> CREATOR = new Creator<Duration>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Duration createFromParcel(android.os.Parcel in) {
            return new Duration(in);
        }

        public Duration[] newArray(int size) {
            return (new Duration[size]);
        }

    }
    ;

    protected Duration(android.os.Parcel in) {
        this.value = ((Double) in.readValue((Double.class.getClassLoader())));
        this.text = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Duration() {
    }

    /**
     * 
     * @param text
     * @param value
     */
    public Duration(Double value, String text) {
        super();
        this.value = value;
        this.text = text;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(value);
        dest.writeValue(text);
    }

    public int describeContents() {
        return  0;
    }

}
