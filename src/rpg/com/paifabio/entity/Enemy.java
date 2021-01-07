package rpg.com.paifabio.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import rpg.com.paifabio.enums.TipoEnemy;
import rpg.com.paifabio.graficos.Spritesheet;
import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.world.Camera;
import rpg.com.paifabio.world.World;

public class Enemy extends Entity{
	private double speed=0.4;
	private TipoEnemy tipoEnemy=null;
	
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] downPlayer;
	private BufferedImage[] idlePlayer;
	private BufferedImage[] damagePlayer;
	
	private BufferedImage[] curAnim;
	
	private boolean isDamaged =false;
	
	private int curDamagedFrame=0,maxDamagedFrames = 10;
	
	private int frames = 0,maxFrames=10, index = 0;

	private int life=3;
	
	public Enemy(int x, int y, int width, int height, Spritesheet spritesheet,TipoEnemy tipoEnemy) {
		this(x, y, width, height, spritesheet,0, 0, 0,0,tipoEnemy);
	}
	public Enemy(int x, int y, int width, int height, Spritesheet spritesheet,int maskX, int maskY, int maskW,int maskH,TipoEnemy tipoEnemy) {
		super(x, y, width, height, spritesheet.getSpriteByPosition(2, 6));
		
		super.setMask(maskX, maskY, maskW, maskH);
		this.tipoEnemy =tipoEnemy;
		if(this.tipoEnemy == TipoEnemy.ESQUELETO) {
			life=3;
			speed=0.4;
			this.setMask(3,0,9,16);
			idlePlayer = new BufferedImage[4];
			downPlayer = new BufferedImage[4];
			leftPlayer = new BufferedImage[4];
			rightPlayer = new BufferedImage[4];
			damagePlayer = new BufferedImage[2];
			
			idlePlayer[0] =	spritesheet.getSpriteByPosition(2, 6);
			idlePlayer[1] =	spritesheet.getSpriteByPosition(3, 6);
			idlePlayer[2] =	spritesheet.getSpriteByPosition(4, 6);
			idlePlayer[3] =	spritesheet.getSpriteByPosition(5, 6);
			
			downPlayer[0] = spritesheet.getSpriteByPosition(2, 7);
			downPlayer[1] = spritesheet.getSpriteByPosition(3, 7);
			downPlayer[2] = spritesheet.getSpriteByPosition(4, 7);
			downPlayer[3] = spritesheet.getSpriteByPosition(5, 7);
			
			leftPlayer[0] = spritesheet.getSpriteByPosition(2, 8);
			leftPlayer[1] = spritesheet.getSpriteByPosition(3, 8);
			leftPlayer[2] = spritesheet.getSpriteByPosition(4, 8);
			leftPlayer[3] = spritesheet.getSpriteByPosition(5, 8);
			
			rightPlayer[0] = spritesheet.getSpriteByPosition(2, 9);
			rightPlayer[1] = spritesheet.getSpriteByPosition(3, 9);
			rightPlayer[2] = spritesheet.getSpriteByPosition(4, 9);
			rightPlayer[3] = spritesheet.getSpriteByPosition(5, 9);
			
			damagePlayer[0] = spritesheet.getSpriteByPosition(0, 6);
			damagePlayer[1] = spritesheet.getSpriteByPosition(1, 6);
			
		}else if(this.tipoEnemy == TipoEnemy.LOBO) {
			life=2;
			speed=0.6;
			super.setMask(0,6,14,10);
			
			idlePlayer = new BufferedImage[4];
			downPlayer= new BufferedImage[4];
			leftPlayer = new BufferedImage[4];
			rightPlayer = new BufferedImage[4];
			damagePlayer = new BufferedImage[2];
			
			idlePlayer[0] = spritesheet.getSpriteByPosition(6, 6);
			idlePlayer[1] = spritesheet.getSpriteByPosition(7, 6);
			idlePlayer[2] = spritesheet.getSpriteByPosition(8, 6);
			idlePlayer[3] = spritesheet.getSpriteByPosition(9, 6);
			
			downPlayer[0] = spritesheet.getSpriteByPosition(6, 7);
			downPlayer[1] = spritesheet.getSpriteByPosition(7, 7);
			downPlayer[2] = spritesheet.getSpriteByPosition(8, 7);
			downPlayer[3] = spritesheet.getSpriteByPosition(9, 7);
			
			leftPlayer[0] = spritesheet.getSpriteByPosition(6, 8);
			leftPlayer[1] = spritesheet.getSpriteByPosition(7, 8);
			leftPlayer[2] = spritesheet.getSpriteByPosition(8, 8);
			leftPlayer[3] = spritesheet.getSpriteByPosition(9, 8);
			
			rightPlayer[0] = spritesheet.getSpriteByPosition(6, 9);
			rightPlayer[1] = spritesheet.getSpriteByPosition(7, 9);
			rightPlayer[2] = spritesheet.getSpriteByPosition(8, 9);
			rightPlayer[3] = spritesheet.getSpriteByPosition(9, 9);
			
			damagePlayer[0] = spritesheet.getSpriteByPosition(0, 7);
			damagePlayer[1] = spritesheet.getSpriteByPosition(1, 7);
		}
		
		curAnim=downPlayer;
		
	}
	
	public void tick() {

		curAnim=idlePlayer;
		
		if(!this.isCollidingWithPlayer()) {
			//chase player
			double xNext = x;
			double yNext = y;
			Player playerRef = Game.getGame().getPlayer(); 
					
			if((int)y<playerRef.getY()) {
				yNext+=speed;
				curAnim = downPlayer;
			}else if((int)y>playerRef.getY()) {
				yNext-=speed;
				curAnim = downPlayer;
			}
			
			if((int)x<playerRef.getX()) {
				xNext+=speed;
				curAnim = rightPlayer;
			}else if((int)x>playerRef.getX()) {
				xNext-=speed;
				curAnim = leftPlayer;
			}
			
			if(World.getWorld().isfree(this.getX(), (int)yNext) && !isColliding(this.getX(), (int)yNext)){
				y=yNext;
				super.setMaskRectangle();
			}
			
			if(World.getWorld().isfree((int)xNext, this.getY()) && !isColliding((int)xNext,this.getY())){
				x=xNext;
				super.setMaskRectangle();
			}
		}else {
			if(Game.getGame().getRandonInt(10)<1) {
				Game.getGame().getPlayer().takeDamage(1);
			}
		}
		
		if(isDamaged) {
			curDamagedFrame++;
			curAnim=damagePlayer;
			if (curDamagedFrame>=maxDamagedFrames) {
				isDamaged=false;
				curDamagedFrame=0;
			}
		}
		
		//CONTROLA A TROCA DE  FRAMES
		frames++;
		if(frames == maxFrames) {
			frames=0;
			index++;
		}
		
		if(index>=curAnim.length) {
			index=0;
		}
		
	}

	@Override
	public void render (Graphics g) {
		g.drawImage(curAnim[index], this.getX()-Camera.x, this.getY()-Camera.y,  null);
		//debug
		super.drawMaskRectangle(g, Color.red);
	}
	
	public boolean isCollidingWithPlayer() {
		Player player = Game.getGame().getPlayer();
		return super.isColliding(player);
	}
	
	public boolean isColliding(int xNext, int yNext) {
		Rectangle currentEnemy = new Rectangle(xNext+maskX,yNext+maskY,maskW,maskH);
		for (Enemy e: Game.getGame().enemyList) {
			if(e==this) 
				continue;
			if(currentEnemy.intersects(e.maskRectangle)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void takeDamage(int value) {
		life-=value;
		isDamaged=true;
		if(life<=0) {
			life=0;
			Game.getGame().enemyList.remove(this);
			Game.getGame().entityList.remove(this);
			return;
		}
	}
}
