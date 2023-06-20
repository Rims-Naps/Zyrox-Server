package com.zenyte.api.client.webhook.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Corey
 * @since 06/04/2020
 */
@Data
@Builder
public class EmbedObject {
    private static final DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    
    private List<Field> fields;
    private String title;
    private String description;
    private String url;
    private int color;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private String timestamp;
    private Author author;
    
    public static class EmbedObjectBuilder {
        public EmbedObjectBuilder field(String name, String value, boolean inline) {
            if (this.fields == null) {
                this.fields = new ArrayList<>();
            }
            this.fields.add(new Field(name, value, inline));
            return this;
        }
        
        public EmbedObjectBuilder timestamp(final Date date) {
            this.timestamp = timestampFormat.format(date);
            return this;
        }
    }
    
    @Data
    public static class Footer {
        private final String text;
        @SerializedName("icon_url")
        private final String iconUrl;
    }
    
    @Data
    public static class Thumbnail {
        private final String url;
    }
    
    @Data
    public static class Image {
        private final String url;
    }
    
    @Data
    public static class Author {
        private final String name;
        private final String url;
        @SerializedName("icon_url")
        private final String iconUrl;
    }
    
    @Data
    public static class Field {
        private final String name;
        private final String value;
        private final boolean inline;
    }
}
