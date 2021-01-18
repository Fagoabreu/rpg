package rpg.com.paifabio.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;
import rpg.com.paifabio.world.Camera;
import rpg.com.paifabio.world.World;

public class BulletShoot extends Entity{

	private double dx,dy;
	private double speed;
	private int curlifeTime, maxLifeTime,damage=1;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite,double dx,double dy,double speed) {
		super(x, y, width, height, sprite);
		this.dx=dx;
		this.dy=dy;
		this.speed=speed;
		this.curlifeTime=0;
		maxLifeTime=20;
		Sound.shoot.play();
		Game.getGame().bulletList.add(this);
	}

	public void tick() {
		if(World.getWorld().isWall(getX(), getY())) {
			Sound.hit.play();
			destroy();
		}
		
		x+=(dx * speed);
		y+=(dy * speed);
		setMaskRectangle();
		List<Enemy> enemyList = Game.getGame().enemyList;
		for( int i=0;i< enemyList.size();i++) {
			Enemy e = enemyList.get(i);
			if(this.isColliding(e)) {
				Sound.hit.play();
				e.takeDamage(damage);
				destroy();
			}
		}
		
		curlifeTime++;
		if(curlifeTime>=maxLifeTime) {
			destroy();
		}
		
		
	}
	
	public void render(Graphics g) {
		super.render(g);
		g.setColor(Color.yellow);
		g.drawOval(this.getX()-Camera.x,this.getY()-Camera.y, this.getWidth(), this.getHeight());
	}
	
	public void destroy() {
		Game.getGame().bulletList.remove(this);
	}
}
