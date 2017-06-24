package app.com.cris.simplweather.model;

/**
 * Created by Cris on 2017/6/22.
 */

public class LocationEntity {
    private String address;
    private String province;
    private String city;
    private String district;
    private String street;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return "中国-" + province +" " +city + " " + district + " " +street +"; " +address;
    }
}
