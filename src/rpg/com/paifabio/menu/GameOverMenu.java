package rpg.com.paifabio.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import rpg.com.paifabio.graficos.FontBuilder;
import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;

public class GameOverMenu {
	private int width,height,scale;
	private final String[] options = {
			"Reiniciar Jogo",
			"sair"};
	
	private int currentOption,maxOption;
	public boolean up,down,enter;
	
	public GameOverMenu(int width,int height,int scale) {
		this.width=width;
		this.height=height;
		this.scale = scale;
		this.up=false;
		this.down=false;
		this.enter=false;
		currentOption=0;
		maxOption=options.length-1;
		
	}
	
	public void tick() {
		if(up) {
			up=false;
			Sound.menu.play();
			currentOption--;
			if(currentOption<0) {
				currentOption=maxOption;
			}
		}else if(down) {
			down=false;
			Sound.menu.play();
			currentOption++;
			if(currentOption>maxOption) {
				maxOption=0;
			}
		}
		if(enter) {
			enter=false;
			Sound.menu.play();
			if(options[currentOption].equalsIgnoreCase("Reiniciar Jogo")) {
				Game.getGame().setInitGame();
			}else if(options[currentOption].equalsIgnoreCase("Sair")) {
				Game.getGame().setStartMenu();
			}
		}
		
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0, width*scale, height*scale);
		
		g2.setFont(FontBuilder.getFont(26*scale,true));
		g2.setColor(Color.black);
		g2.drawString("Game Over", ((width-60+1))/2*scale ,(50+1)*scale );
		g2.setColor(Color.yellow);
		g2.drawString("Game Over", ((width-60))/2*scale ,50*scale );

		
		for (int i=0;i<=maxOption;i++) {
			Color colorText;
			if(i==currentOption) {	
				colorText = Color.yellow;
				g2.setFont(FontBuilder.getFont(18*scale,true));
				g2.drawString("->", 25/2*scale ,(height/2+ i*10)*scale);
			}else {
				colorText = Color.white;
				g2.setFont(FontBuilder.getFont(14*scale,true));
			}
			g2.setColor(Color.black);
			g2.drawString(options[i], (60*scale/2)+2 ,(height/2 + i*10 +2)*scale);
			g2.setColor(colorText);
			g2.drawString(options[i], 60/2*scale ,(height/2+ i*10)*scale);
			
		}
	}
}
