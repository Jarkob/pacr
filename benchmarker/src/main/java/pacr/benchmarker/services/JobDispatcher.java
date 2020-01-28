package pacr.benchmarker.services;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;


import java.io.*;

public class JobDispatcher {

    private static final Logger LOGGER = LogManager.getLogger(JobDispatcher.class);
    private static final String RUNNER_FILE = "test.bat";

    private String runnerDir;
    private String runnerFile;

    public JobDispatcher() {
        this.runnerDir = System.getProperty("user.dir");
        runnerFile = runnerDir + "/" + RUNNER_FILE;
    }

    public JobDispatcher(String runnerDir) {
        this.runnerDir = System.getProperty("user.dir") + runnerDir;
        runnerFile = this.runnerDir + "/" + RUNNER_FILE;
    }

    public BenchmarkingResult dispatchJob(String repositoryDir) throws IOException, InterruptedException {
        LOGGER.info("Starting process {} with {} as argument.", RUNNER_FILE, repositoryDir);
        Process process = new ProcessBuilder(runnerFile, repositoryDir)
                .directory(new File(runnerDir))
                .start();

        LOGGER.info("Waiting for process to finish.");
        int exitCode = process.waitFor();
        LOGGER.info("Job finished with exit code {}.", exitCode);


        if (exitCode != 0) {
            return new BenchmarkingResult();
        }
        FetchResults fetchResults = new FetchResults(process.getInputStream());

        Thread t = new Thread(fetchResults);
        t.start();

        t.join();

        return fetchResults.getResult();
    }

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

            System.out.println("gna gna gna gna gna");
            System.out.println(sb.toString());
            System.out.println("gna gna gna gna gna");

            JSONToGSONAdapter adapter = new JSONToGSONAdapter();
            String gsonFormat = adapter.convertJSONToGSON(sb.toString());

            Gson g = new Gson();

            result = g.fromJson(gsonFormat, BenchmarkingResult.class);
        }

        BenchmarkingResult getResult() {
            return result;
        }
    }

}
