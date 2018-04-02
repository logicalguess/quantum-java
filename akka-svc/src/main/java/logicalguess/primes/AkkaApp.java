package logicalguess.primes;

import static akka.http.javadsl.server.PathMatchers.integerSegment;
import static akka.http.javadsl.server.PathMatchers.segment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class AkkaApp extends AllDirectives {
	
	private static final Logger LOG = LoggerFactory.getLogger(AkkaApp.class);
	private final ConfigurableApplicationContext applicationContext;
	private final PrimeService primeService;
	
	public AkkaApp() {
		applicationContext = new ClassPathXmlApplicationContext("classpath:context.xml");
		primeService = applicationContext.getBean("primeService", PrimeService.class);
	}

	public static void main(String[] args) throws Exception {
		// boot up server using the route as defined below
		ActorSystem system = ActorSystem.create("routes");

		final Http http = Http.get(system);
		final ActorMaterializer materializer = ActorMaterializer.create(system);

		//In order to access all directives we need an instance where the routes are define.
		AkkaApp app = new AkkaApp();

		final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
		final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
				ConnectHttp.toHost("0.0.0.0", 8080), materializer);

		LOG.info("Server online at http://localhost:8080/\nPress RETURN to stop...");
		System.in.read(); // let it run until user presses return

		binding
			.thenCompose(ServerBinding::unbind) // trigger unbinding from the port
			.thenAccept(unbound -> system.terminate()); // and shutdown when done
	}

	private Route createRoute() {
		return route(
				pathPrefix(segment("primes").slash(integerSegment()), primeCount ->
					get(() ->
							completeOKWithFuture(
									CompletableFuture.<List<String>>supplyAsync(() ->
										primeService.getFirstNPrimes(primeCount)
										.stream()
										.map(String::valueOf)
										.collect(Collectors.toList())), Jackson.<List<String>>marshaller())
						)
					)
				);
	}
}
