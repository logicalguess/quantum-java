package logicalguess.test;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class RunnableActor extends AbstractActor {
	
	private final Runnable runnable;
	
	public RunnableActor(Runnable runnable) {
		super();
		this.runnable = runnable;
	}
	
	public static Props props(Runnable runnable) {
        return Props.create(RunnableActor.class, runnable);
	}

	@Override
	public void preStart() throws Exception {
		runnable.run();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder().matchAny(a -> unhandled(a)).build();
	}
}