import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

public class Flow {
    public static void main(String[] argv) {
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);
        final Source<Integer, NotUsed> source = Source.range(1, 100);

        final CompletionStage<Done> source1 = source.toMat(Sink.foreach((data) -> System.out.println("Source 1 :" + data.toString())), Keep.right())
              .run(materializer);
        final CompletionStage<Done> source2 = source.toMat(Sink.foreach((data) -> System.out.println("Source 2 :" + data.toString())), Keep.right())
                .run(materializer);

        source1.toCompletableFuture().join();
        source2.toCompletableFuture().join();
        System.out.println("Stop main");
        system.terminate();
    }
}
