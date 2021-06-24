package com.example.mathcompetitions;

public class MCProblem extends Problem {

    private String[] options;

    public MCProblem() {
    }

    public MCProblem(int answer, double difficulty, String question, String solution, String[] options) {
        super(answer, difficulty, question, solution);
        this.options = options;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
}
