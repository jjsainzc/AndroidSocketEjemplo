package cec.android.curso.socketejemplo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Socket socket = null;
    private String server;
    private BufferedReader entrada = null;
    private PrintWriter salida = null;

    private StringBuffer sb;
    private TextView resultado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sb = new StringBuffer();
        resultado = (TextView) findViewById(R.id.resultado);
        resultado.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.conectar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            server = ((EditText) findViewById(R.id.server)).getText().toString();
                            socket = new Socket(InetAddress.getByName(server), 4445);
                            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            salida = new PrintWriter(socket.getOutputStream());
                            sb.append("Conectado\n");
                        } catch (IOException e) {
                            sb.append("Error de entrada/salida\n").append(e.toString());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resultado.setText(sb.toString());
                                resultado.setFocusable(true);
                            }
                        });
                    }
                }).start();
            }
        });

        findViewById(R.id.desconectar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desconectar();
                sb.append("Desconectado\n");
                resultado.setText(sb.toString());
                resultado.setFocusable(true);
            }
        });

        findViewById(R.id.limpiar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sb.setLength(0);
                resultado.setText("");
                resultado.setFocusable(true);
            }
        });

        findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String respuesta = null;
                            salida.println(((EditText) findViewById(R.id.datos2)).getText().toString());
                            salida.flush();
                            respuesta = entrada.readLine();
                            sb.append("Respuesta del server : ").append(respuesta).append("\n");
                        } catch (NullPointerException | IOException e) {
                            sb.append("ERROR transporte del socket").append("\n");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resultado.setText(sb.toString());
                                resultado.setFocusable(true);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void desconectar() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        desconectar();
    }
}

