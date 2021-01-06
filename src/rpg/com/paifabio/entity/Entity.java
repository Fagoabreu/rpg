package rpg.com.paifabio.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.world.Camera;

public abstract class Entity {
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected int maskX,maskY,maskW,maskH;
	protected Rectangle maskRectangle;
	
	private BufferedImage sprite;
	
	public Entity(int x,int y,int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		this.setMask(0, 0, width, height);
	}
	
	public void setMask(int maskX,int maskY,int maskW,int maskH ) {
		this.maskX = maskX;
		this.maskY = maskY;
		this.maskW = maskW;
		this.maskH = maskH;
		setMaskRectangle();
	}
	
	protected void setMaskRectangle() {
		this.maskRectangle = new Rectangle(this.getX()+this.maskX,this.getY()+this.maskY,this.maskW,this.maskH);
	}
	
	
	protected void drawMaskRectangle(Graphics g,Color color) {
		if(Game.getGame().debug) {
			g.setColor(color);
			g.drawRect(this.getX()+this.maskX-Camera.x,this.getY()-Camera.y+this.maskY,this.maskW,this.maskH);
		}
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void tick() {
		
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		return e1.maskRectangle.intersects(e2.maskRectangle);
	}
	
	public boolean isColliding(Entity e2) {
		return this.maskRectangle.intersects(e2.maskRectangle);
	}
	
	public  boolean isColliding(int nextX, int nextY, Entity e2) {
		Rectangle nextRectangle = new Rectangle(nextX+maskX,nextY+maskW,this.maskW,this.maskH);
		return nextRectangle.intersects(e2.maskRectangle);
	}
	
	public void render(Graphics g) {
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
	
	public double getRadToPoint(int mx, int my) {
		return Math.atan2(my-(this.getY()+(this.getHeight()/2) -Camera.y)  , mx -(this.getX()+(this.getWidth()/2) -Camera.x ));
	}
	public double getAngleToPoint(int mx, int my) {
		return Math.toDegrees(getRadToPoint(mx, my));
	}
}
