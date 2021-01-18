package rpg.com.paifabio.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import rpg.com.paifabio.graficos.FontBuilder;
import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;

public class PauseMenu {
	private int width,height,scale;
	
	private final String[] comandos = {
		"W A S D = Mover",
		"E ou MOUSE ESQUERDO = Atacar",
		"ESC = Parar Jogo",
		"ENTER = Interagir Menu",
		"DELETE = Modo Depuração"
		
	};
	private final String[] options = {
			"continue",
			"salvar jogo",
			"sair"};
	private int currentOption,maxOption;
	public boolean up,down,enter;
	
	public PauseMenu(int width,int height,int scale) {
		this.width=width;
		this.height=height;
		this.scale = scale;
		this.currentOption=0;
		this.maxOption=options.length-1;
		this.up=false;
		this.down=false;
		this.enter=false;
	}
	
	public void tick() {
		if(up==true) {
			up=false;
			Sound.menu.play();
			currentOption--;
			if(currentOption<0) {
				currentOption=maxOption;
			}
			
		}else if(down ==true) {
			down =false;
			Sound.menu.play();
			currentOption++;
			if(currentOption>maxOption) {
				currentOption=0;
			}
		}
		
		if(enter) {
			enter=false;
			Sound.menu.play();
			if(options[currentOption].equalsIgnoreCase("continue")) {
				Game.getGame().setNormalGameState();
			}else if(options[currentOption].equalsIgnoreCase("salvar jogo")) {
				Game.getGame().saveGame();
			}else if(options[currentOption].equalsIgnoreCase("sair")) {
				Game.getGame().setStartMenu();
			}
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0, width*scale, height*scale);
		g2.setFont(FontBuilder.getFont(26*scale, true));
		g2.setColor(Color.black);
		g2.drawString("Estatua!", (width-59)/2*scale ,(51)*scale );
		g2.setColor(Color.yellow);
		g2.drawString("Estatua!", (width-60)/2*scale ,(50)*scale );
		
		for (int i=0;i<=maxOption;i++) {
			Color colorText;
			if(i==currentOption) {	
				colorText = Color.yellow;
				g2.setFont(FontBuilder.getFont(22*scale, true));
				g2.drawString("->", 25*scale/2 ,(height/2 + i*10)*scale);
			}else {
				colorText = Color.white;
				g2.setFont(FontBuilder.getFont(16*scale, true));
			}
			g2.setColor(Color.black);
			g2.drawString(options[i], (60*scale/2)+2 ,(height/2 + i*10 +2)*scale);
			g2.setColor(colorText);
			g2.drawString(options[i], 60*scale/2 ,(height/2 + i*10)*scale);
			
		}
		
		for(int i =0; i<comandos.length;i++) {
			g2.setFont(FontBuilder.getFont(8*scale, true));
			g2.setColor(Color.black);
			g2.drawString(comandos[i], (width-80+1)*scale ,(height/2 + i*10+1)*scale);
			g2.setColor(Color.white);
			g2.drawString(comandos[i], (width-80)*scale ,(height/2 + i*10)*scale);
		}
	}
}
