/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.server;

import dao.FriendDAO;
import dao.FriendReqDAO;
import dao.GroupDAO;
import dao.GroupMsgDAO;
import dao.MessageDAO;
import dao.UserDAO;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import model.Group;
import model.GroupMsg;
import model.Message;
import model.ObjectWrapper;
import model.User;

/**
 *
 * @author Dieu Dai Hiep
 */
public class ServerWorker extends Thread {

    private Socket clientSocket;
    private User user;
    private Server server;
    // private HashSet<String> topicSet = new HashSet<>();
    private UserDAO ud;
    private String res;
    private String[] tokens;
    private FriendReqDAO fd;
    private FriendDAO frd;
    private MessageDAO msgd;
    private GroupDAO gd;
    private GroupMsgDAO gmd;
    private String msg;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        ud = new UserDAO();
        fd = new FriendReqDAO();
        frd = new FriendDAO();
        msgd = new MessageDAO();
        gd = new GroupDAO();
        gmd = new GroupMsgDAO();
    }

    public User getUser() {
        return user;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public void sendData(Object obj) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(obj);
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }

    public void sendDataToUser(Object obj, int userId) {
        ServerWorker worker = server.getWorkerByID(userId);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(worker.getSocket().getOutputStream());
            oos.writeObject(obj);
        } catch (Exception e) {
            // e.printStackTrace();
        }

    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public ArrayList<User> getListOnlineFriendById(int id) {
        //get all onlineUser
        ArrayList<ServerWorker> listOnlineWorker = server.getWorkerList();
        ArrayList<User> listOnline = new ArrayList<>();
        for (ServerWorker worker : listOnlineWorker) {
            if (worker.getUser() != null) {
                listOnline.add(worker.getUser());
            }
        }
        // get all friend by id
        ArrayList<User> listFriend = frd.showListFriend(id);
        // get all online-friend User
        ArrayList<User> listOnlineFriend = new ArrayList<>();
        for (int i = 0; i < listOnline.size(); i++) {
            for (int j = 0; j < listFriend.size(); j++) {
                if (listOnline.get(i).getId() == listFriend.get(j).getId()) {
                    listOnlineFriend.add(listOnline.get(i));
                }
            }
        }
        return listOnlineFriend;
    }

    public ArrayList<User> getListOnlineFromGroup(int gr_id) {
        ArrayList<ServerWorker> listOnlineWorker = server.getWorkerList();
        ArrayList<User> listOnline = new ArrayList<>();
        for (ServerWorker worker : listOnlineWorker) {
            if (worker.getUser() != null) {
                listOnline.add(worker.getUser());
            }
        }
        // get all User in Group by gr_id
        ArrayList<User> listUser = gmd.getListUserOfGroup(gr_id);
        // get all online-friend User
        ArrayList<User> listOnlineFromGroup = new ArrayList<>();
        for (int i = 0; i < listOnline.size(); i++) {
            for (int j = 0; j < listUser.size(); j++) {
                if (listOnline.get(i).getId() == listUser.get(j).getId()) {
                    listOnlineFromGroup.add(listOnline.get(i));
                }
            }
        }
        return listOnlineFromGroup;

    }

    public void UpdateOnlineFriendListByID(int id) {
        ArrayList<User> listOnlineFriend = getListOnlineFriendById(id);
        sendDataToUser(new ObjectWrapper(ObjectWrapper.LIST_ONLINE_FRIEND, listOnlineFriend), id);
    }

    public void UpdateAllOnlineFriendList() {
        // get-list-online-user
        ArrayList<User> listOnlineFriend = getListOnlineFriendById(user.getId());
        //  sendData(new ObjectWrapper(ObjectWrapper.LIST_ONLINE_FRIEND, listOnlineFriend));
        // notify to all friend User
        for (int i = 0; i < listOnlineFriend.size(); i++) {
            if (listOnlineFriend.get(i).getUsername() != user.getUsername()) {
                //
                int frend_id = listOnlineFriend.get(i).getId();
                ArrayList<User> listOnlineFriendForEachUser = getListOnlineFriendById(frend_id);
                sendDataToUser(new ObjectWrapper(ObjectWrapper.LIST_ONLINE_FRIEND, listOnlineFriendForEachUser), frend_id);

            }
        }
    }

    public void sendListGroup() {
        ArrayList<Group> listGroup = gd.getGroupsByUserID(this.getUser().getId());
        sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_GROUP, listGroup));
    }

    public void sendListMsg(int from_id, String to_username) {
        try {
            ArrayList<Message> listMsg = msgd.getListMessage(from_id, to_username);
            sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_MESSAGE, listMsg));
        } catch (Exception e) {
            sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_MESSAGE, "false"));

        }

    }

    public void sendListGroupMsg(int gr_id) {
        try {
            ArrayList<GroupMsg> list = gmd.getListGrMsgByID(gr_id);
            sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_GROUP_MESSAGE, list));
        } catch (Exception e) {
            sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_GROUP_MESSAGE, "false"));
        }
    }
    

    @Override
    public void run() {
        try {
            while (true) {
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                //get Object 
                Object o = ois.readObject();
                if (o instanceof ObjectWrapper) {
                    ObjectWrapper data = (ObjectWrapper) o;
                    switch (data.getPerformative()) {
                        case ObjectWrapper.LOGIN:
                            User user = (User) data.getData();
                            if (ud.checkLogin(user)) {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN, "ok " + user.getId()));
                                //set User for ServerWorker
                                this.user = user;
                                //show msg
                                User u = ud.getUserByID(user.getId());
                                sendData( new ObjectWrapper( ObjectWrapper.REPLY_GET_USER, u));
                                
                                showMessage(user.getUsername() + " has join the chat.");
                                // take Online Friend List
                                UpdateOnlineFriendListByID(user.getId());
                                //update Online Friend List for allUser
                                UpdateAllOnlineFriendList();

                                // take all Group of User
                                sendListGroup();
                                //send notify :"well-come"
                                sendData(new ObjectWrapper(ObjectWrapper.NOTIFY_TO_USER, "Well come!"));

                            } else {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN, "false"));
                            }
                            break;
                        case ObjectWrapper.SIGNUP:
                            User user_sign_up = (User) data.getData();
                            UserDAO ud_sign_up = new UserDAO();
                            String userName = user_sign_up.getUsername();
                            if (!ud_sign_up.isExist(userName)) {
                                if (ud_sign_up.signUp(user_sign_up)) {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_SIGNUP, "ok"));
                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_SIGNUP, "false"));
                                }
                            } else {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_SIGNUP, "exist"));
                            }
                            break;
                        case ObjectWrapper.SEARCH_USER_BY_NAME:
                            res = (String) data.getData();
                            tokens = res.split(" ", 2);
                            String username = tokens[0];
                            String key = tokens[1];
                            ArrayList<User> list = ud.getFindListUser(username, key);
                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_USER, list));
                            break;
                        case ObjectWrapper.ADD_FRIEND_REQUEST:
                            res = (String) data.getData();
                            tokens = res.split(" ", 2);
                            int from_id = Integer.parseInt(tokens[0]);
                            int to_id = Integer.parseInt(tokens[1]);
                            //exist
                            if (fd.isExist(from_id, to_id)) {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADDFRIEND_REQUEST, "exist"));

                            } else {
                                if (fd.addRequest(from_id, to_id)) {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADDFRIEND_REQUEST, "ok"));
                                    // notify to user
                                    String fromUser = ud.getUserByID(from_id).getUsername();
                                    msg = "You have got a new friend request form " + fromUser;
                                    sendDataToUser(new ObjectWrapper(ObjectWrapper.NOTIFY_TO_USER, msg), to_id);

                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADDFRIEND_REQUEST, "false"));
                                }
                            }
                            break;
                        case ObjectWrapper.SHOW_FRIEND_REQUEST:
                            res = (String) data.getData();
                            int to_id_rq = Integer.parseInt(res);
                            ArrayList<User> list_user_rq = fd.showListUserReq(to_id_rq);
                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_FRIEND_REQUEST, list_user_rq));
                            break;
                        case ObjectWrapper.ADD_FRIEND:
                            res = (String) data.getData();
                            tokens = res.split(" ", 2);
                            int from = Integer.parseInt(tokens[0]);
                            int to = Integer.parseInt(tokens[1]);
                            if (!frd.isExist(from, to)) {
                                if (frd.addFriend(from, to) && frd.addFriend(to, from)) {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADD_FRIEND, "ok"));
                                    // notify to user
                                    String us = ud.getUserByID(from).getUsername();
                                    msg = us + " has accepct your request";
                                    sendDataToUser(new ObjectWrapper(ObjectWrapper.NOTIFY_TO_USER, msg), to);

                                    //updateStatus
                                    UpdateOnlineFriendListByID(from);
                                    UpdateOnlineFriendListByID(to);

                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADD_FRIEND, "false"));
                                }
                            } else {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADD_FRIEND, "exist"));
                            }

                            break;
                        case ObjectWrapper.SHOW_LIST_FRIEND:
                            res = (String) data.getData();
                            int my_id = Integer.parseInt(res);
                            ArrayList<User> listFriend = frd.showListFriend(my_id);
                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_FRIEND, listFriend));
                            break;

                        case ObjectWrapper.SEND_MESSAGE:
                            res = (String) data.getData();
                            tokens = res.split(" ", 3);
                            int msg_from_id = Integer.parseInt(tokens[0]);
                            String msg_to_username = tokens[1];
                            msg = tokens[2];
                            //insert to db successfull
                            if (msgd.addMsg(msg_from_id, msg_to_username, msg)) {
                                // update list msg for from_user
                                sendListMsg(msg_from_id, msg_to_username);

                                // select User message to ( update in the ListOnlineFriend )
                                int msg_to_id = ud.getIdByUsername(msg_to_username);
                                String msg_from_username = ud.getUserByID(msg_from_id).getUsername();
                                sendDataToUser(new ObjectWrapper(ObjectWrapper.SELECT_USER_MESSAGE_TO, msg_from_username), msg_to_id);
                                // update list msg for to_user
                                ArrayList<Message> listMsg = msgd.getListMessage(msg_from_id, msg_to_username);
                                sendDataToUser(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_MESSAGE, listMsg), msg_to_id);
                                //error
                            } else {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_MESSAGE, "false"));

                            }

                            break;
                        case ObjectWrapper.SHOW_LIST_MESSAGE:
                            res = (String) data.getData();
                            tokens = res.split(" ");
                            int slm_from_id = Integer.parseInt(tokens[0]);
                            String slm_to_username = tokens[1];
                            sendListMsg(slm_from_id, slm_to_username);
                            break;
                        case ObjectWrapper.UPDATE_STATUS:
                            res = (String) data.getData();
                            // send all user in the server
                            for (ServerWorker worker : server.getWorkerList()) {
                                User u = worker.getUser();
                                if (u != null) {
                                    sendDataToUser(new ObjectWrapper(ObjectWrapper.UPDATE_NEWS, res), u.getId());
                                }
                            }
                            break;
                        case ObjectWrapper.ADD_GROUP:
                            String gr_name = (String) data.getData();
                            if (!gd.isGroupExist(gr_name)) {
                                if (gd.addGroup(gr_name)) {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADD_GROUP, "ok"));
                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADD_GROUP, "false"));
                                }

                            } else {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_ADD_GROUP, "exist"));
                            }
                            break;
                        case ObjectWrapper.JOIN_GROUP:
                            res = (String) data.getData();
                            tokens = res.split(" ", 2);
                            int user_id = Integer.parseInt(tokens[0]);
                            String join_gr_name = tokens[1];
                            if (!gd.isGroupExist(join_gr_name)) {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_JOIN_GROUP, "GroupNoExist"));
                            } else {
                                int gr_id = gd.getIDByName(join_gr_name);
                                if (!gd.isJoinExist(user_id, gr_id)) {
                                    if (gd.join(user_id, gr_id)) {
                                        sendData(new ObjectWrapper(ObjectWrapper.REPLY_JOIN_GROUP, "ok"));
                                        //update list group
                                        sendListGroup();
                                    } else {
                                        sendData(new ObjectWrapper(ObjectWrapper.REPLY_JOIN_GROUP, "false"));
                                    }
                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_JOIN_GROUP, "JoinExist"));
                                }

                            }
                            break;
                        case ObjectWrapper.SHOW_LIST_GROUP:
                            sendListGroup();
                            break;
                        case ObjectWrapper.SEND_GROUP_MESSAGE:
                            res = (String) data.getData();
                            tokens = res.split(" ", 3);
                            int user_id_gr_msg = Integer.parseInt(tokens[0]);
                            int gr_id_gr_msg = gd.getIDByName(tokens[1]);
                            String gr_msg = tokens[2];
                            String gr_name_gr_msg = tokens[1];
                            // sendlistGroupMsg to User
                            if (gmd.addGrMsg(user_id_gr_msg, gr_id_gr_msg, gr_msg)) {

                                //Updaate ListGroupMsg to all user of group
                                // get lits Msg of Group
                                ArrayList<GroupMsg> listGrMsg = gmd.getListGrMsgByID(gr_id_gr_msg);
                                // get all User online of Group
                                ArrayList<User> listUserOnlineFromGroup = getListOnlineFromGroup(gr_id_gr_msg);
                                //send List Msg of Group to All User Online && Set index (Group msg to) of all User
                                for (User u : listUserOnlineFromGroup) {
                                    sendDataToUser(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_GROUP_MESSAGE, listGrMsg), u.getId());
                                    sendDataToUser(new ObjectWrapper(ObjectWrapper.SELECT_GROURP_MESSAGE_TO,gr_name_gr_msg ), u.getId());
                                }
                            } else {
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_SHOW_LIST_GROUP_MESSAGE, "false"));
                            }

                            break;
                        case ObjectWrapper.SHOW_LIST_GROUP_MESSAGE:
                            res = (String) data.getData();
                            tokens = res.split(" ", 2);
                            //send list msg for user
                            sendListGroupMsg(gd.getIDByName(tokens[1]));
                            break;
                        case ObjectWrapper.QUIT:
                            // show Message
                            clientSocket.close();
                            server.removeWorker(this);
                            showMessage("User " + this.getUser().getUsername() + " has left the chat.");
                            UpdateAllOnlineFriendList();
                            this.stop();
                            break;
                    }
                }
            }
            // disconnect 
        } catch (Exception e) {
           //  e.printStackTrace();
        }
    }

}
