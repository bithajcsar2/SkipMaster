/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.unideb.inf.skipmaster;

import java.io.Serializable;

/**
 *
 * @author szath
 */
public class Course implements Serializable{
    private String course;
    private String course_type;
    private int numberOfSkips;
    
    public Course(String course, String course_type, int numberOfSkips) {
        this.course = course;
        this.course_type = course_type;
        this.numberOfSkips = numberOfSkips;
    }
    
    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public int getNumberOfSkips() {
        return numberOfSkips;
    }

    public void setNumberOfSkips(int numberOfSkips) {
        this.numberOfSkips = numberOfSkips;
    }
}
