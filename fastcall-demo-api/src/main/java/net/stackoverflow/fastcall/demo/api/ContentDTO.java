package net.stackoverflow.fastcall.demo.api;

public class ContentDTO {

    private String content;

    private Integer code;

    public ContentDTO() {

    }

    public ContentDTO(String content, Integer code) {
        this.content = content;
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
