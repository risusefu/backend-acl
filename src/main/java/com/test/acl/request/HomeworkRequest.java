package com.test.acl.request;

import lombok.Data;

@Data
public class HomeworkRequest {
    private boolean active;
    private String description;

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }
}
