/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Dieu Dai Hiep
 */
public class Server extends Thread {

    private int port;
    private ArrayList<ServerWorker> workerList = new ArrayList<ServerWorker>();

    public Server(int port) {
        this.port = port;
    }

    public ArrayList<ServerWorker> getWorkerList() {
        return workerList;
    }

    public ServerWorker getWorkerByID(int id) {
        for (ServerWorker worker : workerList) {
            if (worker.getUser().getId() == id) {
                return worker;
            }
        }
        return null;
    }

    public void OpenServer() {

    }

    @Override
    public void run() {
        OpenServer();
        try {
            ServerSocket myServer = new ServerSocket(port);
            //show msg
            System.out.println("Server is opening in port: " + port + ".");
            while (true) {
                Socket clientSocket = myServer.accept();
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void removeWorker(ServerWorker worker) {
        workerList.remove(worker);
    }

}
