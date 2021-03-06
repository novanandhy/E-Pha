package com.example.novan.tugasakhir.login_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.novan.tugasakhir.MainActivity;
import com.example.novan.tugasakhir.R;
import com.example.novan.tugasakhir.util.database.AppConfig;
import com.example.novan.tugasakhir.util.database.AppController;
import com.example.novan.tugasakhir.util.database.DataHelper;
import com.example.novan.tugasakhir.util.database.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    Button login;
    EditText username, password;
    String username_string, password_string;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    DataHelper db;
    Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //retrieve form text
        username = (EditText) findViewById(R.id.username_login);
        password = (EditText) findViewById(R.id.password_login);

        //progress dialog show
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //SQLite Handler
        db = new DataHelper(getApplicationContext());

        //session manager handle
        sessionManager = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (sessionManager.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Log.d(TAG,"user already Logged in");
            finish();
        }

        login = (Button) findViewById(R.id.btn_login2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_string = username.getText().toString().trim();
                password_string = password.getText().toString().trim();

                // Check for empty data in the form
                if (!username_string.isEmpty() && !password_string.isEmpty()) {
                    // login user
                    checkLogin(username_string, password_string);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Tolong isi semua form", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void checkLogin(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        progressDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        sessionManager.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String previllage = user.getString("previllage");
                        String username = user.getString("username");
                        String created_at = user.getString("created_at");
                        String image = user.getString("image");

                        Log.d(TAG,image.toString());

                        //decode base64 to byte array
                        byte[] image_data = Base64.decode(image, Base64.DEFAULT);

                        // Inserting row in users table
                        db.addUser(uid, name, previllage, username, created_at, image_data);

                        //move to intent if successful
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, LoginregisterActivity.class);
        startActivity(intent);
        finish();
    }
}