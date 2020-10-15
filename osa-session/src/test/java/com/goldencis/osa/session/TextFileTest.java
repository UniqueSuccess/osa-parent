package com.goldencis.osa.session;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by limingchao on 2018/11/20.
 */
public class TextFileTest {

    @Test
    public void textFileTest() throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:0\n");
        sb.append("#EXT-X-ALLOW-CACHE:YES\n");
        sb.append("#EXT-X-TARGETDURATION:17\n");

        File file = new File("C:\\Users\\Administrator\\Desktop\\storage\\attachment\\replay");

//        Path path = Paths.get("C:\\Users\\Administrator\\Desktop\\storage\\attachment\\replay\\test.txt");
//        FileChannel channel = FileChannel.open(path);

        FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\storage\\attachment\\replay\\test.txt");
        FileChannel channel = fos.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        buf.clear();
        buf.put(sb.toString().getBytes());
        buf.flip();

        while(buf.hasRemaining()) {
            channel.write(buf);
        }
    }
}
