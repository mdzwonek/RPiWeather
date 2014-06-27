package com.mdzwonek.rpi.weather;

import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class RPiResizableImageView extends JLabel {

	private static final long serialVersionUID = -4704469942056966608L;
	
	private ImageIcon imageIcon;
	private boolean fit;
	
	public RPiResizableImageView() {
		super();
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setVerticalAlignment(JLabel.CENTER);
		this.addComponentListener(this.componentAdapter);
		this.fit = true;
	}
	
	private final ComponentAdapter componentAdapter = new ComponentAdapter() {
		
		public void componentResized(ComponentEvent event) {
			RPiResizableImageView.this.refreshImageIconSize();
		}
		
		public void componentMoved(ComponentEvent event) {
			RPiResizableImageView.this.refreshImageIconSize();
		}
		
	};
	
	public void setImageIcon(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
		this.refreshImageIconSize();
	}
	
	private void refreshImageIconSize() {
		try {
			float newWidth = this.getWidth();
			float newHeight = this.getHeight();
			float iconWidth = this.imageIcon.getIconWidth();
			float iconHeight = this.imageIcon.getIconHeight();
			final float scale;
			if (this.fit) {
				scale = Math.min(newWidth / iconWidth, newHeight / iconHeight);
			} else {
				scale = Math.max(newWidth / iconWidth, newHeight / iconHeight);
			}
			int newIconWidth = (int) (scale * iconWidth);
			int newIconHeight = (int) (scale * iconHeight);
			Image image = this.imageIcon.getImage();
			Image scaledImage = image.getScaledInstance(newIconWidth, newIconHeight, Image.SCALE_SMOOTH);
			this.setIcon(new ImageIcon(scaledImage));
		} catch (Exception e) {
			this.setIcon(null);
		}
	}
	
	public void setFit() {
		this.fit = true;
	}
	
	public void setFill() {
		this.fit = false;
	}

}
