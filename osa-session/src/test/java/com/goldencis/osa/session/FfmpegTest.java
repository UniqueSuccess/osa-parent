package com.goldencis.osa.session;

import org.junit.Test;

import java.io.*;

/**
 * Created by limingchao on 2018/11/21.
 */
public class FfmpegTest {

    @Test
    public void test() throws IOException {
        Process process;
        Runtime runtime =Runtime.getRuntime();

        process = runtime.exec("D:\\ffmpeg\\ffmpeg -i D:\\ffmpeg\\1.mp4 -vcodec copy -acodec copy -vbsf h264_mp4toannexb D:\\ffmpeg\\1.ts");
        showResult(process);

        process = runtime.exec("D:\\ffmpeg\\ffmpeg -i D:\\ffmpeg\\2.mp4 -vcodec copy -acodec copy -vbsf h264_mp4toannexb D:\\ffmpeg\\2.ts");
        showResult(process);

        process = runtime.exec("D:\\ffmpeg\\ffmpeg -i D:\\ffmpeg\\3.mp4 -vcodec copy -acodec copy -vbsf h264_mp4toannexb D:\\ffmpeg\\3.ts");
        showResult(process);

        process = runtime.exec("D:\\ffmpeg\\ffmpeg -i \"concat:D:\\ffmpeg\\1.ts|D:\\ffmpeg\\2.ts|D:\\ffmpeg\\3.ts\" -acodec copy -vcodec copy -absf aac_adtstoasc D:\\ffmpeg\\output.mp4");
        showResult(process);
    }

    public void showResult(Process process) throws IOException {
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        System.out.println(sb.toString());

        is.close();
        isr.close();
        br.close();
    }
}
