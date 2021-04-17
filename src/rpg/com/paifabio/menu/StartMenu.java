package rpg.com.paifabio.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import rpg.com.paifabio.enums.SaveParameter;
import rpg.com.paifabio.graficos.FontBuilder;
import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;

public class StartMenu {

	private int width;
	private int height;
	private int scale;
	
	private final String[] options = {
			"novo jogo",
			"carregar jogo",
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
			}else if(options[currentOption].equalsIgnoreCase("carregar jogo")) {
				String saveString = loadGame(13);
				applySave(saveString);
			}else if(options[currentOption].equalsIgnoreCase("sair")) {
				System.exit(1);
			}
		}
	}
	
	public static void applySave(String str) {
		if (str==null || str.isEmpty()) {
			return;
		}
		String[] spl = str.split("/");
		for(int i = 0;i< spl.length;i++) {
			String[] spl2 = spl[i].split(":");
			switch(SaveParameter.valueOf(spl2[0])) {
			case LEVEL:
				Game.getGame().loadLevel(Integer.valueOf(spl2[1]));
				Game.getGame().setNormalGameState();
				break;
			case PLAYER_WEAPON:
				if(spl2[1].equalsIgnoreCase("1")) {
					Game.getGame().getPlayer().enableArma();
				}
				break;
			case PLAYER_LIFE:
				Game.getGame().getPlayer().setLife(Double.valueOf(spl2[1]));
				break;
			case PLAYER_AMMO:
				Game.getGame().getPlayer().addAmmo(Integer.valueOf(spl2[1]));
				break;
			case GAME_DIFICULT:
				Game.getGame().dificuldade =Integer.valueOf(spl2[1]);
				break;
			default:
				System.out.println("Parametro save '"+spl2[0]+"' n�o implementado");
				break;
			}
		}
		
	}
	
	public static String loadGame(int encode){
		File file = new File("save.txt");
		if(!file.exists()) {
			return null;
		}
		String line=new String();
		String singleLine =null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((singleLine=reader.readLine()) !=null) {
				String[] trans = singleLine.split(":");
				char[] val = trans[1].toCharArray();
				trans[1] = new String();
				for (int i = 0;i<val.length;i++) {
					val[i] -=encode;
					trans[1] +=val[i];
				}
				line+=trans[0];
				line+=":";
				line+=trans[1];
				line+="/";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return line;
	}
	
	public static void saveGame(SaveParameter []val1,int[] val2,int encode){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("save.txt"));
			for (int i=0;i<val1.length;i++) {
				String current =val1[i].toString();
				current+=":";
				char[] value = Integer.toString(val2[i]).toCharArray();
				
				for (int j =0; j<value.length;j++) {
					value[j]+=encode;
					current+=value[j];
				}
				bw.write(current);
				if(i<val1.length-1)
					bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render (Graphics g) {
		Graphics2D g2 =(Graphics2D) g;
		
		//imagem de fundo
		try {
			BufferedImage backImage = ImageIO.read(getClass().getResource("/menu.png"));
			int backWidth = backImage.getWidth();
			int backHeight = backImage.getHeight();
			g2.drawImage(backImage,0,0, backWidth*scale, backHeight*scale, null);
		} catch (IOException e) {
			System.out.println("N�o foi possivel localizar a imagem de fundo do menu");
			e.printStackTrace();
		}
		
		//escurecer background
		g2.setColor(new Color(0,0,0,50));
		g2.fillRect(0, 0, width*scale, height*scale);
		
		//titulo
		g2.setFont(FontBuilder.getFont(30*scale, true));
		g2.setColor(Color.black);
		g2.drawString("Torre das Caveiras", (width-180+2)/2*scale  ,(40+2)*scale );
		g2.setColor(Color.yellow);
		g2.drawString("Torre das Caveiras", (width-180)/2*scale ,(40)*scale);
		
		
		//rodap�
		g2.setFont(FontBuilder.getFont(8*scale, true));
		g2.setColor(Color.black);
		g2.drawString("Por Fabio Gomes de Abreu", (width-75)*scale+1  ,(height-15)*scale +1);
		g2.drawString("para game JAAJ V", (width-75)*scale+1 ,(height-10)*scale +1 );
		g2.drawString("Agradecimento a Danki Code", (width-75)*scale+1  ,(height-5)*scale +1 );
		
		g2.setColor(Color.white);
		g2.drawString("Por Fabio Gomes de Abreu", (width-75)*scale ,(height-15)*scale);
		g2.drawString("para game JAAJ V", (width-75)*scale ,(height-10)*scale);
		g2.drawString("Agradecimento a Danki Code", (width-75)*scale ,(height-5)*scale);
						
		//op��es
		for (int i=0;i<=maxOption;i++) {
			Color colorText;
			if(i==currentOption) {	
				colorText = Color.yellow;
				g2.setFont(FontBuilder.getFont(22*scale, true));
				g2.drawString("->", 25*scale/2 ,(height/2 + (i*10))*scale);
			}else {
				colorText = Color.white;
				g2.setFont(FontBuilder.getFont(16*scale, true));
			}
			g2.setColor(Color.black);
			g2.drawString(options[i], (60*scale/2)+2 ,(height/2 + (i*10)+1)*scale);
			g2.setColor(colorText);
			g2.drawString(options[i], (60*scale/2) ,(height/2 + i*10)*scale);
			
		}
	}
}
