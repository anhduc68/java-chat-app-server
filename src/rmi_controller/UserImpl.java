/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi_controller;

import dao.UserDAO;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import model.User;

/**
 *
 * @author Dieu Dai Hiep
 */
public class UserImpl extends java.rmi.server.UnicastRemoteObject implements IUser{
    public UserImpl()throws RemoteException {
        
    }
    @Override
    public int update(User user) throws RemoteException {
        UserDAO ud = new UserDAO();
        if( ud.update(user))
            return 1;
        return 0;
    }

    @Override
    public int isExist(String username) throws RemoteException {
        UserDAO ud = new UserDAO();
        if( ud.isExist(username))
            return 1;
        return 0;
    }

    @Override
    public int updateImg(User user, File f) throws RemoteException, FileNotFoundException {
        UserDAO ud = new UserDAO();
        if( ud.updateImg(user, f))
            return 1;
        return 0;
    }

    @Override
    public byte[] retriveImg(User user) throws RemoteException {
        UserDAO ud = new UserDAO();
        return ud.retriveImg(user);
    }

    @Override
    public User getUserByID(int id) throws RemoteException {
         UserDAO ud = new UserDAO();
         return ud.getUserByID(id);
    }
    
    
}
