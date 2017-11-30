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

public class Participacoes extends AppCompatActivity {
    ListView lista;
    int idEvento, idUsuario;
    SharedPreferences configuracoes;
    SharedPreferences.Editor editor;
    Connection conexao;
    ArrayList funcoes;
    ArrayList usuarios;
    ArrayList participacoes;
    ListAdapter adaptador;
    Intent intent;
    String participacao;
    Boolean isParticipante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participacoes);
        lista = findViewById(R.id.listaParticipacoes);
        funcoes = new ArrayList();
        usuarios = new ArrayList();
        participacoes = new ArrayList<>();
        adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, participacoes);
        lista.setAdapter(adaptador);
        lista.setBackgroundColor(Color.DKGRAY);
        configuracoes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = configuracoes.edit();
        isParticipante = false;

        buscarParticipacoes();
    }

    protected void buscarParticipacoes(){
        idEvento = configuracoes.getInt("idEvento", 0);
        idUsuario = configuracoes.getInt("idUsuario", 0);
        BuscaParticipacoes buscaparticipacoes = new BuscaParticipacoes();
        buscaparticipacoes.execute(Integer.toString(idEvento));
    }

    public class BuscaParticipacoes extends AsyncTask<String, String, String> {
        String mensagem = "Inicial";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(Participacoes.this, r, Toast.LENGTH_SHORT).show();
            /*if(!isSuccess) {
                Toast.makeText(Participacoes.this, r, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }*/

        }

        @Override
        protected String doInBackground(String... parametros) {
            idEvento = Integer.parseInt(parametros[0]);
            if (idEvento == 0)
                mensagem = "Erro ao procurar participacoes para este eventp!";
            else {
                try {
                    conexao = connectionclass();        // Connect to database
                    if (conexao == null) {
                        mensagem = "Erro de conexão com o Banco de Dados!";
                    } else {
                        String query = "SELECT NOME FROM FUNCOES WHERE ID IN" +
                                "(SELECT IDFUNCAO FROM PARTICIPACOES WHERE IDEVENTO = "+ idEvento + ")";
                        Statement stmt = conexao.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        while(rs.next())
                            funcoes.add(rs.getString("NOME"));
                        query = "SELECT NOME FROM USUARIOS WHERE ID IN" +
                                "(SELECT IDUSUARIO FROM PARTICIPACOES WHERE IDEVENTO = "+ idEvento + ")";
                        stmt = conexao.createStatement();
                        rs = stmt.executeQuery(query);
                        while(rs.next())
                            usuarios.add(rs.getString("NOME"));
                        query = "SELECT IDUSUARIO FROM PARTICIPACOES WHERE IDUSUARIO = " + idUsuario;
                        stmt = conexao.createStatement();
                        rs = stmt.executeQuery(query);
                        if(rs.next()) {
                            isParticipante = true;
                            mensagem = "É PARTICIPANTE";
                        }
                        else
                            mensagem = "NÃO É PARTICIPANTE";
                        isSuccess = true;
                        conexao.close();
                        for(int i = 0; i < funcoes.size(); i++) {
                            participacao = (String) usuarios.get(i);
                            participacao += " - " + funcoes.get(i);
                            participacoes.add(participacao);
                        }
                    }
                }
                catch (Exception e) {
                    isSuccess = false;
                    mensagem = "Erro ao recuperar participacoes!";
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