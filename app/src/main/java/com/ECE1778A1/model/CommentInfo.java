/*
 * MIT License
 *
 * Copyright (c) 2020 YixiaoHong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ECE1778A1.model;

import java.io.Serializable;

public class CommentInfo implements Serializable,Comparable{

    private String photo_id, photo_owner_id, commenter_id, comment_time, commenter_name, comment_text;

    public CommentInfo() {
    }

    public CommentInfo(String photo_id, String photo_owner_id, String commenter_id, String comment_time, String commenter_name, String comment_text){
        this.photo_id = photo_id;
        this.photo_owner_id =photo_owner_id;
        this.commenter_id =commenter_id;
        this.comment_time =comment_time;
        this.commenter_name =commenter_name;
        this.comment_text =comment_text;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public String getPhoto_owner_id() {
        return photo_owner_id;
    }

    public String getCommenter_id() {
        return commenter_id;
    }

    public String getComment_time() {
        return comment_time;
    }

    public String getCommenter_name() {
        return commenter_name;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public void setPhoto_owner_id(String photo_owner_id) {
        this.photo_owner_id = photo_owner_id;
    }

    public void setCommenter_id(String commenter_id) {
        this.commenter_id = commenter_id;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public void setCommenter_name(String commenter_name) {
        this.commenter_name = commenter_name;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    @Override
    public int compareTo(Object o) {
        Integer me = Integer.valueOf(this.comment_time);
        Integer other = Integer.valueOf(((CommentInfo)o).getComment_time());
        return me.compareTo(other);
    }
}
