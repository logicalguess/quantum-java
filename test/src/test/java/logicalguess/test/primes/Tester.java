package logicalguess.test.primes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import logicalguess.test.RunnableActor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Tester {
	
	private static final Logger LOG = LoggerFactory.getLogger(Tester.class);
	
	private OkHttpClient client = new OkHttpClient();
	private Request request = new Request.Builder()
			.url("http://localhost:8080/primes/20")
			.build();
	private Map<Integer, Instant> inTimes = new ConcurrentHashMap<>();
	private Map<Integer, Instant> outTimes = new ConcurrentHashMap<>();

	private ActorSystem actorSystem;

	@Before
	public void setUp() {
		actorSystem = ActorSystem.create();
	}

	@After
	public void tearDown() {
		actorSystem.terminate();
		actorSystem = null;
	}

	@Test
	public void loadTest() throws InterruptedException {
		
		IntStream.range(0, 1000).parallel().forEach(i -> {
			actorSystem.actorOf(RunnableActor.props(() -> {
				try {
					inTimes.put(i, Instant.now());
					Response response = client.newCall(request).execute();
					assertEquals(200, response.code());
					response.close();
					outTimes.put(i, Instant.now());
				} catch (IOException e) {
					fail("Whoops");
				}
			}));
		});

		while (inTimes.size() != 1000 && outTimes.size() != 1000 ) {
			TimeUnit.MILLISECONDS.sleep(100);
			LOG.info("in: " + inTimes.size() + ", out: " + outTimes.size());
		}

		TimeUnit.MILLISECONDS.sleep(1000);
		buildReport();
	}

	private void buildReport() {
		Map<Integer, Long> latencyMap = new HashMap<>();
		long totalLatency = 0L;
		
		for (Integer i : inTimes.keySet()) {
			long latency = Duration.between(inTimes.get(i), outTimes.get(i)).toMillis();
            latencyMap.put(i, latency);
            totalLatency += latency;
		}
		
        Instant first = inTimes.values().stream().min(Instant::compareTo).get();
        Instant last = outTimes.values().stream().max(Instant::compareTo).get();
        
        Long minLatency = latencyMap.values().stream().min(Long::compareTo).get();
        Long maxLatency = latencyMap.values().stream().max(Long::compareTo).get();
        
        List<Long> sortedList = latencyMap.values().stream().sorted(Long::compareTo).collect(Collectors.toList());
        
        int ninetyNinthPercentile = ( (sortedList.size() / 100) * 99);
        LOG.info("******************************************************************************");
        LOG.info("Total Messages processed: {}" , inTimes.size());
        LOG.info("Min Latency: {}ms" , minLatency);
        LOG.info("Max Latency: {}ms" , maxLatency);
        LOG.info("Mean Latency: {}ms" , totalLatency / inTimes.size());
        LOG.info("95th Percentile: {}ms" , sortedList.get(ninetyNinthPercentile));
        LOG.info("Processed: {} messages in {} seconds ({} messages per second.)" , inTimes.size(),
                new BigDecimal(Duration.between(first, last).toMillis()).divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP),
                // (inputTimes.size()) / (Duration.between(first, last).toMillis()/1000)
                (new BigDecimal(inTimes.size())).divide(new BigDecimal(Duration.between(first, last).toMillis()).divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP));
        LOG.info("******************************************************************************");

	}
	
}
