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

import rpg.com.paifabio.statics.StaticValues;

public class Sound {

	public static Clips musicBackground = load("/volvion_8bit_level.wav",1);
	public static Clips death = load("/death.wav",1);
	public static Clips heal = load("/heal.wav",1);
	public static Clips hit = load("/hit.wav",1);
	public static Clips hurt = load("/hurt.wav",1);
	public static Clips menu = load("/menu.wav",1);
	public static Clips pick = load("/pick.wav",1);
	public static Clips shoot = load("/shoot.wav",1);
	
	public static class Clips{
		public Clip[] clips;
		private int p;
		private int count;
		
		public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
			if (buffer==null)
				return;
			
			clips = new Clip[count];
			this.count = count;
			
			for(int i = 0; i < count; i++) {
				clips[i] = AudioSystem.getClip();
				clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
			}
		}
		
		public void play() {
			if(clips==null)
				return;
			
			clips[p].stop();
			clips[p].setFramePosition(0);
			clips[p].start();
			p++;
			if(p>=count) p = 0;
		}
		
		public void loop() {
			if(clips==null)
				return;
			clips[p].loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		public void setVolume(int volume) {
			//ajusta o volume entre 0 e 100
			volume =volume<0?0:volume>100?100:volume;
			
			FloatControl gainControl = (FloatControl) clips[p].getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = ((float)volume/100) * range + gainControl.getMinimum();
			gainControl.setValue(gain);
		}
	}
	
	public static Clips load(String name,int count) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(SoundOld.class.getResourceAsStream(StaticValues.audioFolder + name));
			
			byte[] buffer = new byte[1024];
			int read = 0;
			while((read=dis.read(buffer)) >= 0 ) {
				baos.write(buffer,0,read);
			}
			dis.close();
			byte[] data = baos.toByteArray();
			return new Clips(data,count);
			
		}catch (Exception e) {
			return null;
		}
	}
	
}
