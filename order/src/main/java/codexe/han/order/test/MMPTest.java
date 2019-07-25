package codexe.han.order.test;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MMPTest {
    public static void main(String args[]){

        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile("/hello.txt", "rw");
            RandomAccessFile world = new RandomAccessFile("C:/hinusDocs/world.txt", "rw");
            FileChannel fc = f.getChannel();
            MappedByteBuffer buf = fc.map(FileChannel.MapMode.READ_WRITE, 0, 20);

            fc.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
