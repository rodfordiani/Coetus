package coetus.projetointegradod.puccampinas.coetus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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

public class ParticiparGrupo extends AppCompatActivity {

    ProgressBar progressBar;
    EditText chaveET;
    Button participar;
    int id, idGrupo;
    Connection conexao;
    SharedPreferences configuracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participar_grupo);
        progressBar = findViewById(R.id.progressBarParticiparGrupo);
        chaveET = findViewById(R.id.chave);
        participar = findViewById(R.id.participarGrupoChave);
        configuracoes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        participar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participarGrupo(v);
            }
        });
    }

    public void participarGrupo(View v){
        String chave = chaveET.getText().toString();
        Participar participar = new Participar();// this is the Asynctask, which is used to process in background to reduce load on app process
        participar.execute(chave);

    }


    public class Participar extends AsyncTask<String, String, String> {
        String mensagem = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ParticiparGrupo.this, r, Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                Intent intent = new Intent(getApplicationContext(), Grupos.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... parametros) {
            id = configuracoes.getInt("id", 0);
            String chave = parametros[0];
            if (chave.trim().equals(""))
                mensagem = "Entre com a chave do grupo!";
            else {
                try {
                    conexao = connectionclass();        // Connect to database
                    if (conexao == null) {
                        mensagem = "Erro de conexão com o Banco de Dados!";
                    } else {
                        // Change below query according to your own database.
                        Statement stmt = conexao.createStatement();
                        String query = "select id from Grupos where chave = '" +chave+ "'";
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next()) {
                            idGrupo = rs.getInt("ID");
                            query = "insert into Integrantes values(" + id + ", " + idGrupo + ", 'false');";
                            stmt.execute(query);
                            mensagem = "Participação incluída com sucesso!";
                            isSuccess = true;
                        }
                        else{
                            mensagem = "Grupo não encontrado!";
                            isSuccess = false;
                        }
                        conexao.close();
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    mensagem = "Erro ao tentar participar do grupo!";
                    e.printStackTrace();
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