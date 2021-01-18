package rpg.com.paifabio.graficos;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontBuilder {
	
	private static Font pixelfont=initFont("pixelfont.ttf");
	
	private static Font initFont(String fontname) {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream(fontname));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Font getFont(int size, boolean bold) {
			if(bold) {
				return pixelfont.deriveFont(Font.BOLD,size);
			}else {
				return pixelfont.deriveFont(size);
			}
	}
}
