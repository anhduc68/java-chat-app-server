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
public class FriendReqDAO extends DAO{

    public FriendReqDAO() {
    }
    public boolean isExist( int from_id , int to_id){
        
        String sql = "Select * from tblfriendreq where from_id = ? and to_id =?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, from_id);
            ps.setInt(2, to_id);
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
    
    public boolean addRequest( int from_id, int to_id ){
        String sql = "insert into tblfriendreq(from_id,to_id) values(?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, from_id);
            ps.setInt(2, to_id);
            ps.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
     public ArrayList<User> showListUserReq( int to_id ){
        ArrayList<User> list= new ArrayList<User>();
        UserDAO ud = new UserDAO();
        String sql = "select from_id from tblfriendreq where to_id= ? and from_id not in (SELECT fr_id as from_id from tblFriend where my_id= ?)";
         try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, to_id);
            ps.setInt(2, to_id);
            ResultSet res = ps.executeQuery();
            while( res.next()){
                User u = ud.getUserByID(res.getInt("from_id"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void main(String[] args) {
        
   
       FriendReqDAO rd = new FriendReqDAO();
       //test getUserReq
//       ArrayList<User> list = rd.showListUserReq(7);
//       for( User u:list ){
//           System.out.println(u.getUsername());
//       }
    }
    
   
}
