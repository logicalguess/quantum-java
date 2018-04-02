package logicalguess.primes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrimeController {
	
	private final PrimeService primeService;
	
	@Autowired
	public PrimeController(PrimeService primeService) {
		super();
		this.primeService = primeService;
	}

	@GetMapping("/primes/{primeCount}")
	public List<String> getFirstNPrimes(@PathVariable Integer primeCount) {
		return primeService.getFirstNPrimes(primeCount).stream().map(String::valueOf).collect(Collectors.toList());
	}
}
