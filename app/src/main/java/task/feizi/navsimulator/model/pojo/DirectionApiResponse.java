
package task.feizi.navsimulator.model.pojo;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.neshan.servicessdk.direction.model.Route;

public class DirectionApiResponse implements Parcelable
{

    @SerializedName("routes")
    @Expose
    private List<Route> routes = new ArrayList<Route>();
    public final static Creator<DirectionApiResponse> CREATOR = new Creator<DirectionApiResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public DirectionApiResponse createFromParcel(android.os.Parcel in) {
            return new DirectionApiResponse(in);
        }

        public DirectionApiResponse[] newArray(int size) {
            return (new DirectionApiResponse[size]);
        }

    }
    ;

    protected DirectionApiResponse(android.os.Parcel in) {
        in.readList(this.routes, (Route.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public DirectionApiResponse() {
    }

    /**
     * 
     * @param routes
     */
    public DirectionApiResponse(List<Route> routes) {
        super();
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeList(routes);
    }

    public int describeContents() {
        return  0;
    }

}
