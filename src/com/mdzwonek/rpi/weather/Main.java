package com.mdzwonek.rpi.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.CodeSource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import com.mdzwonek.rpi.weather.RPiWeatherApiConnection.RPiWeatherApiResultListener;

public class Main {

	private static final long SERVER_REFRESH_INTERVAL = TimeUnit.MINUTES.toMillis(15);
	
	private static final RPiWeatherMainFrame mainFrame = new RPiWeatherMainFrame();
	
	private static String directory = "";
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	try {
            		CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
					File jarFile = new File(codeSource.getLocation().toURI().getPath());
					directory = jarFile.getParentFile().getPath();
				
					File configFile = new File(directory + "/config");
					BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
	            	
	                mainFrame.setVisible(true);
	                mainFrame.setLocation(bufferedReader.readLine());
	                mainFrame.setBackground(directory + "/img/background.png");
	                
	                RPiWeatherApiConnection connection = new RPiWeatherApiConnection(weatherApiListener, SERVER_REFRESH_INTERVAL);
	                connection.setLatitude(Double.parseDouble(bufferedReader.readLine()));
	                connection.setLongitude(Double.parseDouble(bufferedReader.readLine()));
	                connection.startRefreshing();
	                
	                bufferedReader.close();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        });
	}
	
	private static final RPiWeatherApiResultListener weatherApiListener = new RPiWeatherApiResultListener() {

		@Override
		public void weatherApiUpdatedForToday(final RPiWeather todayWeather) {
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	mainFrame.setWeatherIcon(directory + "/" + todayWeather.getWeatherIcon());
			    	mainFrame.setTemperature(todayWeather.getTemperatureNow());
			    }
			});
		}

		@Override
		public void weatherApiUpdatedForWeek(final List<RPiWeather> weathers) {
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	for (int i = 0; i < weathers.size() - 1; i++) {
			    		RPiWeather weather = weathers.get(i);
			    		mainFrame.setNextDayWeatherIcon(i, directory + "/" + weather.getWeatherIcon());
			    		mainFrame.setNextDayWeatherTemperature(i, weather.getTimestamp(), weather.getTemperatureMin(), weather.getTemperatureMax());
			    	}
			    }
			});
		}
		

	};

}
