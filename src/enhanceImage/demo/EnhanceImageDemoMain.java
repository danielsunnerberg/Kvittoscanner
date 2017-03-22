package enhanceImage.demo;

/**
 * Created by gustavbergstrom on 2017-03-21.
 */
public class EnhanceImageDemoMain {
	public static void main (String[] args) {
		System.load(System.getProperty("java.library.path"));
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String imageName = "BKNoFlash.JPG";

		new EnhanceImageDemo(imageName);
	}
}
