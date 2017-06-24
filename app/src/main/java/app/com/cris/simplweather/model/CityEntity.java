package app.com.cris.simplweather.model;

/**
 * Created by Cris on 2017/6/22.
 */

public class CityEntity {

    private int id;
    private String streetName;
    private String districtEngName;
    private String districtName;
    private String cityName;
    private String provinceName;
    private String cityId;

    public String getDistrictEngName() {
        return districtEngName;
    }

    public void setDistrictEngName(String districtEngName) {
        this.districtEngName = districtEngName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @Override
    public String toString() {
        return provinceName + " " + cityName + " " +districtName + " " + streetName +"; 城市代码： " +cityId;

    }
}
