package pacr.benchmarker.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jni.Proc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Dispatches a job.
 */
@Component
public class JobDispatcher {

    private static final Logger LOGGER = LogManager.getLogger(JobDispatcher.class);

    private String runnerDir;
    private String runnerFile;

    /**
     * Initializes a new instance of JobDispatcher.
     */
    public JobDispatcher(@Value("${runnerFile}") String runnerFile, @Value("${runnerDir}") String runnerDir) {
        this.runnerDir = System.getProperty("user.dir") + "/" + runnerDir;
        this.runnerFile = runnerFile;
    }

    /**
     * Dispatches a job.
     * @param repositoryDir is the directory of the repository.
     * @return the benchmarking result. NULL if the benchmarking result could not be fetched.
     */
    public BenchmarkingResult dispatchJob(String repositoryDir) {
        LOGGER.info("Starting process {} with {} as argument.", runnerFile, repositoryDir);

        // check if windows
        String os = System.getProperty("os.name").toLowerCase();
        String runner;
        if (os.contains("win")) {
            runner = runnerDir + "/" + runnerFile;
        } else {
            runner = "./" + runnerFile;
        }

        Process process;
        try {
            process = new ProcessBuilder(runner, repositoryDir)
                    .directory(new File(runnerDir))
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Could not start process.");
            return createBenchmarkingResult(e.getMessage());
        }

        String output = readInputBuffer(process);

        LOGGER.info("Waiting for process to finish.");
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException while waiting for process.");
            return createBenchmarkingResult(e.getMessage());
        }

        LOGGER.info("Job finished with exit code {}.", exitCode);
        if (exitCode != 0) {
            LOGGER.error("Exit code not 0.");
            return createBenchmarkingResult("Exit code " + exitCode);
        }

        LOGGER.info("Got {} as result.", output);

        JSONToGSONAdapter adapter = new JSONToGSONAdapter();
        String gsonFormat = adapter.convertJSONToGSON(output);

        Gson g = new Gson();

        BenchmarkingResult result;
        try {
            result = g.fromJson(gsonFormat, BenchmarkingResult.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("JSON syntax exception for this output: '{}'.", output);
            result = new BenchmarkingResult();
            result.setError(e.getMessage());
        }

        return result;
    }

    private String readInputBuffer(Process process) {
        // get stdin
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            LOGGER.error("Could not read output of runner script.");
        }
        return sb.toString();
    }

    private BenchmarkingResult createBenchmarkingResult(String globalError) {
        BenchmarkingResult result = new BenchmarkingResult();
        result.setError(globalError);
        return result;
    }

}
