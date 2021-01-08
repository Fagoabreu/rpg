package rpg.com.paifabio.graficos;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontBuilder {
	public static Font getFont(int size, boolean bold) {
		try {
			Font newFont=Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf"));
			if(bold) {
				return newFont.deriveFont(Font.BOLD,size);
			}else {
				return newFont.deriveFont(size);
			}
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
