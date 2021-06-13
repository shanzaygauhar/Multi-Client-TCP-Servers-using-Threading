import java.net.*;
import java.io.*;

public class client {
    public static void main(String arg[]) throws Exception
    {
      try {
          Socket clientSocket = new Socket("localhost", 5656);
          DataOutputStream ServerOutput = new DataOutputStream(clientSocket.getOutputStream());
          DataInputStream ServerInput = new DataInputStream(clientSocket.getInputStream());

          //Client IP and Port
          String ip = clientSocket.getLocalSocketAddress().toString();
          String data[] = ip.split("/");
          String[] name = data[1].split(":");

          //Server IP and Port
          ip = clientSocket.getRemoteSocketAddress().toString();
          data = ip.split("/");
          String[] Servername = data[1].split(":");

          System.out.println("Client " + name[0] + " is active");

          //receives data from server
          String info = (String) ServerInput.readUTF();
          System.out.println(info);
          info = (String) ServerInput.readUTF();
          System.out.println(info);
          info = (String) ServerInput.readUTF();
          System.out.println(info);

          //inputs and send user choice of action to server
          BufferedReader UserInput = new BufferedReader(new InputStreamReader(System.in));
          String input = UserInput.readLine();
          ServerOutput.writeUTF(input);

          //Displays user choice
          System.out.println(ServerInput.readUTF());

          if (input.equalsIgnoreCase("read")) {
              //Filename for client
              String ClientFilename = name[0] + "_" + Servername[0] + ".txt";
              try {
                  File file = new File(ClientFilename);
                  if (!file.exists()) {
                      file.createNewFile();
                      System.out.println("File created: " + file.getName());
                  } else {
                      System.out.println("File already exists.");
                  }
                  String sentence = ServerInput.readUTF();
                  System.out.println(sentence);
                  FileWriter writer = new FileWriter(file);
                  //BufferedWriter bufferedWriter = new BufferedWriter(writer);
                  writer.write(sentence);
                  writer.close();
              } catch (IOException e) {
                  System.out.println("Client Side: Error in File Handling");
              }

          } else if (input.equalsIgnoreCase("write") || input.equalsIgnoreCase("save")) {
              input = UserInput.readLine();
              ServerOutput.writeUTF(input); //send data to server
          } else {
              System.out.println("Invalid Action");
          }
          clientSocket.close();
          ServerInput.close();
          ServerOutput.close();
      }catch(ConnectException err){
          System.out.println("Error establishing Clients Connection");
      }
    }
}
