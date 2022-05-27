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
import model.Message;

/**
 *
 * @author Dieu Dai Hiep
 */
public class MessageDAO extends DAO {

    public boolean addMsg(int from_id, String to_username, String msg) {
        UserDAO ud = new UserDAO();
        int to_id = ud.getIdByUsername(to_username);
        String sql = "insert into tblMessage(from_id,to_id,msg) values(?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, from_id);
            ps.setInt(2, to_id);
            ps.setString(3, msg);
            ps.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public ArrayList<Message> getListMessage(int from_id, String to_username) {
        ArrayList<Message> list = new ArrayList<>();
        UserDAO ud = new UserDAO();
        int to_id = ud.getIdByUsername(to_username);
        String from_user = ud.getUserByID(from_id).getUsername();
        String sql = "SELECT * FROM tblMessage where (from_id = ? or from_id = ?) and (to_id = ? or to_id = ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, from_id);
            ps.setInt(2, to_id);
            ps.setInt(3, from_id);
            ps.setInt(4, to_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setFrom_id( rs.getInt("from_id"));
                m.setTo_id(rs.getInt("to_id"));
                m.setMsg(rs.getString("msg"));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list; 
        
    }

    public static void main(String[] args) {
        MessageDAO msgd = new MessageDAO();
//        msgd.addMsg(1,"xxx","hello em");
        ArrayList<Message> list = msgd.getListMessage(1,"xxx");
        for( Message msg: list ){
            System.out.println(msg.toString());
        }
    }
}
