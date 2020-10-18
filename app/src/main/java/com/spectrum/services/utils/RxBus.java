package com.spectrum.services.utils;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Abins Shaji on 05/02/18.
 */

public class RxBus {

    public RxBus() {
    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }
    public Boolean hasObservable()
    {
        return bus.hasObservers();
    }

}
