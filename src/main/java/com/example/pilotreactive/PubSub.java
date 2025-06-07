package com.example.pilotreactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubSub {
    public static void main(String[] args) {

        System.out.println("Start");

        Publisher<Integer> pub = new Publisher<Integer>(){


            @Override
            public void subscribe(Subscriber<? super Integer> sub) {

                Iterable<Integer> iter = Stream.iterate(-1, i->i+1).limit(10).collect(Collectors.toList());

                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        // 구독자가 발행자에게 필요한 만큼 데이터를 요청
                        // 효율적으로 리소스를 관리

                        System.out.println("request");


                    }

                    @Override
                    public void cancel() {
                        System.out.println("cancel");
                        // 구독취소
                    }
                });

            }
        };

        Subscriber<Integer> sub = new Subscriber<Integer>(){

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe : " + s);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext : " + integer );
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError : " + t);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

    }
}
