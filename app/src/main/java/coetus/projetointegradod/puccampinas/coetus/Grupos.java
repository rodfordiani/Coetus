package coetus.projetointegradod.puccampinas.coetus;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Grupos extends AppCompatActivity {
    ListView lista;
    int id;
    SharedPreferences configuracoes;
    Connection conexao;
    ProgressBar progressBar;
    ArrayList nomes;
    ListAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);
        lista = findViewById(R.id.listaGrupos);
        progressBar = findViewById(R.id.progressBarGrupos);
        nomes = new ArrayList();
        adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, nomes);
        lista.setAdapter(adaptador);
        lista.setBackgroundColor(Color.DKGRAY);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                         @Override
                                         public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                             String textoPosicao = String.valueOf(parent.getItemAtPosition(position));
                                             Toast.makeText(Grupos.this, textoPosicao, Toast.LENGTH_SHORT).show();
                                         }
                                     });
        buscarGrupos();
    }

    protected void buscarGrupos(){
        configuracoes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        id = configuracoes.getInt("id", 0);
        BuscaGrupos buscagrupos = new BuscaGrupos();
        buscagrupos.execute(Integer.toString(id));
    }

    public class BuscaGrupos extends AsyncTask<String, String, String> {
        String mensagem = "Inicial";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(Grupos.this, r, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            /*if(!isSuccess) {
                Toast.makeText(Grupos.this, r, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }*/

        }

        @Override
        protected String doInBackground(String... parametros) {
            id = Integer.parseInt(parametros[0]);
            if (id == 0)
                mensagem = "Erro ao procurar grupos para esta conta!";
            else {
                try {
                    conexao = connectionclass();        // Connect to database
                    if (conexao == null) {
                        mensagem = "Erro de conexão com o Banco de Dados!";
                    } else {
                        String query = "select * from Grupos where ID in (select IDGRUPO from Integrantes where IDUSUARIO =" + id + ")";
                        Statement stmt = conexao.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next()) {
                            /*Grupo grupo = new Grupo();
                            grupo.setId(rs.getInt("ID"));
                            grupo.setNome(rs.getString("NOME"));*/
                            nomes.add(rs.getString("NOME"));
                            mensagem += rs.getString("NOME");
                        }
                        /*isSuccess = true;
                        conexao.close();*/
                    }
                }
                catch (Exception e) {
                    isSuccess = false;
                    mensagem = "Erro ao recuperar grupos!";
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
