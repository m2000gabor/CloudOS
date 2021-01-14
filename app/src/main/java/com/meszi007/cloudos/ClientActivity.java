package com.meszi007.cloudos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {

    Thread otherDevice = null;
    EditText etIP, etPort;
    TextView allMessages;
    EditText myMessage;
    Button btnSend;
    String SERVER_IP;
    int SERVER_PORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        allMessages = findViewById(R.id.tvMessages);
        myMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allMessages.setText("");
                SERVER_IP = etIP.getText().toString().trim();
                SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
                otherDevice = new Thread(new CreateSocketThread());
                otherDevice.start();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = myMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    new Thread(new SendMessageThread(message)).start();
                }
            }
        });
    }

    private DataOutputStream output;
    private DataInputStream input;

    class CreateSocketThread implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new DataOutputStream(socket.getOutputStream());
                input =new DataInputStream(socket.getInputStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allMessages.setText("Connected\n");
                    }
                });
                new Thread(new MessageReceiverThread()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class MessageReceiverThread implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {
                    final String message = input.readUTF();
                    if (message != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allMessages.append("server: " + message + "\n");
                            }
                        });
                    } else{
                        otherDevice = new Thread(new CreateSocketThread());
                        otherDevice.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendMessageThread implements Runnable {
        private String message;

        SendMessageThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    allMessages.append("client: " + message + "\n");
                    myMessage.setText("");
                }
            });
        }
    }

}