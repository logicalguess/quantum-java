package logicalguess.primes;

import java.util.ArrayList;
import java.util.List;

public class PrimeServiceImpl implements PrimeService {

	public List<Long> getFirstNPrimes(int n) {
		List<Long> primes = new ArrayList<>();
		long i = 2;
		
		while (primes.size() != n) {
			if (isPrime(i)) {
				primes.add(i);
			}
			i++;
		}
		return primes;
	}

	private boolean isPrime(long n) {
		for(int i = 2; i < n; i++) {
			if(n % i == 0) {
				return false;
			}
		}
		return true;
	}
}
