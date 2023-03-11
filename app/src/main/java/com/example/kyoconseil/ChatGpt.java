package com.example.kyoconseil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGpt extends AppCompatActivity {
RecyclerView recyclerView ;
TextView welcometext;
EditText messageedittext;
ImageButton sendmessage;
ImageButton sendvcl;
MessageAdapter messageAdapter;
List<Message> messageList;
private static final int REQUEST_CODE_SPEECH_INPUT =1000;

public static  final MediaType JSON = MediaType.get("application/json; charset=utf-8");
OkHttpClient client = new OkHttpClient();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gpt);


        sendvcl=findViewById(R.id.sendvcl_btn);
        recyclerView=findViewById(R.id.recyclerview);
        welcometext = findViewById(R.id.welcome_text);
        messageedittext=findViewById(R.id.message_dit_text);
        sendmessage=findViewById(R.id.send_btn);
        messageList=new ArrayList<>();

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        recyclerView.setLayoutManager(lm);


sendvcl.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        speak();
    }
});


        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = messageedittext.getText().toString().trim();

                addtoChat(question,Message.SENT_BY_ME);
                messageedittext.setText("");
                callApi(question);
                welcometext.setVisibility(View.GONE);
            }
        });


    }

    void  addtoChat(String message , String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add( new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
    void addReponse(String reponse){
        messageList.remove(messageList.size()-1);
        addtoChat(reponse,Message.SENT_BY_BOT);
    }


   private void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"say somthing");
        try {
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if (resultCode== RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String r = result.toString().substring(1,result.toString().length()-1);
                   addtoChat(r, Message.SENT_BY_ME);
                   callApi(r);
                   welcometext.setVisibility(View.GONE);
                }
            }
        }
    }

    void callApi(String question) {

messageList.add(new Message("...",Message.SENT_BY_BOT));
        JSONObject jsonbody = new JSONObject();

        try {
            jsonbody.put("model","text-davinci-003");
            jsonbody.put("prompt",question);
            jsonbody.put("max_tokens",4000);
            jsonbody.put("temperature",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    RequestBody body = RequestBody.create(jsonbody.toString(),JSON);
        Request request = new Request.Builder().url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer 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")
                .post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addReponse("Failed to load message"+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray( "choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addReponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addReponse("failed to load response");
                }

            }
        });

    }

}