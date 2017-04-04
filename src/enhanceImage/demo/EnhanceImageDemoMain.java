package enhanceImage.demo;

/**
 * Created by gustavbergstrom on 2017-03-21.
 */
public class EnhanceImageDemoMain {

	public static void main (String[] args) {
		System.load(System.getProperty("java.library.path"));
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String imagePath = "src/test/resources/randomimages/";
		String imageName = "BKNoFlash.JPG";
		String filePath = imagePath + imageName;

		new EnhanceImageDemo(filePath);
	}
}
