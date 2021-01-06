package rpg.com.paifabio.main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
	private AudioClip clip;
	
	public static final Sound musicBackground = new Sound("volvion_8bit_level.wav");
	public static final Sound death = new Sound("death.wav");
	public static final Sound heal = new Sound("heal.wav");
	public static final Sound hit = new Sound("hit.wav");
	public static final Sound hurt = new Sound("hurt.wav");
	public static final Sound menu = new Sound("menu.wav");
	public static final Sound pick = new Sound("pick.wav");
	public static final Sound shoot = new Sound("shoot.wav");
	
	private Sound(String name) {
		try {
			clip=Applet.newAudioClip(Sound.class.getResource("/"+name));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loop() {
		try {
			new Thread() {
				public void  run() {
					clip.loop();
				}
			}.start();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		try {
			new Thread() {
				public void  run() {
					clip.play();
				}
			}.start();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
