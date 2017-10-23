package beta.user.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity{
    private TextView usuario;
    private EditText senha;
    private Context context;
    private View include_form;
    private View include_loading;

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
            UserLoginTask task = new UserLoginTask(usuario.getText().toString(), senha.getText().toString());
            task.execute();
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

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private String s_usuario, s_senha;
        UserLoginTask(String email, String password) {
            s_usuario = email;
            s_senha = password;
        }
        @Override
        protected void onPreExecute() {
            include_form.setVisibility(View.GONE);
            include_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(s_usuario.equals("admin") && s_senha.equals("admin")){
                return true;
            }
            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                Intent intent = new Intent(context, CameraActivity.class);
                startActivity(intent);
                finish();
            }else{
                include_form.setVisibility(View.VISIBLE);
                include_loading.setVisibility(View.GONE);
                Toast.makeText(context,"Login Inválido",Toast.LENGTH_LONG).show();
            }
        }
    }
}

