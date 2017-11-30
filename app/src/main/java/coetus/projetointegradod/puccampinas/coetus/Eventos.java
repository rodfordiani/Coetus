package coetus.projetointegradod.puccampinas.coetus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Eventos extends AppCompatActivity {
    ListView lista;
    int idGrupo;
    SharedPreferences configuracoes;
    SharedPreferences.Editor editor;
    Connection conexao;
    ArrayList nomes, ids;
    ListAdapter adaptador;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        lista = findViewById(R.id.listaEventos);
        nomes = new ArrayList();
        ids = new ArrayList();
        adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, nomes);
        lista.setAdapter(adaptador);
        lista.setBackgroundColor(Color.DKGRAY);
        configuracoes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = configuracoes.edit();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                /*intent = new Intent(getApplicationContext(), Participantes.class);*/
                String textoPosicao = String.valueOf(parent.getItemAtPosition(position));
                Toast.makeText(Eventos.this, textoPosicao, Toast.LENGTH_SHORT).show();
            }
        });
        buscarEventos();
    }

    protected void buscarEventos(){
        idGrupo = configuracoes.getInt("idGrupo", 0);
        BuscaEventos buscaeventos = new BuscaEventos();
        buscaeventos.execute(Integer.toString(idGrupo));
    }

    public class BuscaEventos extends AsyncTask<String, String, String> {
        String mensagem = "Inicial";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(Eventos.this, r, Toast.LENGTH_SHORT).show();
            /*if(!isSuccess) {
                Toast.makeText(Eventos.this, r, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }*/

        }

        @Override
        protected String doInBackground(String... parametros) {
            idGrupo = Integer.parseInt(parametros[0]);
            if (idGrupo == 0)
                mensagem = "Erro ao procurar eventos para este grupo!";
            else {
                try {
                    conexao = connectionclass();        // Connect to database
                    if (conexao == null) {
                        mensagem = "Erro de conexão com o Banco de Dados!";
                    } else {
                        String query = "select * from Eventos where ID in (select IDGRUPO from Eventos where IDGRUPO =" + idGrupo + ")";
                        Statement stmt = conexao.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        while (rs.next()) {
                            /*Evento evento = new Evento();
                            evento.setId(rs.getInt("ID"));
                            evento.setNome(rs.getString("NOME"));*/
                            nomes.add(rs.getString("NOME"));
                            ids.add(rs.getInt("ID"));
                            mensagem += rs.getString("NOME");
                        }
                        /*isSuccess = true;
                        conexao.close();*/
                    }
                }
                catch (Exception e) {
                    isSuccess = false;
                    mensagem = "Erro ao recuperar eventos!";
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