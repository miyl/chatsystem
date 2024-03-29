package chat.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import chat.server.User;
import chat.libs.Connection;
import java.util.Optional;
import java.util.Collections;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer {

  /**
   * Static fields
   */
  private static final int PORT = 6666;
  private static final int MAX_CAPACITY = 5;

  /**
   * Fields
   */
  // TODO: Why not lock on 'users'?
  private final Object lock = new Object();
  private final ExecutorService threadPool;
  private final ServerSocket serverSocket;
  private final List<User> users;

  public ChatServer(final int port) throws IOException {
    threadPool = Executors.newFixedThreadPool(MAX_CAPACITY);
    serverSocket = new ServerSocket(port);
    users = new ArrayList<>(MAX_CAPACITY);

    System.out.println("Starting server on port " + port + "...");
  }

  public void receiveMessage(String msg, ClientHandler ch) {
    // TODO: Do lexer/parser validation here to figure out which command and if it's valid
    //System.out.println("Message received!");

    // Send the message to all clients except the one it was received from
    for (User u : users) {
      if ( ! u.getClientHandler().equals(ch) ) {
        u.getClientHandler().sendMessage(msg);
      }
    }
  }

  // Untested
  public void removeUser(ClientHandler ch) {
    for (User u : users) {
      if ( u.getClientHandler().equals(ch) ) removeUser(u);
    }
  }


  public void acceptClients() {
    // TODO: Is 'users' or 'threadPool' responsible for gatekeeping the capacity?
    while (users.size() <= MAX_CAPACITY) {
      try {
        Socket clientSocket = serverSocket.accept();
        System.out.println("A new connection from " + clientSocket + "!");

        // Prepare to spawn a new thread for that user
        ClientHandler ch = new ClientHandler(clientSocket, this);

        // Keep track of all users/client in the users list
        addUser( new User(ch) );

        threadPool.submit(ch);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    try {
      ChatServer server = new ChatServer(PORT);
      server.acceptClients();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // FOR HANDLING THE USER LIST
  // TODO: ReEntrant lock might be better than synchronized? Testing needed.

  // TODO: Handle (prevent) user name clashes in some fashion, and return false in that case
  public boolean addUser(User user) {
    synchronized(lock) {
      users.add(user);
      return true;
    }
  }

  public void removeUser(User user) {
    synchronized(lock) {
      users.remove(user);
    }
  }

  public List<User> getUsers() {
    synchronized(lock) {
      return Collections.unmodifiableList(users);
    }
  }

  // TODO: Use Map<String, User> to avoid linear lookup.
  public Optional<User> getUser(String userName) {
    synchronized(lock) {
      for (var u : users) {
        if ( u.getUserName().equals(userName) ) return Optional.of(u);
      }
      return Optional.empty(); // In case no user by that name was found.
    }
  }
}
