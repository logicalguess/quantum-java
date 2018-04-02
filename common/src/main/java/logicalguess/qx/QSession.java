package logicalguess.qx;

import logicalguess.util.Holder;
import logicalguess.util.QConfig;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static logicalguess.util.Utils.get;
import static logicalguess.util.Utils.post;


public class QSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(QSession.class);
    private Config conf = QConfig.load("ibm");

    private Holder<Optional<String>> sessionIdHolder;

    private String url = conf.getString("url");
    private String loginPath = conf.getString("loginPath");
    private String jobsPath = conf.getString("jobsPath");

    private JSONObject loginData = new JSONObject().put("apiToken", conf.getString("token"));

    private Optional<String> logon() {
        OkHttpClient client = new OkHttpClient();
        try {
            JSONObject session = post(client, url + loginPath, loginData);
            String id = session.getString("id");
            if (id != null && !id.equals(""))
                return Optional.of(id);
        } catch (IOException e) {
            LOGGER.error("Error logging in", e);
        }
        return Optional.empty();
    }

    public QSession() {
        sessionIdHolder = new Holder<>(() -> logon());
    }

    public void init() {
        sessionIdHolder.init();
    }

    public String run(String filePath) throws IOException {
        return run(filePath, conf.getString("backend"), conf.getInt("shots"), conf.getInt("maxCredits"));
    }

    public String run(String filePath, String backend, int shots, int maxCredits) throws IOException {

        Qasm qasm = new Qasm("");
        qasm.setQasmCodeFilePath(qasm.getQasmCodeFile().getAbsolutePath().concat(filePath));
        qasm.setQasmCodeFile(qasm.getQasmCodeFilePath());
        qasm.setQasmCode(qasm.extractQasmCode(qasm.getQasmCodeFile()));


        String[] tempQasm = {};
        try {
            tempQasm = qasm.replace("IBMQASM 2.0;", "");
            tempQasm = qasm.replace("OPENQASM 2.0;", "");
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
        }

        // Convert the code string array, String[] qasmCode, into a single string
        String tempQasmCode;
        StringBuilder stringBuilder = new StringBuilder();
        for (String lineOfCode : tempQasm) {
            stringBuilder.append(lineOfCode);
        }

        tempQasmCode = stringBuilder.toString();


        JSONObject data = new JSONObject();
        data.put("qasms", new JSONObject[]{new JSONObject().put("qasm", tempQasmCode)});
        data.put("shots", shots);
        data.put("maxCredits", maxCredits);
        data.put("backend", new JSONObject().put("name", backend));

        Map<String, String> params = new HashMap<>();
        params.put("access_token", sessionIdHolder.get().get());

        OkHttpClient client = new OkHttpClient();
        JSONObject jobResult = post(client, url + jobsPath, params, data);
        return jobResult.getString("id");
    }

    public JSONObject check(String jobId) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", sessionIdHolder.get().get());

        OkHttpClient client = new OkHttpClient();
        JSONObject jobResult = get(client, url + jobsPath + jobId, params);
        return jobResult;
    }

    public JSONObject check(String jobId, int secs) throws IOException, InterruptedException {
        JSONObject jobResult;
        do {
            TimeUnit.SECONDS.sleep(secs);
            jobResult = check(jobId);
            LOGGER.info("Job Result:\n" + jobResult);

        } while (jobResult.getString("status").equals("RUNNING") && jobResult.has("status"));

        return jobResult;
    }
}
