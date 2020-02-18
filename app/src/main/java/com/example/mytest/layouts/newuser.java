package com.example.mytest.layouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytest.R;
import com.example.mytest.firebaseclass.UserModel;
import com.example.mytest.firebaseclass.firebaseuser;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class newuser extends AppCompatActivity {

    Button btn;
    EditText EditText_mail, EditText_pw, EditText_pw2, EditText_name;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "newuserActivity";
    String email;
    String name;
    String pw;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_0newuser);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        EditText_mail = findViewById(R.id.text_newuser_mail);
        EditText_pw = findViewById(R.id.text_newuser_pw);
        EditText_pw2 = findViewById(R.id.text_newuser_pw2);
        EditText_name = findViewById(R.id.text_newuser_name);
        btn = (Button) findViewById(R.id.btn_회원가입확인);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(EditText_mail.getText().toString().length() != 0 && EditText_pw.getText().length() != 0) { //값을 모두 적었을때
                    if (EditText_pw.getText().toString().equals(EditText_pw2.getText().toString())) { //비밀번호 값이 서로 같을때

                        email = EditText_mail.getText().toString();
                        pw = EditText_pw.getText().toString();
                        name = EditText_name.getText().toString();


                        if(EditText_pw.getText().length() < 6){
                            Toast.makeText(newuser.this, "비밀번호는 6자리 이상입니다", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                UserModel userModel = new UserModel();
                                userModel.userName = name;
                                userModel.mail = email;
                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String uid = task.getResult().getUser().getUid();
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                            }
                        });


                        //store에서 확인
                        final DocumentReference docRef = db.collection("Lets_study").document("Users");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {//문서 불러오기 성공
                                    Map<String, Object> document = task.getResult().getData();
                                    if (document== null) { //기존 값이 아무것도 없을때
                                        document = new HashMap<>();

                                        final Shared_user shared_user = new Shared_user(name, email, pw);
                                        final Shared_class shared_class = new Shared_class();
                                        String newemail = email.replace(".","_");
                                        document.put(newemail, shared_class.touser(shared_user));
                                        db.collection("Lets_study").document("Users");
                                        docRef.set(document).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                SharedPreferences  Prefs = getSharedPreferences(Shared_user.user,MODE_PRIVATE);
                                                SharedPreferences.Editor prefsEditor = Prefs.edit();
                                                prefsEditor.putString(email, shared_class.touser(shared_user));
                                                prefsEditor.apply();
                                                Intent intent = new Intent(newuser.this, idpw.class);
                                                intent.putExtra("email", email);
                                                intent.putExtra("pw", pw);
                                                startActivity(intent);
                                                Toast.makeText(newuser.this, "회원가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });

                                    } else { //기존 값이 있을때
                                        String newemail = email.replace(".","_");
                                        if(document.containsKey(newemail)){ //이메일이 중복될때
                                            Toast.makeText(newuser.this, "중복된 이메일입니다", Toast.LENGTH_SHORT).show();
                                        }else{ //이메일이 중복되지 않을때
                                            final Shared_user shared_user = new Shared_user(name, email, pw);
                                            final Shared_class shared_class = new Shared_class();
                                            document.put(newemail, shared_class.touser(shared_user));
                                            db.collection("Lets_study").document("Users");
                                            docRef.set(document).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SharedPreferences  Prefs = getSharedPreferences(Shared_user.user,MODE_PRIVATE);
                                                    SharedPreferences.Editor prefsEditor = Prefs.edit();
                                                    prefsEditor.putString(email, shared_class.touser(shared_user));
                                                    prefsEditor.apply();
                                                    Intent intent = new Intent(newuser.this, idpw.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("pw", pw);
                                                    startActivity(intent);
                                                    Toast.makeText(newuser.this, "회원가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    Toast.makeText(newuser.this, "firebase store에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });






//                        if (savemail != null){
//                            Toast.makeText(newuser.this, "중복된 아이디입니다.",Toast.LENGTH_SHORT).show();
//
//                            return;
//
//                        }





                        //Toast.makeText(newuser.this, "회원가입 완료했습니다 로그인해주세요", Toast.LENGTH_SHORT).show();



                        ////////////////////////////////////////////////////////



                        ////////////////////////////////////////////////////////
                        //mAuth = FirebaseAuth.getInstance();
                        //signup(email, pw, json, shared_user);



                    } else {
                        Toast.makeText(newuser.this, "비밀번호를 다시 확인하세요", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(newuser.this, "모두 필수 입력해야합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btn = (Button) findViewById(R.id.btn_newuser_x);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //값 가져오기
        //클릭을 감지하기
        //가져온값을 다음 액티비티로 넘기기


    }

    //firebase signup
    private void signup(final String email, final String password, final String json, final Shared_user jsonOb){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            //성공했을때 로직
                            //updateUI(user);
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            assert user != null;
//                            String userid = user.getUid();
//
//                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
//
//                            firebaseuser firebaseuser = new firebaseuser(name, email, password);
//
//                            reference.setValue(firebaseuser).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.i("newuser_json", json);
//                                    Intent intent = new Intent(newuser.this, idpw.class);
//                                    intent.putExtra("email", email);
//                                    intent.putExtra("pw", pw);
//                                    startActivity(intent);
//                                }
//                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //실패했을때 로직
                            Toast.makeText(newuser.this, "중복된 회원입니다.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LifeCycle", "onStop(newuser)");
    }




}
