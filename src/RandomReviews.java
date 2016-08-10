import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by lld on 16/4/23.
 */
public class RandomReviews {
    private String sourceFolder = "/Users/lld/Dropbox/semester4/KT/Assignment/revs";
    private String desFolder = "/Users/lld/Dropbox/semester4/KT/Assignment/randomeReviews2";

    private static void nioTransferCopy(File source, File target) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inStream.close();
            in.close();
            outStream.close();
            out.close();
        }
    }

    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    public static void main(String args[]) throws IOException{
        RandomReviews rm = new RandomReviews();
        File sourceFolder = new File(rm.sourceFolder);
        File desFolder = new File(rm.desFolder);
        String[] fileList = sourceFolder.list();
        int[] randomArray = RandomReviews.randomCommon(0, fileList.length, 100);
        for(int i = 0; i < randomArray.length; i++) {
            File source = new File(rm.sourceFolder + File.separator +fileList[randomArray[i]]);
            File target = new File(rm.desFolder + File.separator + source.getName());
            target.createNewFile();
            RandomReviews.nioTransferCopy(source, target);
        }
    }
}
