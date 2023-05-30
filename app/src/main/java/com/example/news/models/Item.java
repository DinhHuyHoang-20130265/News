package com.example.news.models;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Item {
    private int id;
    private String title, link, date, linkImg;

    public Item() {
    }

    public Item(String title, String link, String date, String linkImg) {
        this.title = title;
        this.link = link;
        this.date = date;
        this.linkImg = linkImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(title, item.title) && Objects.equals(link, item.link) && Objects.equals(date, item.date) && Objects.equals(linkImg, item.linkImg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, date, linkImg);
    }

    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", date='" + date + '\'' +
                ", linkImg='" + linkImg + '\'' +
                '}';
    }
}
