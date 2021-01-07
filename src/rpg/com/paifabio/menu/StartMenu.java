package rpg.com.paifabio.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;

public class StartMenu {

	private int width;
	private int height;
	private int scale;
	
	private final String[] options = {
			"novo jogo",
			//"carregar jogo",
			"sair"};
	private int currentOption=0;
	private final int maxOption = options.length-1;
	public boolean up,down,enter;
	
	public StartMenu(int width,int height,int scale) {
		this.width=width;
		this.height=height;
		this.scale=scale;
		Sound.musicBackground.loop();
		Sound.musicBackground.setVolume(80);
	}
	
	public void tick() {
		if(up) {
			up=false;
			Sound.menu.play();
			currentOption--;
			if(currentOption<0)
			{
				currentOption=maxOption;
			}
		}else if(down) {
			
			down=false;
			Sound.menu.play();
			currentOption++;
			if(currentOption>maxOption)
			{
				currentOption=0;
			}
		}
		
		if(enter) {
			enter=false;
			Sound.menu.play();
			if(options[currentOption].equalsIgnoreCase("novo jogo")) {
				Game.getGame().setInitGame();
				
			}else if(options[currentOption].equalsIgnoreCase("sair")) {
				System.exit(1);
			}
		}
		
	}
	
	public void render (Graphics g) {
		Graphics2D g2 =(Graphics2D) g;
		
		//imagem de fundo
		try {
			BufferedImage backImage = ImageIO.read(getClass().getResource("/menu.png"));
			int backWidth = backImage.getWidth();
			int backHeight = backImage.getHeight();
			g2.drawImage(backImage,0,0, backWidth*2*scale, backHeight*2*scale, null);
		} catch (IOException e) {
			System.out.println("Não foi possivel localizar a imagem de fundo do menu");
			e.printStackTrace();
		}
		
		//escurecer background
		g2.setColor(new Color(0,0,0,50));
		g2.fillRect(0, 0, width*scale, height*scale);
		
		//titulo
		g2.setFont(new Font("arial",Font.BOLD,16*scale));
		g2.setColor(Color.black);
		g2.drawString("Torre das Caveiras", (width-180)/2*scale +4 ,(40)*scale +4);
		g2.setColor(Color.yellow);
		g2.drawString("Torre das Caveiras", (width-180)/2*scale ,(40)*scale);
		
		
		//rodap�
		g2.setFont(new Font("arial",Font.BOLD,4*scale));
		g2.setColor(Color.black);
		g2.drawString("Por Fabio Gomes de Abreu", (width-70)*scale +1 ,(height-15)*scale +1);
		g2.drawString("para game JAAJ V", (width-70)*scale +1,(height-10)*scale +1);
		g2.drawString("Agradecimento a Danki Code", (width-70)*scale +1 ,(height-5)*scale +1);
		
		g2.setColor(Color.white);
		g2.drawString("Por Fabio Gomes de Abreu", (width-70)*scale ,(height-15)*scale);
		g2.drawString("para game JAAJ V", (width-70)*scale ,(height-10)*scale);
		g2.drawString("Agradecimento a Danki Code", (width-70)*scale ,(height-5)*scale);
						
		//opções
		for (int i=0;i<=maxOption;i++) {
			Color colorText;
			if(i==currentOption) {	
				colorText = Color.yellow;
				g2.setFont(new Font("arial",Font.BOLD,11*scale));
				g2.drawString("->", (25)*scale/2 ,((height )*scale/2)+ (i*30));
			}else {
				colorText = Color.white;
				g2.setFont(new Font("arial",Font.BOLD,8*scale));
			}
			g2.setColor(Color.black);
			g2.drawString(options[i], (60*scale/2)+2 ,((height )*scale/2)+ (i*30)+2);
			g2.setColor(colorText);
			g2.drawString(options[i], (60*scale/2) ,((height )*scale/2)+ (i*30));
			
		}
	}
}
