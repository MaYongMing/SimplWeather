package app.com.cris.simplweather.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Cris on 2017/6/25.
 */

public class WeatherEntity  implements Parcelable{

    private String status;
    private String updateTime; //basic.loc
    private String cityName;
    private String cityId;
    private String curTmp;
    private String conCode;//now.cond.txt
    private String curCond; //now.cond.txt
    private String  aqi;
    private String pm2p5;
    private String airQlty;
    private String feelTmp;//now.cond.fl
    private String humidity;
    private String windDir;
    private String windLvl;

    private List<CityWeatherResponse.HeWeather5Bean.DailyForecastBean> dailyForecasts;
    private CityWeatherResponse.HeWeather5Bean.SuggestionBean mSuggestion;
public WeatherEntity(){

}
    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCurTmp() {
        return curTmp;
    }

    public void setCurTmp(String curTmp) {
        this.curTmp = curTmp;
    }

    public String getConCode() {
        return conCode;
    }

    public void setConCode(String conCode) {
        this.conCode = conCode;
    }

    public String getCurCond() {
        return curCond;
    }

    public void setCurCond(String curCond) {
        this.curCond = curCond;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getPm2p5() {
        return pm2p5;
    }

    public void setPm2p5(String pm2p5) {
        this.pm2p5 = pm2p5;
    }

    public String getAirQlty() {
        return airQlty;
    }

    public void setAirQlty(String airQlty) {
        this.airQlty = airQlty;
    }

    public String getFeelTmp() {
        return feelTmp;
    }

    public void setFeelTmp(String feelTmp) {
        this.feelTmp = feelTmp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getWindLvl() {
        return windLvl;
    }

    public void setWindLvl(String windLvl) {
        this.windLvl = windLvl;
    }

    public List<CityWeatherResponse.HeWeather5Bean.DailyForecastBean> getDailyForecasts() {
        return dailyForecasts;
    }

    public void setDailyForecasts(List<CityWeatherResponse.HeWeather5Bean.DailyForecastBean> dailyForecasts) {
        this.dailyForecasts = dailyForecasts;
    }

    public CityWeatherResponse.HeWeather5Bean.SuggestionBean getSuggestion() {
        return mSuggestion;
    }

    public void setSuggestion(CityWeatherResponse.HeWeather5Bean.SuggestionBean suggestion) {
        mSuggestion = suggestion;
    }

    @Override
    public String toString() {
        return cityName + "天气: "
                + "; 发布时间： " + updateTime
                + "; 天气状况： " + curCond
                + "; 温度： " + curTmp
                + "; 体感温度： " + feelTmp
                + "; 相对湿度： " + humidity
                + "; 空气质量： " + airQlty
                + "; 空气质量指数： " + aqi
                + "; PM2.5指数： " + pm2p5
                + "; 风向： " + windDir
                + "; 风力等级： " + windLvl
//                +"; \n舒适度指数: " + mSuggestion.getComf().getBrf()
//                + "; " + mSuggestion.getComf().getTxt()
//                + "; \n穿衣指数： " + mSuggestion.getDrsg().getBrf()
//                + "; " + mSuggestion.getDrsg().getTxt()
//                + "; \n感冒指数： " + mSuggestion.getFlu().getBrf()
//                + "; " + mSuggestion.getFlu().getTxt()
//                + "; \n运动指数： " + mSuggestion.getSport().getBrf()
//                + "; " + mSuggestion.getSport().getTxt()
//                + "; \n出游指数： " + mSuggestion.getTrav().getBrf()
//                + "; " + mSuggestion.getTrav().getTxt()
//                + "; \n紫外线指数： " + mSuggestion.getUv().getBrf()
//                + "; " + mSuggestion.getUv().getTxt()
//                + "; \n洗车指数： " + mSuggestion.getCw().getBrf()
//                + "; " + mSuggestion.getCw().getTxt()
                ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cityName);
        dest.writeString(this.airQlty);
        dest.writeString(this.aqi);
        dest.writeString(this.cityId);
        dest.writeString(this.conCode);
        dest.writeString(this.curCond);
        dest.writeString(this.curTmp);
        dest.writeString(this.feelTmp);
        dest.writeString(this.status);
        dest.writeString(this.humidity);
        dest.writeString(this.pm2p5);
        dest.writeString(this.updateTime);
        dest.writeString(this.windDir);
        dest.writeString(this.windLvl);
    }

    public static final Parcelable.Creator<WeatherEntity> CREATOR = new Creator<WeatherEntity>() {

        @Override
        public WeatherEntity createFromParcel(Parcel source) {
            WeatherEntity weatherEntity = new WeatherEntity(source);

            return weatherEntity;
        }

        @Override
        public WeatherEntity[] newArray(int size) {
            return new WeatherEntity[size];
        }
    };

    protected WeatherEntity(Parcel source){

        this.cityName = source.readString();
        this.airQlty = source.readString();
        this.aqi = source.readString();
        this.cityId = source.readString();
        this.conCode = source.readString();
        this.curCond = source.readString();
        this.curTmp = source.readString();
        this.feelTmp = source.readString();
        this.status = source.readString();
        this.humidity = source.readString();
        this.pm2p5 = source.readString();
        this.updateTime = source.readString();
        this.windDir = source.readString();
        this.windLvl = source.readString();
    }

}
