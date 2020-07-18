package com.example.demo.dto;

import com.example.demo.link.HyperLink;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private String userName;
    private String role;
    private List<HyperLink> Links = new ArrayList<>();

    public void setLinks(List<HyperLink> links) {
        Links = links;
    }

    public List<HyperLink> getLinks() {
        return Links;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void addLink(String href, String rel){
        HyperLink Link = new HyperLink();
        Link.setRel(rel);
        Link.setHref(href);
        Links.add(Link);
    }
}
