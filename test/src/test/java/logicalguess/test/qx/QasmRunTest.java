package logicalguess.test.qx;

import logicalguess.qx.QSession;
import logicalguess.util.StatsBarChart;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static logicalguess.util.Utils.getCounts;
import static logicalguess.util.Utils.plotCounts;
import static org.junit.Assert.assertEquals;

public class QasmRunTest {

    private static final Logger LOG = LoggerFactory.getLogger(QasmRunTest.class);

    @Test
    public void runQasm() throws IOException, InterruptedException {
        QSession session = new QSession();
        session.init();
        String jobId = session.run("/src/test/resources/teleportation.qasm");
        JSONObject result = session.check(jobId, 7);

        assertEquals("COMPLETED", result.getString("status"));

        plotCounts("Teleportation", getCounts(result), StatsBarChart.Type.PROBS);
        System.in.read();
    }


}
