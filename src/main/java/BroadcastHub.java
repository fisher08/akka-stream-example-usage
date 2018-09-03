import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class BroadcastHub  {
    public static void main(String[] argv) {
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);
        final Source<Integer, NotUsed> source = Source.range(1, 100);

        Source<String, Cancellable> producer = Source.tick(
                FiniteDuration.create(1, TimeUnit.SECONDS),
                FiniteDuration.create(1, TimeUnit.SECONDS),
                "New message"
        );

        // Attach a BroadcastHub Sink to the producer. This will materialize to a
        // corresponding Source.
        // (We need to use toMat and Keep.right since by default the materialized
        // value to the left is used)
        RunnableGraph<Source<String, NotUsed>> runnableGraph =
                producer.toMat(akka.stream.javadsl.BroadcastHub.of(String.class, 256), Keep.right());

        // By running/materializing the producer, we get back a Source, which
        // gives us access to the elements published by the producer.
        Source<String, NotUsed> fromProducer = runnableGraph.run(materializer);

        // Print out messages from the producer in two independent consumers
        fromProducer.runForeach(msg -> System.out.println("consumer1: " + msg), materializer);
        fromProducer.runForeach(msg -> System.out.println("consumer2: " + msg), materializer);
        system.terminate();
    }
}
