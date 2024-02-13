import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;

public class Application {
	
	public static final String DB_URL = ""; //Add your DB access point//
    public static final String DB_USER = "";
    public static final String DB_PASSWORD = "";

	public static void main(String[] args) throws IOException {
		InetAddress localAddress = InetAddress.getByName("127.0.0.1");
		int serverport = 8000;
		HttpServer server = HttpServer.create(new InetSocketAddress(localAddress, serverport), 0);
		
		// create default context
		server.createContext("/", new MyLoginHandler());
		server.createContext("/users", new MyLoginHandler());
		server.createContext("/registration", new MyRegistrationHandler());
		server.createContext("/logs", new MyHttpHandler());
		server.createContext("/messages", new MyHttpHandler());
		server.createContext("/update/messages", new MyHttpHandler());
		server.createContext("/update_profile", new MyHttpHandler());
		server.createContext("/operations", new MyHttpHandler());
		server.createContext("/request", new MyHttpHandler());
		server.createContext("/my/messages", new MyHttpHandler());
		
		server.start();
		System.out.println("Server started on port " + serverport);
	}
}
