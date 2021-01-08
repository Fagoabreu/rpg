package rpg.com.paifabio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	private final int SCALE = 3;
	public int curFPS =0;
	public boolean debug=false;
	
	private int cur_level=1,max_level=7;
	private BufferedImage image;
	public List<Entity> entityList;
	public List<Enemy> enemyList;
	public List<BulletShoot> bulletList ;
	public Spritesheet spritesheet;
	public int dificuldade;
	
	public World world;
	
	private Player player;
	
	private Random rand;
	
	public UI ui;
	
	public StartMenu startMenu;
	public GameOverMenu gameOverMenu;
	public PauseMenu pauseMenu;
	
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
	
	public void setPauseGame() {
		this.gameState=GameState.PAUSE;
	}
	
	public void setContinueGame() {
		this.gameState=GameState.NORMAL;
	}
	public void setStartMenu() {
		this.gameState=GameState.MENU;
	}
	public void setInitGame() {
		initialize();
		this.gameState=GameState.NORMAL;
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
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		
		//inicializa objetos
		ui = new UI(SCALE);
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		spritesheet = new Spritesheet("/spritesheet.png");
		startMenu = new StartMenu(WIDTH,HEIGHT,SCALE); 
		gameOverMenu = new GameOverMenu(WIDTH, HEIGHT, SCALE);
		pauseMenu = new  PauseMenu(WIDTH, HEIGHT, SCALE);
		
		this.initialize();
	}
	
	public void initialize() {
		dificuldade=0;
		player = new Player(0, 0, 16, 16, spritesheet);
		player.setMask(2,0,12,16);
		String mapSprite ="map1";
		initialize(player,mapSprite);
	}
	
	public void loadGame(String mapSprite) {
		dificuldade=0;
		player = new Player(0, 0, 16, 16, spritesheet);
		player.setMask(2,0,12,16);
		initialize(player,mapSprite);
	}
	
	public void initialize(Player player,String mapSprite) {
		entityList = new ArrayList<Entity>();
		enemyList = new ArrayList<Enemy>();
		bulletList = new ArrayList<BulletShoot>();
		
		entityList.add(player);
		
		world = new World("/"+mapSprite +".png",
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
		World.setWorld(world);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getRandonInt(int value) {
		return rand.nextInt(value);
	}
	
	public void initFrame() {
		frame = new JFrame("Torre das Caveiras");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
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
				if (cur_level>max_level) {
					cur_level=1;
					dificuldade++;
				}
				initialize(player, "map"+cur_level);
			}
		}else if (gameState==GameState.GAME_OVER) {
			gameOverMenu.tick();
		}else if(gameState==GameState.MENU) {
			startMenu.tick();
		}else if(gameState==GameState.PAUSE) {
			pauseMenu.tick();
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		//limpa o fundo
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		
		//renderização do jogo
		//Graphics2D g2 = (Graphics2D) g;
		
		//desenha o mapa
		world.render(g);
		
		//desenha os objetos, players e inimigos
		for(Entity e: entityList) {
			e.render(g);
		}
		//desenha os projeteis
		for(BulletShoot b: bulletList) {
			b.render(g);
		}
		//desenha o UI
		ui.renderImages(g);
		
		//chama a função para renderizar
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0,WIDTH* SCALE,HEIGHT*SCALE, null);
		ui.renderTexts(g);
		
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
	public void keyTyped(KeyEvent e) {
	}

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
				setPauseGame();
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
			this.debug = !debug;
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
			double angulo = player.getRadToPoint(e.getX()/SCALE, e.getY()/SCALE);
			//to rotante s� passar o angulo enRadianos,e os pontos do centro para rotacionar
			
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
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
