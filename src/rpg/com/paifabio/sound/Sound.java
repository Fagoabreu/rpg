package rpg.com.paifabio.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	public static Clips musicBackground = load("/volvion_8bit_level.wav");
	public static Clips death = load("/death.wav");
	public static Clips heal = load("/heal.wav");
	public static Clips hit = load("/hit.wav");
	public static Clips hurt = load("/hurt.wav");
	public static Clips menu = load("/menu.wav");
	public static Clips pick = load("/pick.wav");
	public static Clips shoot = load("/shoot.wav");
	
	public static class Clips{
		public Clip clip;
		
		public Clips(byte[] buffer) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
			if (buffer==null)
				return;
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
		}
		
		public void play() {
			if(clip==null)
				return;
			
			clip.stop();
			clip.setFramePosition(0);
			clip.start();
		}
		
		public void loop() {
			if(clip==null)
				return;
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		public void setVolume(int volume) {
			//ajusta o volume entre 0 e 100
			volume =volume<0?0:volume>100?100:volume;
			
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = ((float)volume/100) * range + gainControl.getMinimum();
			gainControl.setValue(gain);
		}
	}
	
	public static Clips load(String name) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(SoundOld.class.getResourceAsStream(name));
			
			byte[] buffer = new byte[1024];
			int read = 0;
			while((read=dis.read(buffer)) >= 0 ) {
				baos.write(buffer,0,read);
			}
			dis.close();
			byte[] data = baos.toByteArray();
			return new Clips(data);
			
		}catch (Exception e) {
			return null;
		}
	}
	
}
