package rpg.com.paifabio.sound;

import java.applet.Applet;
import java.applet.AudioClip;

public class SoundOld {
	private AudioClip clip;
	
	public static final SoundOld musicBackground = new SoundOld("volvion_8bit_level.wav");
	public static final SoundOld death = new SoundOld("death.wav");
	public static final SoundOld heal = new SoundOld("heal.wav");
	public static final SoundOld hit = new SoundOld("hit.wav");
	public static final SoundOld hurt = new SoundOld("hurt.wav");
	public static final SoundOld menu = new SoundOld("menu.wav");
	public static final SoundOld pick = new SoundOld("pick.wav");
	public static final SoundOld shoot = new SoundOld("shoot.wav");
	
	private SoundOld(String name) {
		try {
			clip=Applet.newAudioClip(SoundOld.class.getResource("/"+name));
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
