package com.example.demo.python;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StreamGobbler extends Thread {
    private static Logger logger = LoggerFactory.getLogger(StreamGobbler.class);
    private InputStream inputStream;
    private String streamType;
    private StringBuilder buf;
    private CountDownLatch countDownLatch;

    public StreamGobbler(final InputStream inputStream, final String streamType) {
        this.inputStream = inputStream;
        this.streamType = streamType;
        this.buf = new StringBuilder();
        //this.isStopped = false;
        countDownLatch = new CountDownLatch(1);
    }


    @Override
    public void run() {
        try {
            // 默认编码为UTF-8，这里设置编码为GBK，因为WIN7的编码为GBK
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                this.buf.append(line + "\n");
            }
        } catch (IOException ex) {
            logger.error("Failed to successfully consume and display the input stream of type " + streamType + ".", ex);
        } finally {

            countDownLatch.countDown();
        }
    }

    public String getContent() {
        try {
            countDownLatch.await(70, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.buf.toString();
    }
}
