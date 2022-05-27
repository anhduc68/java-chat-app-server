/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.util.Scanner;
import rmi_controller.IUser;
import rmi_controller.UserImpl;

/**
 *
 * @author Dieu Dai Hiep
 */
public class ServerMain {

    public static void main(String[] args) {
        
        Scanner in = new Scanner(System.in);
        try {
             System.out.println("Enter the port number: ");
             int port = in.nextInt();
             Server server = new Server(port);
             server.start();
             IUser u = new UserImpl();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("IP Server: 127.0.0.1 and " +InetAddress.getLocalHost().getHostAddress());
			Naming.rebind("rmi://127.0.0.1/user", u);
			System.out.println("Done Binding");
        } catch (java.rmi.UnknownHostException x) {
			x.printStackTrace();
		} catch (java.rmi.RemoteException x) {
			x.printStackTrace();
		} catch (java.net.MalformedURLException x) {
			x.printStackTrace();
		} catch (Exception x) {
			x.printStackTrace();
		}
    }
}
