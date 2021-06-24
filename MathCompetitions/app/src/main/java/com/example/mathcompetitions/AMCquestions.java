package com.example.mathcompetitions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AMCquestions extends AppCompatActivity {

    TextView location;

    LinearLayout postAnswerButtons;
    Button solution;
    String answerDisplay;
    boolean correct;

    LinearLayout choiceHolder;
    ArrayList<WebView> choices = new ArrayList<>();
    boolean[] answerLoads;

    WebView questionWebView;
    WebSettings webSettings;
    boolean loaded = false;
    boolean answered = false;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    ArrayList<MCProblem> problems = new ArrayList<>();
    int problemNum = 0;
    MCProblem currentProblem;


    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amcquestions);

        Intent intent = getIntent();
        reference =  database.getReference("AMC" + intent.getIntExtra("Grade", 10));

        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        problems.clear();
                        for (DataSnapshot i: dataSnapshot.getChildren()){
                            for (DataSnapshot j: i.getChildren()){
                                MCProblem problem = new MCProblem();
                                problem.setQuestion(j.child("Question").getValue(String.class));
                                problem.setAnswer(j.child("Answer").getValue(Integer.class));
                                problem.setSolution(j.child("Solution").getValue(String.class));
                                problem.setDifficulty(j.child("Difficulty").getValue(Double.class));
                                String[] options = new String[5];
                                int index = 0;
                                for (DataSnapshot k: j.child("Choices").getChildren()){
                                    options[index] = k.getValue(String.class);
                                    index++;
                                }
                                problem.setOptions(options);
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

        questionWebView = findViewById(R.id.questionWebView);

        webSettings = questionWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        questionWebView.loadUrl("file:///android_asset/index.html");

        choiceHolder = findViewById(R.id.choiceHolder);
        for (int i = 0; i < choiceHolder.getChildCount(); i++){
            choices.add((WebView) choiceHolder.getChildAt(i));
            choices.get(i).getSettings().setJavaScriptEnabled(true);
            choices.get(i).loadUrl("file:///android_asset/index.html");
            final int index = i;
            choices.get(index).setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            correct = index == currentProblem.getAnswer();
                            if (correct){
                                setText("<h3>You got it right!</h3>The answer was " + currentProblem.getOptions()[currentProblem.getAnswer()] + "<br><h3>Only " + currentProblem.getDifficulty() + "% of students got this right");
                            }
                            else {
                                setText("<h3>You got it wrong...</h3>The answer was " + currentProblem.getOptions()[currentProblem.getAnswer()] + "<br><h3>Only " + currentProblem.getDifficulty() + "% of students got this right...");
                            }

                            choiceHolder.setTranslationX(1000);
                            postAnswerButtons.setTranslationX(0);

                            return false;
                        }
                    }
            );
        }
        answerLoads = new boolean[choices.size()];

        postAnswerButtons = findViewById(R.id.postAnswerButtons);
        location = findViewById(R.id.location);

        solution = findViewById(R.id.solution);
        solution.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (solution.getText().toString().equals("Show Solution")){
                            if (correct){
                                setText("<h3>You got it right!</h3>" + currentProblem.getSolution() + "<br><h3>Only " + currentProblem.getDifficulty() + "% of students got this right");
                            }
                            else {
                                setText("<h3>You got it wrong...</h3>" + currentProblem.getSolution() + "<br><h3>Only " + currentProblem.getDifficulty() + "% of students got this right...");
                            }
                            solution.setText("Hide Solution");
                        }
                        else {
                            if (correct){
                                setText("<h3>You got it right!</h3>The answer was " + currentProblem.getOptions()[currentProblem.getAnswer()] + "<br><h3>Only " + currentProblem.getDifficulty() + "% of students got this right");
                            }
                            else {
                                setText("<h3>You got it wrong...</h3>The answer was " + currentProblem.getOptions()[currentProblem.getAnswer()] + "<br><h3>Only " + currentProblem.getDifficulty() + "% of students got this right...");
                            }
                            solution.setText("Show Solution");
                        }
                    }
                }
        );

    }

    public void setText(String text){
        final String displayedText = text;
        if (loaded) {
            questionWebView.loadUrl("javascript:(function(){document.getElementById(\"mathParagraph\").innerHTML = \"" + displayedText + "\";MathJax.typeset();})()");
            return;
        }
        questionWebView.setWebViewClient(
                new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        questionWebView.loadUrl("javascript:(function(){document.getElementById(\"mathParagraph\").innerHTML = \"" + displayedText + "\";MathJax.typeset();document.body.style.backgroundColor = \"#222\"})()");
                        loaded = true;
                    }
                }
        );

    }

    public void changeAnswers(final String[] options){
        for (int i = 0; i < choices.size(); i++){
            if (answerLoads[i]){
                choices.get(i).loadUrl("javascript:(function(){document.getElementById(\"mathParagraph\").innerHTML = \"" + options[i] + "\";MathJax.typeset();})()");
                continue;
            }
            final int index = i;
            choices.get(index).setWebViewClient(
                    new WebViewClient(){
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            choices.get(index).loadUrl("javascript:(function(){document.getElementById(\"mathParagraph\").innerHTML = \"" + options[index] + "\";MathJax.typeset();})()");
                            answerLoads[index] = true;
                        }
                    }
            );
        }
//        System.out.println(Arrays.toString(options));
    }

    public void changeQuestion(){
        problemNum = (int) (Math.random() * problems.size());

        currentProblem = problems.get(problemNum);
        setText(currentProblem.getQuestion());


        changeAnswers(currentProblem.getOptions());

        solution.setText("Show Solution");
        location.setText(currentProblem.getLocation());
//        problemNum++; problemNum %= problems.size();

    }

    public void showChoices(View view){

        changeQuestion();

        choiceHolder.setTranslationX(0);
        postAnswerButtons.setTranslationX(1000);

    }

}
