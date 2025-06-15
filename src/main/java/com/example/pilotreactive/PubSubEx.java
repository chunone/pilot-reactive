package com.example.pilotreactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Publisher --> Data1 --> Operator1 --> Data2 --> Operator2 --> Subscriber
 */
public class PubSubEx {

    public static void main(String[] args) {

        System.out.println("main Start..");

        Publisher<Integer> pub = iterPub(Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList()));

        Publisher<Integer> mapPub = mapPub(pub, (Function<Integer, Integer>) s -> s*10);
        Publisher<Integer> map2Pub = mapPub(mapPub, (Function<Integer, Integer>) s -> -s);
        map2Pub.subscribe(logSub());
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer i) {
                        sub.onNext(f.apply(i));

                    }

                    @Override
                    public void onError(Throwable t) {
                        sub.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });
            }
        };

    }

    private static Subscriber<Integer> logSub() {
        Subscriber<Integer> sub = new Subscriber<Integer>(){

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe() ");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext() : " + integer );
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError() : " + t);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete() ");
            }
        };
        return sub;
    }

    private static Publisher<Integer> iterPub(List<Integer> iterParam) {
        Publisher<Integer> pub = new Publisher<Integer>(){

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                System.out.println("subscribe() ..");
                Iterable<Integer> iter = iterParam;

                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        // 구독자가 발행자에게 필요한 만큼 데이터를 요청
                        // 효율적으로 리소스를 관리

                        try {
                            System.out.println("request");
                            iter.forEach(item -> sub.onNext(item));

                            // 작업을 완료했음
                            sub.onComplete();

                        }catch(Throwable e){

                            sub.onError(e);
                        }



                    }

                    @Override
                    public void cancel() {
                        System.out.println("cancel");
                        // 구독취소
                    }
                });

            }
        };
        return pub;
    }
}
