package coetus.projetointegradod.puccampinas.coetus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Cadastro extends AppCompatActivity {
    ProgressBar progressBar;
    EditText nomeET, emailET, celularET, senhaET, confSenhaET;
    Button cadastrar;
    Connection conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        progressBar = findViewById(R.id.progressBarCad);
        nomeET = findViewById(R.id.nomeCad);
        emailET = findViewById(R.id.emailCad);
        senhaET = findViewById(R.id.senhaCad);
        confSenhaET = findViewById(R.id.confSenhaCad);
        celularET = findViewById(R.id.celularCad);
        cadastrar = findViewById(R.id.cadastrarCad);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrar(v);
            }
        });
    }

    public void cadastrar(View v) {
        String nome = nomeET.getText().toString();
        String email = emailET.getText().toString();
        String senha = senhaET.getText().toString();
        String confSenha = confSenhaET.getText().toString();
        String celular = celularET.getText().toString();
        Cadastrar cadastro = new Cadastrar();// this is the Asynctask, which is used to process in background to reduce load on app process
        cadastro.execute(nome, email, senha, confSenha, celular);
    }


    public class Cadastrar extends AsyncTask<String, String, String> {
        String mensagem = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Cadastro.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... parametros) {
            String nome = parametros[0];
            String email = parametros[1];
            String senha = parametros[2];
            String confSenha = parametros[3];
            String celular = parametros[4];
            if (nome.trim().equals("") || email.trim().equals("") || senha.trim().equals("") || confSenha.trim().equals("") || celular.trim().equals(""))
                mensagem = "Todos os campos são obrigatórios!";
            else if (!senha.equals(confSenha))
                mensagem = "Senhas não conferem!";
            else {
                try {
                    conexao = connectionclass();        // Connect to database
                    if (conexao == null) {
                        mensagem = "Erro de conexão com o Banco de Dados!";
                    } else {
                        // Change below query according to your own database.
                        String query = "insert into Usuarios values('"+nome+"', '"+email+"', '"+senha+"', '"+celular+"')";
                        Statement stmt = conexao.createStatement();
                        stmt.execute(query);
                        mensagem = "Cadastro efetuado!";
                        isSuccess = true;
                        conexao.close();
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    mensagem = e.getMessage();
                }
            }
            return mensagem;
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionclass() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String stringConexao = "jdbc:jtds:sqlserver://bdprojetointegradod.database.windows.net:1433;databasename=BDProjetoIntegradoD";
            String usuario = "projetointegradod@bdprojetointegradod";
            String senha = "Projeto.Integrado.D";
            connection = DriverManager.getConnection(stringConexao, usuario, senha);
        }
        catch (SQLException se) {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e) {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
}
