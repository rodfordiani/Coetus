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
import java.util.Random;

public class CriarGrupo extends AppCompatActivity {
    EditText nomeET, descricaoET;
    String email, nome, descricao;
    Button criarCriarGrupo;
    ProgressBar progressBar;
    int idUsuario, idGrupo;
    SharedPreferences configuracoes;
    Connection conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_grupo);
        nomeET = findViewById(R.id.nomeCriarGrupo);
        descricaoET = findViewById(R.id.descricaoCriarGrupo);
        criarCriarGrupo = findViewById(R.id.criarCriarGrupo);
        progressBar = findViewById(R.id.progressBarCriarGrupo);
        configuracoes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        email = configuracoes.getString("email", null);

        criarCriarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarGrupo(v);
            }
        });
    }

    public void criarGrupo(View v) {
        nome = nomeET.getText().toString();
        descricao = descricaoET.getText().toString();
        CriaGrupo criacaoGrupo = new CriaGrupo();// this is the Asynctask, which is used to process in background to reduce load on app process
        criacaoGrupo.execute(nome, descricao);
    }

    public class CriaGrupo extends AsyncTask<String, String, String> {
        String mensagem = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(CriarGrupo.this, r, Toast.LENGTH_SHORT).show();
            Intent intent;
            if (isSuccess){
                intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... parametros) {
            String chave;
            nome = parametros[0];
            descricao = parametros[1];
            if (email.trim().equals("") || descricao.trim().equals("")) {
                mensagem = "Entre com nome e descrição!";
                isSuccess = false;
            }
            else {
                try {
                    conexao = connectionclass();        // Connect to database
                    if (conexao == null) {
                        mensagem = "Erro de conexão com o Banco de Dados!";
                    } else {
                        String query = "select id from Usuarios where email = '" + email + "'";
                        Statement stmt = conexao.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            idUsuario = rs.getInt("ID");
                            chave = gerarChave();
                            query = "insert into Grupos values(" +idUsuario+ ", '"+nome+"', '"+descricao+"', '"+chave+"')";
                            stmt.execute(query);
                            query = "select id from Grupos where chave = '"+chave+"'";
                            rs = stmt.executeQuery(query);
                            if (rs.next()) {
                                idGrupo = rs.getInt("ID");
                                query = "insert into Integrantes values(" +idUsuario+ ", " +idGrupo+ ", 'true')";
                                stmt.execute(query);
                                mensagem = "Grupo criado com sucesso!";
                                isSuccess = true;
                                conexao.close();
                            }
                        } else {
                            mensagem = "Erro ao criar grupo!";
                            isSuccess = false;
                        }
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    mensagem = "Erro ao criar grupo!";
                }
            }
            return mensagem;
        }

        protected String gerarChave(){
            String caracteres = "ABCDEFGHJKLMNPQRSTUVYWXZabcdefghijkmnpqrstuvwxyz23456789";

            Random random = new Random();

            String chave = "";

            int index;
            for( int i = 0; i < 10; i++ ) {
                index = random.nextInt(caracteres.length()-1);
                    chave += caracteres.substring(index, index+1);
            }
            return chave;
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
