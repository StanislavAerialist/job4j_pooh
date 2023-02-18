package ru.job4j.pooh;

import java.util.List;

public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        String reqType = content.split(" ")[0];
        String pMode = content.split("/")[1];
        String sName = content.split(" ")[1].split("/")[2];
        String[] strings = content.split(System.lineSeparator());
        String[] forParam = strings[0].split(" ")[1].split("/");
        String param;
        if ("POST".equals(reqType)) {
            param = strings[strings.length - 1];
        } else {
            param = forParam.length > 3 ? forParam[3] : "";
        }
        return new Req(reqType, pMode, sName, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}