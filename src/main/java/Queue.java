import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.stream.javadsl.Flow;

import java.time.Duration;
import java.util.Arrays;

public class Queue {
    public static void main(String[] argv) {
        int bufferSize = 1;
        int elementsToProcess = 10;
        final ActorSystem system = ActorSystem.create("Queue");
        final Materializer materializer = ActorMaterializer.create(system);
        final Flow<Integer, Integer, UniqueKillSwitch> killswitchFlow = Flow.of(Integer.class)
                .joinMat(KillSwitches.singleBidi(), Keep.right());

        Pair<SourceQueueWithComplete<Integer>, UniqueKillSwitch> source =
                Source.<Integer>queue(bufferSize, OverflowStrategy.backpressure())
                        .throttle(elementsToProcess, Duration.ofSeconds(1))
                        .map(x -> x * x)
                        .viaMat(killswitchFlow, Keep.both())
                        .to(Sink.foreach(x -> System.out.println("got: " + x)))
                        .named("name")
                        .run(materializer);

        SourceQueueWithComplete<Integer> sourceQueue = source.first();
        UniqueKillSwitch uniqueKill = source.second();

        Source<Integer, NotUsed> sourceInteger
                = Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        sourceInteger.map(x -> sourceQueue.offer(x)).runWith(Sink.ignore(), materializer);
    }
}
