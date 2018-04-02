package logicalguess.test.qx;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static logicalguess.util.Utils.getCounts;
import static logicalguess.util.Utils.parseJSONFile;
import static org.junit.Assert.assertEquals;

public class ResponseTest {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseTest.class);

    @Test
    public void parseResult() throws IOException, InterruptedException {
        JSONObject result = parseJSONFile("src/test/resources/result_simple.json");
        assertEquals("COMPLETED", result.getString("status"));

        JSONObject counts = getCounts(result);
        assertEquals(2, counts.length());
    }
}
