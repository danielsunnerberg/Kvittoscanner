package enhanceImage.demo;

/**
 * Created by gustavbergstrom on 2017-03-21.
 */
public class EnhanceImageDemoMain {

	public static void main (String[] args) {
		System.load(System.getProperty("java.library.path"));
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String imagePath = "src/test/resources/randomimages/";
		String stadiumReceiptName = "Stadium.JPG";
		String bkReceiptName = "BKNoFlash.JPG";
		String filePath = imagePath + stadiumReceiptName;

		new EnhanceImageDemo(filePath);
	}
}
