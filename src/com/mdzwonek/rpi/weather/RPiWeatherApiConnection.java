package com.mdzwonek.rpi.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RPiWeatherApiConnection {
	
	public static interface RPiWeatherApiResultListener {
		
		public void weatherApiUpdatedForToday(RPiWeather todayWeather);
		public void weatherApiUpdatedForWeek(List<RPiWeather> weather);
		
	}
	
	private final Thread thread;
	private long timeout;
	
	private double latitude;
	private double longitude;
	
	private final RPiWeatherApiResultListener listener;
		
	public RPiWeatherApiConnection(RPiWeatherApiResultListener listener, long timeout) {
		this.listener = listener;
		this.timeout = timeout;
		
		this.thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (true) {
						System.out.print("Making server call for today weather... ");
						String todayResponseJson = getResponseFromUrl(getTodayWeatherUrl());
						System.out.println("done!");
						System.out.print("Parsing results...");
						RPiWeather todayWeather = parseTodayWeatherJson(todayResponseJson);
						System.out.println("done!");
						System.out.print("Notifying listener...");
						if (RPiWeatherApiConnection.this.listener != null) {
							RPiWeatherApiConnection.this.listener.weatherApiUpdatedForToday(todayWeather);
						}
						System.out.println("done!");
						
						
						System.out.print("Making server call for weekly weather... ");
						String weeklyResponseJson = getResponseFromUrl(getWeeklyWeatherUrl());
						System.out.println("done!");
						System.out.print("Parsing results...");
						List<RPiWeather> weeklyWeather = parseWeeklyWeatherJson(weeklyResponseJson);
						System.out.println("done!");
						System.out.print("Notifying listener...");
						if (RPiWeatherApiConnection.this.listener != null) {
							RPiWeatherApiConnection.this.listener.weatherApiUpdatedForWeek(weeklyWeather);
						}
						
						System.out.println("done!");
						synchronized (this) {
							this.wait(RPiWeatherApiConnection.this.timeout);
						}
					}
				} catch (InterruptedException e) {
					System.err.println("Weather API connection thread interrupted. " + e);
				}
			}
			
		});
		this.thread.setPriority(Thread.MIN_PRIORITY);
	}
	
	public void startRefreshing() {
		thread.start();
	}
	
	private String getTodayWeatherUrl() {
		return "http://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + this.latitude + "&lon=" + this.longitude;
	}
	
	private String getWeeklyWeatherUrl() {
		return "http://api.openweathermap.org/data/2.5/forecast/daily?units=metric&cnt=7&lat=" + this.latitude + "&lon=" + this.longitude;
	}
	
	private String getResponseFromUrl(String urlString) {
		BufferedReader reader = null;
		try {
			URL serverUrl = new URL(urlString);
			URLConnection connection = serverUrl.openConnection();
			
	        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line;
	        StringBuffer resultBuffer = new StringBuffer();
	        while ((line = reader.readLine()) != null) {
	        	resultBuffer.append(line);
	        }
	        
	        return resultBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private RPiWeather parseTodayWeatherJson(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(new JSONTokener(jsonString));
			
			JSONArray weatherArray = jsonObject.getJSONArray("weather");
			String weatherType = weatherArray.getJSONObject(0).getString("main");
			String weatherDescription = weatherArray.getJSONObject(0).getString("description");
			String icon = getIcon(weatherType, weatherDescription);
			
			JSONObject temperature = jsonObject.getJSONObject("main");
			int temperatureNow = (int) temperature.getDouble("temp");
			int temperatureMin = (int) temperature.getDouble("temp_min");
			int temperatureMax = (int) temperature.getDouble("temp_max");
			
			return new RPiWeather(icon, 0, temperatureNow, temperatureMin, temperatureMax);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<RPiWeather> parseWeeklyWeatherJson(String jsonString) {
		try {			
			JSONObject jsonObject = new JSONObject(new JSONTokener(jsonString));
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			List<RPiWeather> result = new ArrayList<RPiWeather>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject dayJsonObject = jsonArray.getJSONObject(i);
				
				long timestamp  = dayJsonObject.getLong("dt");
		        
				JSONArray weatherArray = dayJsonObject.getJSONArray("weather");
				String weatherType = weatherArray.getJSONObject(0).getString("main");
				String weatherDescription = weatherArray.getJSONObject(0).getString("description");
				String icon = getIcon(weatherType, weatherDescription);
				
				JSONObject temperature = dayJsonObject.getJSONObject("temp");
				int temperatureMin = (int) temperature.getDouble("min");
				int temperatureMax = (int) temperature.getDouble("max");
				
				result.add(new RPiWeather(icon, timestamp, 0, temperatureMin, temperatureMax));
			}
			return result;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getIcon(String weatherType, String weatherDescription) {
		if ("Clouds".equalsIgnoreCase(weatherType)) {
			if ("sky is clear".equalsIgnoreCase(weatherDescription)) {
				return "img/sunny-icon.png";
			} else if ("few clouds".equalsIgnoreCase(weatherDescription)) {
				return "img/mostly-sunny-icon.png";
			} else {
				return "img/cloudy-icon.png";
			}
		} else if ("Clear".equalsIgnoreCase(weatherType)) {
			return "img/sunny-icon.png";
		} else if ("Rain".equalsIgnoreCase(weatherType)) {
			if ("light intensity shower rain".equalsIgnoreCase(weatherDescription) || "shower rain".equalsIgnoreCase(weatherDescription)
					|| "heavy intensity shower rain".equalsIgnoreCase(weatherDescription)) {
				return "img/shower-icon.png";
			} else {
				return "img/raining-icon.png";
			}
		} else if ("Drizzle".equalsIgnoreCase(weatherType)) {
			return "img/raining-icon.png";
		} else if ("Thunderstorm".equalsIgnoreCase(weatherType)) {
			return "img/thunder-storm-icon.png";
		} else if ("Snow".equalsIgnoreCase(weatherType)) {
			return "img/snowing-icon.png";
		} else if ("Extreme".equalsIgnoreCase(weatherType) && "windy".equalsIgnoreCase(weatherDescription)) {
			return "img/windy-icon.png";
		}  else if ("Mist".equalsIgnoreCase(weatherType)) {
			return "img/mist-icon.png";
		} else {
			System.out.println("Cannot assign icon for " + weatherType + ", " + weatherDescription);
			return "";
		}
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
}
