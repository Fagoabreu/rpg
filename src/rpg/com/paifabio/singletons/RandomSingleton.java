package rpg.com.paifabio.singletons;

import java.util.Random;

public final class RandomSingleton {
	private static RandomSingleton instance;
	private Random random;
	
	private RandomSingleton() {
		this.random = new Random();
	}
	
	public static RandomSingleton getInstance() {
		if(instance==null) {
			instance = new RandomSingleton();
		}
		return instance;
	}
	
	public int nextInt() {
		return random.nextInt();
	}
	
	public int nextInt(int bound) {
		return random.nextInt(bound);
	}
}
