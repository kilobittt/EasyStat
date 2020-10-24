package inozemtsev.coursework.easystat.utils;

import java.net.URL;

public class Request {
    URL url;
    String body;
    String token;
    RequestType type;

    public Request(URL url, String body, RequestType type) {
        this.url = url;
        this.body = body;
        this.type = type;
    }

    public URL getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public String getToken() {
        return token;
    }

    public RequestType getType() {
        return type;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
