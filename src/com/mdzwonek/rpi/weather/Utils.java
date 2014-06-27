package com.mdzwonek.rpi.weather;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.security.CodeSource;

public class Utils {
	
	public static Font getDefaultFont() {
		return Utils.getTTFFontFromFile("font/HelveticaNeue.ttf");
	}
	
	public static Font getUltraLightFont() {
		return Utils.getTTFFontFromFile("font/HelveticaNeueUltraLight.ttf");
	}
	
	public static Font getTTFFontFromFile(String path) {
		try {
			CodeSource codeSource = Utils.class.getProtectionDomain().getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			String directory = jarFile.getParentFile().getPath();
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File(directory + "/" + path));
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			graphicsEnvironment.registerFont(font);
			return font;
		} catch (Exception e) {
			System.err.println("Cannot create font. " + e);
			return null;
		}
	}

}
