package com.example.demo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {
    public GZipUtils() {
    }

    public static String compress(String str) throws IOException {
        if (null != str && str.length() > 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes("utf-8"));
            gzip.close();
            return out.toString("ISO-8859-1");
        } else {
            return str;
        }
    }

    public static String uncompress(String str) throws IOException {
        if (null != str && str.length() > 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            boolean var5 = false;

            int n;
            while((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }

            return out.toString("utf-8");
        } else {
            return str;
        }
    }
}
