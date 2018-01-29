package beta.user.camera;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity{
    public TextView usuario;
    public EditText senha;
    private AppCompatActivity context;
    public View include_form;
    public View include_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        usuario = (TextView) findViewById(R.id.usuario);
        senha = (EditText) findViewById(R.id.senha);

        include_form = findViewById(R.id.include_form);
        include_loading = findViewById(R.id.include_loading);
        include_form.setVisibility(View.VISIBLE);
        include_loading.setVisibility(View.GONE);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    public void click_login(View v){
        if(validate()){
            new SocketClient().execute(this, SocketClient.NameTask.LOGIN);
        }
    }

    public boolean validate(){
        if(usuario.getText().length() < 3){
            Toast.makeText(this,"Usuário inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(senha.getText().length() < 4){
            Toast.makeText(this,"Senha inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}

