package com.yyg365.interestbar.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SEARCH".
 */
public class JFSearch {

    private Long id;
    private String content;

    public JFSearch() {
    }

    public JFSearch(Long id) {
        this.id = id;
    }

    public JFSearch(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
