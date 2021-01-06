package rpg.com.paifabio.world;

public class Camera {
	public static int x;
	public static int y;
	
	public static int clamp (int atual,int min, int max) {
		int result = atual;
		if(atual<min) {
			result =min;
		}else if(atual>max) {
			result =max;
		}
		return result;
	}

}
