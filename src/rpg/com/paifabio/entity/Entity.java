package rpg.com.paifabio.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.world.Camera;
import rpg.com.paifabio.world.ai.Node;
import rpg.com.paifabio.world.ai.Vector2i;

public abstract class Entity {
	protected double x;
	protected double y;
	protected double z=0;
	protected int width;
	protected int height;
	protected int maskX,maskY,maskW,maskH;
	protected Rectangle maskRectangle;
	protected List<Node>path;
	
	private BufferedImage sprite;
	protected int depth;
	
	public Entity(int x,int y,int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		this.setMask(0, 0, width, height);
		this.depth=0;
	}
	
	public void setMask(int maskX,int maskY,int maskW,int maskH ) {
		this.maskX = maskX;
		this.maskY = maskY;
		this.maskW = maskW;
		this.maskH = maskH;
		setMaskRectangle();
	}
	
	public static Comparator<Entity> entitySorter = new Comparator<Entity>() {
		@Override
		public int compare(Entity n0, Entity n1) {
			if(n1.depth <n0.depth)
				return + 1;
			
			if(n1.depth >n0.depth)
				return - 1;
			
			return 0;
		}
		
	};
	
	protected void setMaskRectangle() {
		this.maskRectangle = new Rectangle(this.getX()+this.maskX,this.getY()+this.maskY,this.maskW,this.maskH);
	}
	
	protected void drawSombra(Graphics g) {
		//desenha a sombra
		g.setColor(new Color(0,0,0,100));
		g.fillOval( 
				this.getX() + this.maskX -Camera.x -2, 
				this.getY() + this.maskY + this.maskH -Camera.y  -2,  
				this.maskW +4,
				4);
	}
	protected void drawMaskRectangle(Graphics g,Color color) {
		if(Game.getGame().enableDebug) {
			g.setColor(color);
			g.drawRect(
					this.getX()+this.maskX-Camera.x,
					this.getY()+this.maskY-Camera.y,
					this.maskW,
					this.maskH);
			g.fillRect(
					this.getX()+this.maskX-Camera.x,
					this.getY()+this.maskY +this.maskH -Camera.y -this.getZ(),
					this.maskW,
					2
					);
		}
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}
	
	public int getZ() {
		return (int) z;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void tick() {}
	
	public double calculateDistance(Entity e2) {
		return calculateDistance(this.getX(),this.getY(), e2.getX(), e2.getY());
	}
	
	public static double calculateDistance(int x1,int y1, int x2, int y2) {
		
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	public Vector2i followPath(List<Node> path,double speed) {
		Vector2i direcao = new Vector2i(0,0);
		if(path!=null) {
			if(path.size()>0) {
				Vector2i target =path.get(path.size()-1).tile;
				if(x+speed<target.x *16){
					x+=speed;
					direcao.x=1;
				}else if(x-speed>target.x *16) {
					x-=speed;
					direcao.x=(-1);
				}
				
				if(y+speed<target.y *16){
					y+=speed;
					direcao.y=1;
				}else if(y-speed>target.y *16 ){
					y-=speed;
					direcao.y=(-1);
				}
				
				if(Math.abs(x -(target.x *16))<=speed+1  &&  Math.abs(y -(target.y *16))<=speed+1) {
					path.remove(path.size() -1);
				}
				
			}
		}
		
		return direcao;
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		return (e1.maskRectangle.intersects(e2.maskRectangle) && e1.z == e2.z);
	}
	
	public boolean isColliding(Entity e2) {
		if(this.maskRectangle.intersects(e2.maskRectangle) && this.z == e2.z) {
			return true;
		}
		return false;
	}
	
	public boolean isCollidingEnemy(int xNext, int yNext) {
		Rectangle currentEntity = new Rectangle(xNext+maskX,yNext+maskY,maskW,maskH);
		for (Enemy e: Game.getGame().enemyList) {
			if(e==this) 
				continue;
			if(currentEntity.intersects(e.maskRectangle)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		this.drawSombra(g);
		g.drawImage(sprite,this.getX() -Camera.x,this.getY()-Camera.y,null);
		drawMaskRectangle(g, Color.yellow);
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public double getAnguloRad(int mx, int my) {
		return Math.atan2(my-(this.getY()+(this.getHeight()/2) -Camera.y)  , mx -(this.getX()+(this.getWidth()/2) -Camera.x ));
	}
	public double getAnguloGraus(int mx, int my) {
		return Math.toDegrees(getAnguloRad(mx, my));
	}
}
