/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.mysql.cj.jdbc.Blob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import model.User;

/**
 *
 * @author Dieu Dai Hiep
 */
public class UserDAO extends DAO {

    public UserDAO() {
    }

    public boolean checkLogin(User user) {

        String sql = "SELECT * FROM tblUser WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean isExist(String username) {
        String sql = "Select * from tblUser where username=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
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

    public boolean update(User user) {
        String sql = "update tblUser SET username = ?, password = ?, email =? where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setInt(4, user.getId());
            ps.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean signUp(User user) {

        String sql = "INSERT INTO tblUser(username, password, email) VALUES(?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.executeUpdate();

            //get id of the new inserted client
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getIdByUsername(String username) {
        int res = 0;
        String sql = "select id from tblUser where username = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (Exception e) {
        }
        return res;
    }

    public User getUserByID(int id) {
        User user = new User();
        user.setId(id);
        String sql = "select * from tblUser where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
        } catch (Exception e) {
        }
        return user;
    }

    public ArrayList<User> getFindListUser(String username, String key) {
        UserDAO ud = new UserDAO();
        int id = ud.getIdByUsername(username);
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT * from tblUser where username like ? and id not in (SELECT fr_id from tblFriend where my_id= ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                if (!user.getUsername().equals(username)) {
                    list.add(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
    public boolean updateImg( User user, File f) throws FileNotFoundException{
        String sql = "update tblUser SET img = ? where id = ?";
        FileInputStream fs = new FileInputStream(f);
          try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBinaryStream(1, fs, (int) f.length());
             ps.setInt(2, user.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        
        
    }
//    public boolean SavaImg() throws FileNotFoundException {
//        File f = new File("E:\\Nam 4 PTIT\\Lap trinh mang\\TH3\\testSendImage\\cat.jpg");
//        FileInputStream fs = new FileInputStream(f);
//        String sql = "Insert into tbluser1(id,username,password,img) value(?,?,?,?)";
//        try {
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setInt(1, 3);
//            ps.setString(2, "duc68");
//            ps.setString(3, "duc68");
//            ps.setBinaryStream(4, fs, (int) f.length());
//            ps.executeUpdate();
//            return true;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public byte[] retriveImg(User user) {
        byte[] b = null;
        Blob blob;
        String sql = "select img from tbluser where id =?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                blob = (Blob) rs.getBlob("img");
                b = blob.getBytes(1, (int) blob.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public static void main(String[] args) throws FileNotFoundException {
        // check if username has been exits
        UserDAO ud = new UserDAO();
        //  System.out.println(ud.update(u));

//         UserDAO ud = new UserDAO();
//        User u  = ud.getUserByID(1);
//        System.out.println(u.getUsername()+" "+u.getPassword()+" "+u.getEmail()+u.getId());
        //check findFriend
//        UserDAO ud = new UserDAO();
//        ArrayList<User> list = ud.getFindListUser("hieu32", "");
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println(list.get(i).getUsername());
//        }
        // check login
//        User u = new User();
//        u.setUsername("duc68");
//        u.setPassword("duc68");
//        System.out.println( ud.checkin(u) );
//        System.out.println(u.getId());
        // check signUp
//         User u = new User();
//         u.setUsername("nam40");
//         u.setPassword("123");
//         u.setEmail("namhoang21@gmail.com");
//         ud.signUp(u);
//         System.out.println(u.getId());
    }
}
