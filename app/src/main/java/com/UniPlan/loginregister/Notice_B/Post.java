package com.UniPlan.loginregister.Notice_B;

import com.UniPlan.loginregister.Table;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;

public class Post implements Comparable<Post> {
    private String writer_id;
    private String title;//게시글 제목
    private String contents;//게시글 내용
    private String p_nickname;//게시글 작성자 닉네임
    private String like; //게시글 좋아요 개수
    @ServerTimestamp
    private Timestamp timestamp;
    private String post_id;
    private String writer_token;
    private ArrayList<Comment> comments; //댓글 리스트들
    private int coment_Num;
    private ArrayList<String> image_url;
    private Table table;
    private ArrayList<String> subscriber;
    private String forum;
    private int click;

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }


    public ArrayList<String> getImage_url() { return image_url; }

    public void setImage_url(ArrayList<String> image_url) { this.image_url=image_url; }

    public void setcoment_Num(int coment_num){this.coment_Num=coment_num;}

    public int getcoment_Num(){return coment_Num;}

    public ArrayList<Comment> getComments() {return comments;}

    public void setComments(ArrayList<Comment> comments){this.comments = comments;}

    public String getWriter_id() {
        return writer_id;
    }

    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getP_nickname() {
        return p_nickname;
    }

    public void setP_nickname(String p_nickname) {
        this.p_nickname = p_nickname;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setDate(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public ArrayList<String> getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(ArrayList<String> subscriber) {
        this.subscriber = subscriber;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }
    public String getWriter_token() {
        return writer_token;
    }

    public void setWriter_token(String writer_token) {
        this.writer_token = writer_token;
    }



    public Post() {
       //빈생성자 생성
        this.writer_id = "";
        this.title = "";
        this.contents = "";
        this.p_nickname = "";
        this.like = "";
        this.timestamp = null;
        this.post_id = "";
        this.comments = new ArrayList<>();
        this.coment_Num=0;
        this.image_url= new ArrayList<>();
        this.forum="";
        this.table = new Table();
        this.subscriber = new ArrayList<>();
        this.click = 0;
        this.writer_token ="";
    }

    public Post(String writer_id, String title, String contents, String p_nickname, String like, Timestamp timestamp, String post_id, ArrayList<Comment> comments, int coment_Num, ArrayList<String> image_url, String forum, Table table,ArrayList<String> subscriber, int click,String writer_token) {
        this.writer_id = writer_id;
        this.title = title;
        this.contents = contents;
        this.p_nickname = p_nickname;
        this.like = like;
        this.timestamp = timestamp;
        this.post_id = post_id;
        this.comments = comments;
        this.coment_Num = coment_Num;
        this.image_url = image_url;
        this.forum = forum;
        this.table = table;
        this.subscriber = subscriber;
        this.click = click;
        this.writer_token = writer_token;
    }

    @Override
    public int compareTo(Post o) {
        return Integer.parseInt(o.getLike()) - Integer.parseInt(getLike());
    }
}