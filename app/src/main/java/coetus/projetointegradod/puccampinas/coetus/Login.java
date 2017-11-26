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

public class Login extends AppCompatActivity {
    ProgressBar progressBar;
    EditText email, senha;
    Button entrar;
    Connection conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBarLogin);
        email = findViewById(R.id.emailLogin);
        senha = findViewById(R.id.senhaLogin);
        entrar = findViewById(R.id.entrarLogin);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checarLogin(v);
            }
        });
    }

    public void checarLogin(View v) {
        String emailStr = email.getText().toString();
        String senhaStr = senha.getText().toString();
        CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
        checkLogin.execute(emailStr, senhaStr);
    }

    public void abrirCadastro(View v) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, Cadastro.class);
        startActivity(intent);
    }

    public class CheckLogin extends AsyncTask<String, String, String> {
        String mensagem = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Login.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... parametros) {
            String email = parametros[0];
            String senha = parametros[1];
            if (email.trim().equals("") || senha.trim().equals(""))
                mensagem = "Entre com e-mail e senha!";
            else {
                try {
                    conexao = connectionclass();        // Connect to database
                    if (conexao == null) {
                        mensagem = "Erro de conexão com o Banco de Dados!";
                    } else {
                        // Change below query according to your own database.
                        String query = "select * from Usuarios where email = '" + email + "' and senha = '" + senha + "'";
                        Statement stmt = conexao.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            //Usuario usuario = new Usuario(rs.getString("NOME"), rs.getString("EMAIL"), rs.getString("SENHA"), rs.getString("CELULAR"));
                            //myIntent.putExtra("key", value); //Optional parameters
                            //i.putExtra("Usuario", usuario);
                            mensagem = "Boa organização, ";
                            mensagem += rs.getString("NOME") + "!";
                            isSuccess = true;
                            conexao.close();
                        } else {
                            mensagem = "E-mail ou senha inválidos!";
                            isSuccess = false;
                        }
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
