package io.owslab.mailreceiver.utils;

import org.springframework.http.MediaType;

import javax.servlet.ServletContext;

/**
 * Created by khanhlvb on 3/20/18.
 */
public class MediaTypeUtils {
    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
        String mineType = servletContext.getMimeType(fileName);
        try {
            MediaType mediaType = MediaType.parseMediaType(mineType);
            return mediaType;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
