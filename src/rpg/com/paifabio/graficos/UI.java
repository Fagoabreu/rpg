package rpg.com.paifabio.graficos;

import java.awt.Color;
import java.awt.Graphics;

import rpg.com.paifabio.entity.Player;
import rpg.com.paifabio.main.Game;

public class UI {
	private int scale;
	
	public UI(int scale) {
		this.scale =scale;
	}
	
	public void renderImages (Graphics g) {
		Player p = Game.getGame().getPlayer();
		g.setColor(Color.gray);
		g.drawRect(7, 3, (int)p.getMaxLife()+1, 9);
		g.setColor(Color.red);
		g.fillRect(8, 4, (int)p.getLife(), 8);
		
	}
	
	public void renderTexts (Graphics g) {
		Player p = Game.getGame().getPlayer();
		g.setColor(Color.white);
		g.setFont(FontBuilder.getFont(16*scale, true));
		g.drawString(((int)p.getLife())+" / "+ ((int)p.getMaxLife()), 9*scale, 11*scale);
		g.drawString("Munição: " + p.getAmmo() + "/" + p.getMaxAmmo(), 9*scale, (Game.getGame().HEIGHT -6) * scale);
		
		if(Game.getGame().enableDebug) {
			g.drawString("FPS:" + Game.getGame().curFPS, (Game.getGame().WIDTH-40)*scale, 11*scale);
			g.drawString("Debug: ON", (Game.getGame().WIDTH-60)*scale, (Game.getGame().HEIGHT-6)*scale);
		}
		
	}

}
