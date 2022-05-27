/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Dieu Dai Hiep
 */
public class Group implements Serializable{
    int id;
    String gr_name;

    public Group() {
    }

    public Group(int id, String gr_name) {
        this.id = id;
        this.gr_name = gr_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGr_name() {
        return gr_name;
    }

    public void setGr_name(String gr_name) {
        this.gr_name = gr_name;
    }
    
}
