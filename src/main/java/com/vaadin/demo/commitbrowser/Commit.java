package com.vaadin.demo.commitbrowser;

import java.util.Date;

/**
 *
 * @author Matti Tahvonen
 */
public class Commit {

    private String committer;
    private String email;
    private Date timestamp;
    private Date commitTime;
    private String message;
    private String id;
    private String fullMessage;
    private double size;
    private String fullName;
    private String fullTopic;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public double getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size / 1024.0; // divide by 1k to get a reasonable progress
                                   // bar value.
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullTopic() {
        return fullTopic;
    }

    public void setFullTopic(String fullTopic) {
        this.fullTopic = fullTopic;
    }

}
