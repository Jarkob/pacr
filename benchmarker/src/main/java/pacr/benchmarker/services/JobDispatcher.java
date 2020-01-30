package pacr.benchmarker.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Dispatches a job.
 */
public class JobDispatcher {

    private static final Logger LOGGER = LogManager.getLogger(JobDispatcher.class);

    @Value("${runnerFile}")
    private String RUNNER_FILE_RELATIVE;
    private String runnerDir;
    private String runnerFile;

    /**
     * Initializes a new instance of JobDispatcher.
     */
    public JobDispatcher() {
        this.runnerDir = System.getProperty("user.dir");
        runnerFile = runnerDir + "/" + RUNNER_FILE_RELATIVE;
    }

    /**
     * Initializes a new instance of JobDispatcher.
     * @param runnerDir is the directory where the runner is located.
     */
    public JobDispatcher(String runnerDir) {
        this.runnerDir = System.getProperty("user.dir") + runnerDir;
        runnerFile = this.runnerDir + "/" + RUNNER_FILE_RELATIVE;
    }

    /**
     * Dispatches a job.
     * @param repositoryDir is the directory of the repository.
     * @return the benchmarking result. NULL if the benchmarking result could not be fetched.
     */
    public BenchmarkingResult dispatchJob(String repositoryDir) {
        LOGGER.info("Starting process {} with {} as argument.", RUNNER_FILE_RELATIVE, repositoryDir);

        Process process = null;
        try {
            process = new ProcessBuilder(runnerFile, repositoryDir)
                    .directory(new File(runnerDir))
                    .start();
        } catch (IOException e) {
            LOGGER.error("Could not start process.");
            return null;
        }

        LOGGER.info("Waiting for process to finish.");
        int exitCode = 0;
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
        FetchResults fetchResults = new FetchResults(process.getInputStream());

        Thread t = new Thread(fetchResults);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            LOGGER.error("Could not join thread.");
            return createBenchmarkingResult(e.getMessage());
        }

        LOGGER.info("Getting results.");
        BenchmarkingResult result = fetchResults.getResult();
        LOGGER.info("Got results.");

        return result;
    }

    private BenchmarkingResult createBenchmarkingResult(String globalError) {
        BenchmarkingResult result = new BenchmarkingResult();
        result.setGlobalError(globalError);
        return result;
    }

    /**
     * Fetches the results of the runner.
     */
    private static class FetchResults implements Runnable {

        private static final Logger LOGGER = LogManager.getLogger(FetchResults.class);

        private BenchmarkingResult result;
        private InputStream inputStream;

        FetchResults(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {

            LOGGER.info("Getting job result.");

            StringBuilder sb = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append(System.getProperty("line.separator"));
                }
            } catch (IOException e) {
                LOGGER.error("Could not read output of runner script.");
            }

            JSONToGSONAdapter adapter = new JSONToGSONAdapter();
            String gsonFormat = adapter.convertJSONToGSON(sb.toString());

            Gson g = new Gson();

            try {
                result = g.fromJson(gsonFormat, BenchmarkingResult.class);
            } catch (JsonSyntaxException e) {
                LOGGER.error("JSON syntax exception.");
                result = new BenchmarkingResult();
                result.setGlobalError(e.getMessage());
            }

        }

        /**
         * Returns the fetched result.
         * @return BenchmarkingResult.
         */
        BenchmarkingResult getResult() {
            return result;
        }
    }

}
