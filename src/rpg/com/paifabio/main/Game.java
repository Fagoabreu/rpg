package rpg.com.paifabio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import rpg.com.paifabio.entity.BulletShoot;
import rpg.com.paifabio.entity.Enemy;
import rpg.com.paifabio.entity.Entity;
import rpg.com.paifabio.entity.Player;
import rpg.com.paifabio.enums.GameState;
import rpg.com.paifabio.enums.SaveParameter;
import rpg.com.paifabio.graficos.Spritesheet;
import rpg.com.paifabio.graficos.UI;
import rpg.com.paifabio.menu.GameOverMenu;
import rpg.com.paifabio.menu.PauseMenu;
import rpg.com.paifabio.menu.StartMenu;
import rpg.com.paifabio.statics.StaticValues;
import rpg.com.paifabio.world.World;

public class Game extends Canvas implements Runnable,KeyListener, MouseListener,MouseMotionListener{
	
	private static final long serialVersionUID = 1013703851778961047L;
	
	//singleton
	private static Game game;
	public JFrame frame;
	private Thread thread;
	private boolean isRunning;
	public final int WIDTH = 240;//240//320
	public final int HEIGHT = 160;//160//240//320
	private int SCALE = 3;
	private final boolean isFullScreen=false;
	public int curFPS =0;
	public boolean enableDebug=false;
	private boolean enableLight=true;
	private boolean enableRngMap=true;
	
	private int cur_level=1,max_level=7;
	private BufferedImage image;
	public List<Entity> entityList;
	public List<Enemy> enemyList;
	public List<BulletShoot> bulletList ;
	private Spritesheet spritesheet;
	public int dificuldade;
	
	public World world;
	
	private Player player;
	
	private UI ui;
	
	public StartMenu startMenu;
	public GameOverMenu gameOverMenu;
	public PauseMenu pauseMenu;
	
	public int[] pixels;
	public int[] lightMapPixels;
	private int[] miniMapPixels;
	
	public BufferedImage lightMap;
	private BufferedImage miniMap;
	private int minimapScale=2;
	
	
	//debug
	private int debugMouseX=0,debugMouseY=0;
	
	private GameState gameState=GameState.MENU;
	
	public static Game getGame() {
		if (game==null) {
			game= new Game();
		}
		return game;
	}
	
	public void setGameOver() {
		this.gameState=GameState.GAME_OVER;
		
		
	}
	
	public void setPauseGameState() {
		this.gameState=GameState.PAUSE;
		changeMousePointer("/blankCursor.png");
	}
	
	public void setNormalGameState() {
		this.gameState=GameState.NORMAL;
		if(player.hasArma()) {
			changeMousePointer("/crosshair.png");
		}else {
			changeMousePointer("/blankCursor.png");
		}
	}
	public void setStartMenu() {
		this.gameState=GameState.MENU;
		changeMousePointer("/blankCursor.png");
	}
	public void setInitGame() {
		loadLevel(1);
		setNormalGameState();
	}
	
	public void saveGame() {
		SaveParameter[] opt1= {
				SaveParameter.LEVEL,
				SaveParameter.PLAYER_WEAPON,
				SaveParameter.PLAYER_LIFE,
				SaveParameter.PLAYER_AMMO,
				SaveParameter.GAME_DIFICULT};
		int[] opt2= {
				this.cur_level,
				this.player.hasArma()?1:0,
				(int)this.player.getLife(),
				this.player.getAmmo(),
				this.dificuldade
				};
		StartMenu.saveGame(opt1, opt2, 13);
		System.out.println("Jogo salvo");
	}
	
	public Game() {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		if(isFullScreen){
			this.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
			//float scaleX = Toolkit.getDefaultToolkit().getScreenSize().width/WIDTH;
			float scaleY =Toolkit.getDefaultToolkit().getScreenSize().height/HEIGHT;
			SCALE = (int)scaleY;
		}else {
			this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		}
		initFrame();
		
		//inicializa objetos
		ui = new UI(SCALE);
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			lightMap = ImageIO.read(getClass().getResource("/lightmap.png"));
			lightMapPixels= new int[lightMap.getWidth()*lightMap.getHeight()];
			lightMap.getRGB(0, 0,lightMap.getWidth(),lightMap.getHeight(),lightMapPixels,0,lightMap.getWidth());
		}catch (IOException e) {
			System.out.println("Falha ao carregar o lightmap");
			e.printStackTrace();
		}
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		spritesheet = new Spritesheet("/spritesheet.png");
		startMenu = new StartMenu(WIDTH,HEIGHT,SCALE); 
		gameOverMenu = new GameOverMenu(WIDTH, HEIGHT, SCALE);
		pauseMenu = new  PauseMenu(WIDTH, HEIGHT, SCALE);
		
		this.loadLevel(1);
	}
	
	public void loadLevel(int mapNumer) {
		dificuldade=0;
		player = new Player(0, 0, 16, 16, spritesheet);
		player.setMask(2,0,12,16);
		loadLevel(player, mapNumer);
	}
	
	public void loadLevel(Player player,int mapNumer) {
		entityList = new ArrayList<Entity>();
		enemyList = new ArrayList<Enemy>();
		bulletList = new ArrayList<BulletShoot>();
		enableRngMap =mapNumer>max_level?true:false;
		enableLight = mapNumer>max_level*2?true:false;
		
		entityList.add(player);
		
		if(enableRngMap) {

			world = new World(
					spritesheet.getSpriteByPosition(0, 0),//floor
					spritesheet.getSpriteByPosition(1, 0),//wall
					//spritesheet.getSpriteByPosition(0, 1),//sky
					spritesheet.getSpriteByPosition(1, 1),//escada
					//spritesheet.getSpriteByPosition(7, 0),//weapon
					spritesheet.getSpriteByPosition(6, 1),//ammo
					spritesheet.getSpriteByPosition(6, 0),//lifepack
					spritesheet,
					entityList,
					enemyList,
					player,
					dificuldade,
					10
					);
		}else {
			world = new World("/map"+mapNumer +".png",
				spritesheet.getSpriteByPosition(0, 0),//floor
				spritesheet.getSpriteByPosition(1, 0),//wall
				spritesheet.getSpriteByPosition(0, 1),//sky
				spritesheet.getSpriteByPosition(1, 1),//escada
				spritesheet.getSpriteByPosition(7, 0),//weapon
				spritesheet.getSpriteByPosition(6, 1),//ammo
				spritesheet.getSpriteByPosition(6, 0),//lifepack
				spritesheet,
				entityList,
				enemyList,
				player,
				dificuldade
				);
		}
		
		miniMap = new BufferedImage(world.width, world.height, BufferedImage.TYPE_INT_ARGB);
		miniMapPixels = ((DataBufferInt)miniMap.getRaster().getDataBuffer()).getData();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void initFrame() {
		frame = new JFrame(StaticValues.gameName);
		frame.add(this);
		
		//icone janela
		Image imagem = null;
		try {
			imagem = ImageIO.read(getClass().getResource("/icon.png"));
			frame.setIconImage(imagem);
		}catch(IOException e) {
			e.printStackTrace();
		}
		//mouse pointer
		changeMousePointer("/blankCursor.png");
		
		//propriedades da janela
		frame.setUndecorated(true);//remove as bortdas da janela
		frame.setResizable(false);//n�o deixa redimensionar
		frame.pack();
		frame.setAlwaysOnTop(true);//deixa a janela sempre no topo
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//seta para finalizar a execu��o ao fechar a janela
		frame.setVisible(true);//mostra a janela
	}
	
	public void changeMousePointer(String imagem) {
		//mouse pointer
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursorImage = toolkit.getImage(getClass().getResource(imagem));
		Cursor cursor = toolkit.createCustomCursor(cursorImage,new Point(13,13), "img"); 
		frame.setCursor(cursor);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		this.isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		this.isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.out.println("Erro ao finalizar a thread principal do jogo");
			e.printStackTrace();
		}
	}
	
	
	public void tick() {	
		if (gameState==GameState.NORMAL) {
			for (int i=0;i<entityList.size();i++) {
				entityList.get(i).tick();
			}
			for (int i = 0; i<bulletList.size();i++) {
				bulletList.get(i).tick();
			}
			
			if(enemyList.size()==0) {
				cur_level++;
				loadLevel(player, cur_level);
			}
		}else if (gameState==GameState.GAME_OVER) {
			gameOverMenu.tick();
		}else if(gameState==GameState.MENU) {
			startMenu.tick();
		}else if(gameState==GameState.PAUSE) {
			pauseMenu.tick();
		}
	}
	
	public void applyLight() {
		for(int xx=0;xx<this.WIDTH;xx++) {
			for(int yy=0;yy<this.HEIGHT;yy++) {
				if(lightMapPixels[xx+(yy * this.WIDTH)] == 0xffffffff) {
					int pixel = Pixel.getLightBlend(pixels[xx+(yy * this.WIDTH)],0x808080,0 );
					pixels[xx+(yy * this.WIDTH)] = pixel;
				}
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		
		//renderização do jogo
		//Graphics2D g2 = (Graphics2D) g;
		
		//limpa o fundo
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//desenha o mapa
		world.render(g);
		
		//desenha os objetos, players e inimigos
		Collections.sort(entityList, Entity.entitySorter);
		for(Entity e: entityList) {
			e.render(g);
		}
		//desenha os projeteis
		for(BulletShoot b: bulletList) {
			b.render(g);
		}
		
		//aplica lightmap
		if(enableLight) {
			applyLight();
		}
		
		//desenha o UI
		ui.renderImages(g);
		
		//chama a fun��o para renderizar
		g.dispose();
		g = bs.getDrawGraphics();
		
		g.setColor(Color.black);
		if(isFullScreen) {
			g.fillRect(0, 0, 
					Toolkit.getDefaultToolkit().getScreenSize().width,
					Toolkit.getDefaultToolkit().getScreenSize().height);
		}else {
			g.fillRect(0, 0, WIDTH* SCALE, HEIGHT*SCALE);
		}
		
		if(gameState!=GameState.MENU) {
			//desenha o jogo
			g.drawImage(image, 0, 0,WIDTH* SCALE,HEIGHT*SCALE, null);
			ui.renderTexts(g);
			
			//define e desenha o minimapa
			world.renderMiniMap(miniMapPixels,player);
			g.drawImage(
					miniMap,(WIDTH-(world.width*minimapScale)-2)*SCALE,
					2*SCALE,
					world.width*SCALE*minimapScale,
					world.height*SCALE*minimapScale,null);
			//debug
			if(enableDebug) {
				g.setColor(Color.red);
				g.drawString("mouse", debugMouseX, debugMouseY-20);
				g.fillOval(debugMouseX, debugMouseY, 4, 4);
			}
		}
		
		if(gameState==GameState.GAME_OVER) {
			gameOverMenu.render(g);
		}else if(gameState==GameState.MENU) {
			startMenu.render(g);
		}else if(gameState==GameState.PAUSE) {
			pauseMenu.render(g);
		}
		
		bs.show();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double ammountOfTicks = 60.0;
		double ns = 1000000000/ammountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		
		while (isRunning) {
			long now =System.nanoTime();
			delta += (now-lastTime)/ns;
			lastTime = now;
			
			if(delta>=1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >=1000) {
				curFPS=frames;
				frames=0;
				timer +=1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			player.right=true;
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			player.left=true;
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			player.up=true;
			if(gameState==GameState.MENU) {
				startMenu.up=true;
			}else if(gameState==GameState.GAME_OVER) {
				gameOverMenu.up=true;
			}else if(gameState==GameState.PAUSE) {
				pauseMenu.up=true;
			}
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			player.down=true;
			if(gameState==GameState.MENU) {
				startMenu.down=true;
			}else if(gameState==GameState.GAME_OVER) {
				gameOverMenu.down=true;
			}else if(gameState==GameState.PAUSE) {
				pauseMenu.down=true;
			}
			break;
		case KeyEvent.VK_E:
			player.setShoot(true);
			break;
		case KeyEvent.VK_ESCAPE:
			if(gameState==GameState.NORMAL) {
				setPauseGameState();
			}
			break;
		case KeyEvent.VK_ENTER:
			if(gameState==GameState.MENU) {
				startMenu.enter=true;
			}else if(gameState==GameState.GAME_OVER){
				gameOverMenu.enter=true;
			}else if(gameState==GameState.PAUSE){
				pauseMenu.enter=true;
			}
			break;
		case KeyEvent.VK_Z:
		case  KeyEvent.VK_SPACE:
			player.setJump(true);
			break;
			
		case  KeyEvent.VK_1:
			if(gameState==GameState.NORMAL) {
				saveGame();
			}
			break;
		case  KeyEvent.VK_2:
			if(gameState==GameState.NORMAL) {
				enableLight = !enableLight;
			}
			break;
		
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			player.right=false;
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			player.left=false;
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			player.up=false;
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			player.down=false;
			break;
		case KeyEvent.VK_DELETE:
			this.enableDebug = !enableDebug;
		break;
		
		case KeyEvent.VK_Z:
		case KeyEvent.VK_SPACE:
			player.setJump(false);
			break;
		
		default:
			break;
		}	
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			double angulo = player.getAnguloRad(e.getX()/SCALE, e.getY()/SCALE);
			//to rotate passar o angulo enRadianos,e os pontos do centro para rotacionar
			double cosAngulo = Math.cos(angulo);
			double senAngulo = Math.sin(angulo);
			player.setShoot(true,cosAngulo,senAngulo);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			player.setShoot(false);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		debugMouseX = e.getX();
		debugMouseY = e.getY();
		
	}
}
