package com.mdzwonek.rpi.weather;

public class RPiWeather {
	
	private final String weatherIcon;
	
	private final long timestamp;
	
	private final int temperatureNow;
	private final int temperatureMin;
	private final int temperatureMax;

	public RPiWeather(String weatherIcon, long timestamp, int temperatureNow, int temperatureMin, int temperatureMax) {
		this.weatherIcon = weatherIcon;
		this.timestamp = timestamp;
		this.temperatureNow = temperatureNow;
		this.temperatureMin = temperatureMin;
		this.temperatureMax = temperatureMax;
	}
	
	public String getWeatherIcon() {
		return this.weatherIcon;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}

	public int getTemperatureNow() {
		return this.temperatureNow;
	}
	
	public int getTemperatureMin() {
		return this.temperatureMin;
	}

	public int getTemperatureMax() {
		return this.temperatureMax;
	}

}
