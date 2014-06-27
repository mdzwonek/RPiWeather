package com.mdzwonek.rpi.weather;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.time.DateUtils;

public class RPiWeatherMainFrame extends JFrame {

	private static final long serialVersionUID = -3892656450363354201L;
	
	private RPiResizableImageView backgroundImageView;
	
	private final JLabel locationLabel;
	
	private final RPiResizableImageView weatherIconLabel;
	private final JLabel temperatureLabel;
	
	private final JPanel nextDaysPanel;
	private final ArrayList<RPiResizableImageView> nextDaysImageViews;
	private final ArrayList<JLabel> nextDaysLabels;
	
	public RPiWeatherMainFrame() {
	    super();
	    
	    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    this.setUndecorated(true);
	    
	    this.setLayout(null);
	    
	    this.locationLabel = new JLabel();
	    this.locationLabel.setForeground(Color.white);
	    this.locationLabel.setFont(Utils.getDefaultFont());
	    this.locationLabel.setOpaque(false);
	    this.locationLabel.setBackground(new Color(0, 0, 0, 0));
	    this.add(this.locationLabel);
	    
	    this.weatherIconLabel = new RPiResizableImageView();
	    this.weatherIconLabel.setOpaque(false);
	    this.add(this.weatherIconLabel);
	    
	    this.temperatureLabel = new JLabel();
	    this.temperatureLabel.setForeground(Color.white);
	    this.temperatureLabel.setFont(Utils.getUltraLightFont());
	    this.add(this.temperatureLabel);
	    
	    
	    this.nextDaysPanel = new JPanel();
	    this.nextDaysPanel.setOpaque(false);
	    this.nextDaysPanel.setLayout(new GridLayout(1, 6));
	    this.nextDaysImageViews = new ArrayList<RPiResizableImageView>(6);
	    this.nextDaysLabels = new ArrayList<JLabel>(6);
	    for (int i = 0; i < 6; i++) {
	    	JPanel panel = new JPanel(new GridLayout(2, 1));
	    	panel.setOpaque(false);
	    	
	    	RPiResizableImageView imageView = new RPiResizableImageView();
	    	imageView.setOpaque(false);
	    	panel.add(imageView);
	    	this.nextDaysImageViews.add(imageView);
	    	
	    	JLabel label = new JLabel();	    	
	    	label.setForeground(Color.white);
	    	label.setFont(Utils.getDefaultFont());
	    	panel.add(label);
	    	this.nextDaysLabels.add(label);
	    	
	    	nextDaysPanel.add(panel);
	    }
	    this.add(this.nextDaysPanel);
	    
	    this.backgroundImageView = new RPiResizableImageView();
	    this.backgroundImageView.setFill();
	    this.add(this.backgroundImageView);
	    	    
	    this.addComponentListener(this.componentAdapter);
	}
	
	private final ComponentAdapter componentAdapter = new ComponentAdapter() {
		
		public void componentResized(ComponentEvent event) {
			RPiWeatherMainFrame.this.frameResized();
		}
		
		public void componentMoved(ComponentEvent event) {
			RPiWeatherMainFrame.this.frameResized();
		}
		
	};
	
	private void frameResized() {
		double width = this.getSize().getWidth();
		double height = this.getSize().getHeight();
		
		int mainComponentsWidth = (int) (width / 2);
		int mainComponentsHeight = (int) (0.65 * height);
		
		this.backgroundImageView.setBounds(0, 0, (int) width, (int) height);
		this.locationLabel.setBounds((int) (0.3 * width), (int) (0.05 * height), (int) (0.7 * width), (int) (0.15 * height));
		this.weatherIconLabel.setBounds(0, (int) (0.05 * height), mainComponentsWidth, mainComponentsHeight);
		this.temperatureLabel.setBounds(mainComponentsWidth, (int) (0.16 * height), mainComponentsWidth, mainComponentsHeight);
		this.nextDaysPanel.setBounds(0, (int) (0.66 * height), (int) width, (int) (0.4 * height));
		
		this.refreshLabelTextSize(this.locationLabel, 0.8f);
		this.refreshLabelTextSize(this.temperatureLabel, 0.55f);
		for (JLabel label : this.nextDaysLabels) {
			this.refreshLabelTextSize(label, 0.22f);
		}
	}
	
	public void setBackground(String background) {
		try {
			BufferedImage image = ImageIO.read(new File(background));
			this.backgroundImageView.setImageIcon(new ImageIcon(image));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setLocation(String location) {
		this.locationLabel.setText(location);
		this.locationLabel.setVerticalAlignment(SwingConstants.BOTTOM);
	    this.locationLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    this.refreshLabelTextSize(this.locationLabel, 0.8f);
	}
	
	public void setWeatherIcon(String pathToIcon) {
		try {
			BufferedImage image = ImageIO.read(new File(pathToIcon));
			this.weatherIconLabel.setImageIcon(new ImageIcon(image));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setTemperature(int temperature) {
		this.temperatureLabel.setText(temperature + "°C");
		this.temperatureLabel.setVerticalAlignment(SwingConstants.CENTER);
	    this.temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    this.refreshLabelTextSize(this.temperatureLabel, 0.55f);
	}
	
	public void setNextDayWeatherIcon(int index, String pathToIcon) {
		try {
			BufferedImage image = ImageIO.read(new File(pathToIcon));
			this.nextDaysImageViews.get(index).setImageIcon(new ImageIcon(image));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setNextDayWeatherTemperature(int index, long timestamp, int minTemperature, int maxTemperature) {
		final String day;
		Date date = new Date(1000 * timestamp);
		if (DateUtils.isSameDay(date, new Date())) {
			day = "Today";
		} else {
			Calendar calendar = Calendar.getInstance(); 
	        calendar.setTime(new Date(1000 * timestamp));
	        day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
		}
        
		JLabel label = this.nextDaysLabels.get(index);
		label.setText("<html><center>" + day + "<br><b>" + maxTemperature + "°C</b>" + " / " + minTemperature + "°C" + "</center></html>");
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setOpaque(false);
	    this.refreshLabelTextSize(label, 0.25f);
	}
	
	private void refreshLabelTextSize(JLabel label, float scale) {
		try {
			scale *= (float) label.getHeight();
			label.setFont(new Font(label.getFont().getName(), Font.PLAIN, (int) (scale)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
