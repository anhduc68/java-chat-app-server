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
import model.GroupMsg;
import model.Message;
import model.User;

/**
 *
 * @author Dieu Dai Hiep
 */
public class GroupMsgDAO extends DAO{
    public boolean addGrMsg(int user_id, int gr_id, String msg) {
        String sql = "insert into tblGroupMsg(user_id,gr_id,msg) values(?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setInt(2, gr_id);
            ps.setString(3, msg);
            ps.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public ArrayList<GroupMsg> getListGrMsgByID( int gr_id ){
        ArrayList<GroupMsg> list = new  ArrayList<>();
        String sql = "SELECT * FROM tblGroupMsg where gr_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, gr_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GroupMsg m = new GroupMsg();
                m.setUser_id(rs.getInt("user_id"));
                m.setGr_id(rs.getInt("gr_id"));
                m.setMsg( rs.getString("msg"));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        
    }
    public ArrayList<User> getListUserOfGroup( int gr_id ){
        UserDAO ud = new UserDAO();
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT distinct * FROM tblJoin where gr_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, gr_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                int user_id = rs.getInt("user_id");
                list.add( ud.getUserByID(user_id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        
    }
    public static void main(String[] args) {
        GroupMsgDAO gmd = new GroupMsgDAO();
//        gmd.addGrMsg(1, 1,"duc68: Hello ae");
//    ArrayList<GroupMsg> list = gmd.getListGrMsgByID(1);
//       for( int i=0 ; i<list.size() ; i++ ){
//           System.out.println(list.get(i).getMsg());
//       }
//        ArrayList<User> l = gmd.getListUserOfGroup(1);
//        for( User u: l ){
//            System.out.println(u.getUsername());
//        }
           
    }
}
