package io.jenkins.plugins;

public class Stats {

    private final int classesCount;
    private final int linesCount;
    private final int methodsCount;

    public Stats(int classesCount, int linesCount, int methodsCount) {
        this.classesCount = classesCount;
        this.linesCount = linesCount;
        this.methodsCount = methodsCount;
    }

    public int getClassesCount() {
        return classesCount;
    }

    public int getLinesCount() {
        return linesCount;
    }

    public int getMethodsCount() {
        return methodsCount;
    }
}
