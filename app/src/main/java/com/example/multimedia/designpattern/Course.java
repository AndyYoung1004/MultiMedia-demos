package com.example.multimedia.designpattern;

public class Course {
    private String mName;
    private String mPPT;
    private String mVideo;
    private String mNote;
    private String mHomework;
    private Course(CourseBuilder builder) {
        this.mName = builder.mName;
        this.mPPT = builder.mPPT;
        this.mVideo = builder.mVideo;
        this.mNote = builder.mNote;
        this.mHomework = builder.mHomework;
    }

    public static class CourseBuilder {
        private String mName;
        private String mPPT;
        private String mVideo;
        private String mNote;
        private String mHomework;

        public CourseBuilder setName(String name) {
            mName = name;
            return this;
        }

        public CourseBuilder setPPT(String ppt) {
            mPPT = ppt;
            return this;
        }

        public CourseBuilder setVideo(String video) {
            mVideo = video;
            return this;
        }

        public CourseBuilder setNote(String note) {
            mNote = note;
            return this;
        }

        public CourseBuilder setHomework(String homework) {
            mHomework = homework;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}
