package coetus.projetointegradod.puccampinas.coetus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    Button grupos, criarGrupoHome, participarGrupoHome;
    TextView titulo;
    String email, nome;
    SharedPreferences configuracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        grupos = findViewById(R.id.gruposHome);
        criarGrupoHome = findViewById(R.id.criarGrupoHome);
        participarGrupoHome = findViewById(R.id.participarGrupoHome);
        titulo = findViewById(R.id.tituloHome);
        configuracoes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        email = configuracoes.getString("email", null);
        nome = configuracoes.getString("nome", null);
        titulo.setText("Bem-vind*, " +nome+"!");
    }

    public void criarGrupo(View v) {
        Intent intent = new Intent(this, CriarGrupo.class);
        startActivity(intent);
    }

    public void verGrupos(View v) {
        Intent intent = new Intent(this, Grupos.class);
        startActivity(intent);
    }

    public void participarGrupo(View v) {
        Intent intent = new Intent(this, ParticiparGrupo.class);
        startActivity(intent);
    }
}
