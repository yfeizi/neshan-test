package task.feizi.navsimulator.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReverseApiResponse {
    @SerializedName("neighbourhood")
    @Expose
    private String neighbourhood;
    @SerializedName("municipality_zone")
    @Expose
    private String municipalityZone;
    @SerializedName("in_odd_even_zone")
    @Expose
    private Boolean inOddEvenZone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;

    public ReverseApiResponse() {
    }

    public ReverseApiResponse(String neighbourhood, String municipalityZone, Boolean inOddEvenZone, String address, String city, String state) {
        super();
        this.neighbourhood = neighbourhood;
        this.municipalityZone = municipalityZone;
        this.inOddEvenZone = inOddEvenZone;
        this.address = address;
        this.city = city;
        this.state = state;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getMunicipalityZone() {
        return municipalityZone;
    }

    public void setMunicipalityZone(String municipalityZone) {
        this.municipalityZone = municipalityZone;
    }

    public Boolean getInOddEvenZone() {
        return inOddEvenZone;
    }

    public void setInOddEvenZone(Boolean inOddEvenZone) {
        this.inOddEvenZone = inOddEvenZone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
