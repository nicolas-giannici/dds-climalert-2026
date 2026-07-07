package com.climalert.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherApiResponse {

    private Location location;
    private Current current;

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public Current getCurrent() { return current; }
    public void setCurrent(Current current) { this.current = current; }

    public static class Location {
        private String name;
        private String region;
        private String country;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }

    public static class Current {
        @JsonProperty("temp_c")
        private Double tempC;

        private Double humidity;

        @JsonProperty("last_updated")
        private String lastUpdated;

        public Double getTempC() { return tempC; }
        public void setTempC(Double tempC) { this.tempC = tempC; }

        public Double getHumidity() { return humidity; }
        public void setHumidity(Double humidity) { this.humidity = humidity; }

        public String getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
    }
}
