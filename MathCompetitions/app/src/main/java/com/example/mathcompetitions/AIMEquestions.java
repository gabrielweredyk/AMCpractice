package com.example.mathcompetitions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AIMEquestions extends AppCompatActivity {

    EditText answer;
    Button submit;

    WebView webView;
    boolean loaded = false;
    boolean answered = false;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("AIME");

    ArrayList<Problem> problems = new ArrayList<>();
    int problemNum = 0;
    Problem currentProblem;


    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aimequestions);

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        problems.clear();
                        for (DataSnapshot i: dataSnapshot.getChildren()){
                            for (DataSnapshot j: i.getChildren()){
                                Problem problem = new Problem();
                                problem.setQuestion(j.child("Question").getValue(String.class));
                                problem.setAnswer(j.child("Answer").getValue(Integer.class));
                                problem.setSolution(j.child("Solution").getValue(String.class));
                                problem.setDifficulty(j.child("Difficulty").getValue(Double.class));
                                problem.setLocation("From 20" + i.getKey() + ", Question " + j.getKey());
                                problems.add(problem);
                            }
                        }

                        changeQuestion();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


        answer = findViewById(R.id.answer);
        submit = findViewById(R.id.submit);
        webView = findViewById(R.id.questionWebView);

        /*questionWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });*/

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webView.loadUrl("file:///android_asset/index.html");



    }


    public void setText(String text){
        final String displayedText = text;
        if (loaded) {
            webView.loadUrl("javascript:(function(){document.getElementById(\"mathParagraph\").innerHTML = \"" + displayedText + "\";MathJax.typeset();})()");
            return;
        }
        webView.setWebViewClient(
                new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        webView.loadUrl("javascript:(function(){document.getElementById(\"mathParagraph\").innerHTML = \"" + displayedText + "\";MathJax.typeset();document.body.style.backgroundColor = \"#222\"})()");

                        loaded = true;
                    }
                }
        );

    }

    public void click(View view){
        answer.setEnabled(answered);
        if (answered) {
            answered = false;
            changeQuestion();
            submit.setText("SUBMIT");
            return;
        }
        submit.setText("NEXT");
        int response;
        try {
            response = Integer.parseInt(answer.getText().toString());
        }
        catch (Exception e){
            response = -1;
        }
        answer.setText("");
        if (response == currentProblem.getAnswer()){
            setText("<h3>You got it right!</h3> <br> The answer was " + currentProblem.getAnswer() + "<br> <h3> Only " + currentProblem.getDifficulty() + "% of students got this right!</h3>");
        }
        else {
            setText("<h3>You got it wrong...</h3> <br>" + currentProblem.getSolution()+ "<br> <h3> Only " + currentProblem.getDifficulty() + "% of students got this right...</h3>");
        }
        answered = true;
    }

    public void changeQuestion(){
        problemNum = (int) (Math.random() * problems.size());

        currentProblem = problems.get(problemNum);
        setText(currentProblem.getQuestion());

//        problemNum++; problemNum %= problems.size();

    }


}
