/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static dao.DAO.con;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Group;
import model.Message;

/**
 *
 * @author Dieu Dai Hiep
 */
public class GroupDAO extends DAO {

    public boolean addGroup(String gr_name) {

        String sql = "insert into tblGroup(gr_name) values(?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, gr_name);
            ps.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isGroupExist(String gr_name) {

        String sql = "Select * from tblGroup where  gr_name = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, gr_name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean isJoinExist(int user_id, int gr_id) {
        String sql = "Select * from tblJoin where user_id = ? and gr_id =?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setInt(2, gr_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    public int getIDByName(String gr_name) {

        int res = 0;
        String sql = "select id from tblGroup where gr_name = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, gr_name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (Exception e) {
        }
        return res;
    }

    public boolean join(int user_id, int gr_id) {
        String sql = "insert into tblJoin(user_id,gr_id) values(?,?)";
        try {

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setInt(2, gr_id);
            ps.execute();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Group getGroupByID( int gr_id ){
         Group  g = new Group();
         g.setId(gr_id);
        String sql = "select * from tblGroup where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, gr_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                g.setGr_name(rs.getString("gr_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return g;
        
    }
    public ArrayList<Group> getGroupsByUserID(int user_id) {
        ArrayList<Group> list = new ArrayList<>();
         GroupDAO gd = new GroupDAO();
        String sql = "SELECT * FROM tblJoin where user_id = ?  ";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int gr_id = rs.getInt("gr_id");
                Group g = gd.getGroupByID(gr_id);
                list.add(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    public static void main(String[] args) {
        GroupDAO gd = new GroupDAO();
//        ArrayList<Group> list = gd.getGroupsByUserID(1);
//        for (Group g : list) {
//            System.out.println(g.getGr_name());
//        }
    }
}
