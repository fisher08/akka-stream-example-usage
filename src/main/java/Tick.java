import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.japi.Pair;
import akka.japi.function.Function;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import scala.concurrent.duration.FiniteDuration;

import java.nio.file.Paths;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class Tick {
    private static final FiniteDuration delay = scala.concurrent.duration.Duration.create(5000, TimeUnit.MILLISECONDS);
    private static final FiniteDuration interval = scala.concurrent.duration.Duration.create(2000, TimeUnit.MILLISECONDS);

    public static void main(String[] argv) {
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);
        final Source<Integer, NotUsed> source1 = Source.range(1, 10);
        final Source<Integer, NotUsed> source2 = Source.range(200, 300);

        Source<String,Cancellable> source = Source.tick(delay, interval, "test string" );


        final CompletionStage<Done> done =
                source.runForeach(i -> System.out.println(i), materializer);

        done.thenRun(() -> system.terminate());

    }
}