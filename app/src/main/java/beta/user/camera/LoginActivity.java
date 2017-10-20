package beta.user.camera;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    private void attemptLogin() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        finish();

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        UserLoginTask(String email, String password) {

        }
        @Override
        protected Boolean doInBackground(Void... params) {

            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

        }
        @Override
        protected void onCancelled() {

        }
    }
}

