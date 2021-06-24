package com.example.mathcompetitions;


class Problem {
    private String question, solution, location;
    private int answer;
    private double difficulty;

    public Problem() {
    }

    Problem(int answer, double difficulty, String question, String solution) {
        this.question = question;
        this.solution = solution;
        this.answer = answer;
        this.difficulty = difficulty;
    }

    String getQuestion() {
        return question;
    }

    String getSolution() {
        return solution;
    }

    String getLocation(){
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    int getAnswer() {
        return answer;
    }

    double getDifficulty() {
        return difficulty;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }
}
