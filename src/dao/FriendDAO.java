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
import model.User;

/**
 *
 * @author Dieu Dai Hiep
 */
public class FriendDAO extends DAO{

    public FriendDAO() {
    }
     public boolean isExist( int my_id , int fr_id){
        
        String sql = "Select * from tblfriend where my_id = ? and fr_id =?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, my_id);
            ps.setInt(2, fr_id);
            ResultSet rs = ps.executeQuery();
            if( rs.next() ){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    public boolean addFriend( int my_id, int fr_id ){
        String sql = "insert into tblFriend(my_id,fr_id) values(?,?)";
        try {
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, my_id);
            ps.setInt(2, fr_id);
            ps.execute();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }
    public ArrayList<User> showListFriend( int my_id ){
        ArrayList<User> list= new ArrayList<User>();
        UserDAO ud = new UserDAO();
        String sql = "Select fr_id from tblfriend where my_id=?";
         try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, my_id);
            ResultSet res = ps.executeQuery();
            while( res.next()){
                User u = ud.getUserByID(res.getInt("fr_id"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void main(String[] args) {
        FriendDAO fd = new FriendDAO();
//        System.out.println(fd.isExist(1,14));
//        ArrayList<User> list = fd.showListFriend(2);
//        for( User u: list ){
//            System.out.println(u.getUsername());
//        }
    }
    
}
