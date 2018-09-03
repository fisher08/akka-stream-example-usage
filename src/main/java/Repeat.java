import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Repeat {
    private static final FiniteDuration delay = scala.concurrent.duration.Duration.create(5000, TimeUnit.MILLISECONDS);
    private static final FiniteDuration interval = scala.concurrent.duration.Duration.create(2000, TimeUnit.MILLISECONDS);

    public static void main(String[] argv) {
        final ActorSystem system = ActorSystem.create("Unfold");
        final Materializer materializer = ActorMaterializer.create(system);

        final Source<Long, NotUsed> numbers = Source.repeat(1L);

        numbers
                .throttle(1, interval, 1, ThrottleMode.shaping())
                .runForeach(System.out::println, materializer);
    }
}
