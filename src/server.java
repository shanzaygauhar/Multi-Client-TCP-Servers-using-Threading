import java.net.*;
import java.io.*;



public class server implements Runnable {
    public static int activeClients = 0;
    static int port_num = 5656;
    static ServerSocket server_connection;
    Socket socket;

    server(Socket soc){
        socket = soc;
    }
    public static void IncreaseClients(){
        activeClients++;
    }
    public static void CloseClient(){
        activeClients--;
        if(activeClients == 0 || activeClients < 0){
            System.out.println("Waiting for clients on port " + port_num);
        }
    }
    public static void main(String args[])throws Exception {
        try {
            server_connection = new ServerSocket(port_num);
            System.out.println("Server started!");
            System.out.println("Waiting for clients on port " + port_num);

            while (true) {
                Socket client = server_connection.accept();
                String cl = client.getRemoteSocketAddress().toString();
                String[] data = cl.split("/");
                System.out.println("Got connection from " +  data[1]);
                IncreaseClients();
                System.out.println("Active Connections = " + activeClients);

                server server_ = new server(client);
                Thread thread = new Thread(server_);
                thread.start();
            }
        } catch (BindException err) {
            System.out.println("Can't listen on this port");
            return;
        }catch(IOException err){
            System.out.println("Error communicating with the client");
        }catch(Exception err){
            System.out.println("Server experienced an exception");
        }
        finally{
            server_connection.close();
        }

    }
    @Override
    public void run() {
        System.out.println();
        //ip of client
        String ip = socket.getRemoteSocketAddress().toString();
        String data[] = ip.split("/");
        String[] name = data[1].split(":");

        String ser = socket.getLocalSocketAddress().toString();
        String[] srvr  = ser.split("/");

        System.out.println("Welcome to the server " + srvr[1]);
        try {
            DataInputStream dataInput = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());

            dataOutput.writeUTF("Please select the desired operation");
            dataOutput.writeUTF("Enter 'read' if you wish to get information from server");
            dataOutput.writeUTF("Enter 'write' or 'save' if you wish to upload data");
            String choice = (String) dataInput.readUTF(); //sees choice, whether to read/write
            if(choice.equalsIgnoreCase("read")){
                String ret = "Choice: " + choice;
                dataOutput.writeUTF(ret);
                String Filename = name[0]+ ".txt";
                File output = new File(Filename);

                if(!output.exists()){
                    System.out.println("No Information found for client: " + name[0]);
                }else{
                    System.out.println("Information for client: " + name[0]);
                    BufferedReader br = new BufferedReader(new FileReader(Filename));
                    String str;
                    System.out.println();
                    while ((str = br.readLine()) != null) {
                        System.out.println(str);
                        dataOutput.writeUTF(str);
                    }
                }
            }else if(choice.equalsIgnoreCase("write") || choice.equalsIgnoreCase("save")){
                String ret = "Choice: " + choice;
                dataOutput.writeUTF(ret);

                String Filename = name[0] + ".txt";
                System.out.println(Filename);
                File output = new File(Filename);
                if(!output.exists()){
                    output.createNewFile();
                }
                // Gets Information to write on file
                String information = (String) dataInput.readUTF();
                FileWriter filehandler = new FileWriter(output.getName(),true);
                BufferedWriter head = new BufferedWriter(filehandler);
                head.write(information);
                head.close();
                System.out.println("Information saved for client " + name[0]);
            }else{
                dataOutput.writeUTF("Invalid choice");
                System.out.println("Invalid choice");
            }
        } catch (IOException e) {
            System.out.println("Error establishing Input/Output stream in the run method");
        }

        finally{
            try {
                socket.close();
                CloseClient();
            } catch (IOException e) {
                System.out.println("Error closing socket");
            }
        }

    }
}

