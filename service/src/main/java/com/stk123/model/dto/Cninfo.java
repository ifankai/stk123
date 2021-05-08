package com.stk123.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class Cninfo {
    @Setter
    @Getter
    public static class NoticeRoot {
        private String classifiedAnnouncements;
        private int totalSecurities;
        private int totalAnnouncement;
        private int totalRecordNum;
        private List<Announcement> announcements;
        private String categoryList;
        private boolean hasMore;
        private int totalpages;
    }

    @Setter
    @Getter
    @ToString
    public static class Announcement {
        private String id;
        private String secCode;
        private String secName;
        private String orgId;
        private String announcementId;
        private String announcementTitle;
        private long announcementTime;
        private String adjunctUrl;
        private int adjunctSize;
        private String adjunctType;
        private String storageTime;
        private String columnId;
        private String pageColumn;
        private String announcementType;
        private String associateAnnouncement;
        private String important;
        private String batchNum;
        private String announcementContent;
        private String orgName;
        private String announcementTypeName;
    }
}
