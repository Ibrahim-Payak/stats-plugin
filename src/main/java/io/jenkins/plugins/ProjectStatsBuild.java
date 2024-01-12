package io.jenkins.plugins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import javax.annotation.Nonnull;
import org.kohsuke.stapler.DataBoundConstructor;

public class ProjectStatsBuild extends BuildWrapper {

    private static final String REPORT_PATH = "/stats.html";
    private static final String PROJECT_NAME = "$PROJECT_NAME$";
    private static final String CLASSES_COUNT = "$CLASSES_COUNT$";
    private static final String LINES_COUNT = "$LINES_COUNT$";
    private static final String METHODS_COUNT = "$METHODS_COUNT$";

    @DataBoundConstructor
    public ProjectStatsBuild() {}

    @Override
    public Environment setUp(AbstractBuild build, final Launcher launcher, BuildListener listener) {
        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener)
                    throws IOException, InterruptedException {
                Stats stats = buildStats(build.getWorkspace());
                String report = generateFile(build.getProject().getDisplayName(), stats);
                File artifactsDir = build.getArtifactsDir();
                if (!artifactsDir.isDirectory()) {
                    boolean success = artifactsDir.mkdirs();
                    if (!success) {
                        listener.getLogger()
                                .println("Can't create artifacts directory at " + artifactsDir.getAbsolutePath());
                    }
                }
                String path = artifactsDir.getCanonicalPath() + REPORT_PATH;
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
                    writer.write(report);
                }
                return super.tearDown(build, listener);
            }
        };
    }

    private static Stats buildStats(FilePath root) throws IOException, InterruptedException {
        int classesCount = 0;
        int linesCount = 0;
        int methodsCount = 0;

        Stack<FilePath> toProcess = new Stack<>();
        toProcess.push(root);
        while (!toProcess.isEmpty()) {
            FilePath path = toProcess.pop();
            if (path.isDirectory()) {
                toProcess.addAll(path.list());
            } else if (path.getName().endsWith(".java")) {
                classesCount++;
                linesCount += countLines(path);
                methodsCount += countMethods(path);
            }
        }
        return new Stats(classesCount, linesCount, methodsCount);
    }

    private static int countLines(FilePath path) throws IOException, InterruptedException {
        byte[] buffer = new byte[1024];
        int result = 1;
        try (InputStream in = path.read()) {
            while (true) {
                int read = in.read(buffer);
                if (read < 0) {
                    return result;
                }
                for (int i = 0; i < read; i++) {
                    if (buffer[i] == '\n') {
                        result++;
                    }
                }
            }
        }
    }

    private static int countMethods(FilePath filePath) throws IOException, InterruptedException {
        int methodsCount = 0;

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(filePath.read(), StandardCharsets.UTF_8))) {
            String line;
            boolean insideMethod = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("public")
                        || line.startsWith("private")
                        || line.startsWith("protected")
                        || line.startsWith("static")
                        || line.startsWith("final")
                        || line.startsWith("synchronized")
                        || line.startsWith("abstract")
                        || line.startsWith("native")
                        || line.startsWith("transient")
                        || line.startsWith("volatile")) {
                    // This line may be the start of a method
                    insideMethod = true;
                }

                if (insideMethod) {
                    if (line.endsWith("{")) {
                        // This line contains the opening brace of a method
                        methodsCount++;
                        insideMethod = false;
                    } else if (line.endsWith("}") || line.endsWith(");")) {
                        // This line contains the closing brace or semicolon, indicating the end of a single-line method
                        methodsCount++;
                        insideMethod = false;
                    }
                }
            }
        }

        return methodsCount;
    }

    private static String generateFile(String projectName, Stats stats) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try (InputStream in = ProjectStatsBuild.class.getResourceAsStream(REPORT_PATH)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                bOut.write(buffer, 0, read);
            }
        }
        String content = new String(bOut.toByteArray(), StandardCharsets.UTF_8);
        content = content.replace(PROJECT_NAME, projectName);
        content = content.replace(CLASSES_COUNT, String.valueOf(stats.getClassesCount()));
        content = content.replace(LINES_COUNT, String.valueOf(stats.getLinesCount()));
        content = content.replace(METHODS_COUNT, String.valueOf(stats.getMethodsCount()));
        return content;
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Construct project stats during build";
        }
    }
}
