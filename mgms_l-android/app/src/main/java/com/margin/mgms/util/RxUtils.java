package com.margin.mgms.util;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on March 31, 2016.
 *
 * @author Marta.Ginosyan
 */
@SuppressWarnings("unused")
public class RxUtils {

    public static final int DURATION_DEBOUNCE = 500;
    /**
     * {@link Observable.Transformer} that transforms the source observable to subscribe in the
     * io thread and observe on the Android's UI thread.
     */
    @SuppressWarnings("all")
    private static final Observable.Transformer IO_TO_MAIN_THREAD_SCHEDULER_TRANSFORMER =
            observable -> ((Observable) observable)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
    @SuppressWarnings("all")
    private static final Observable.Transformer SKIP_FIRST_TEXT_CHANGE_DEBOUNCE_TRANSFORMER =
            o -> ((Observable<CharSequence>) o)
                    .skip(1)
                    .debounce(RxUtils.DURATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread());

    private RxUtils() {
    }

    /**
     * Get {@link Observable.Transformer} that transforms the source observable to subscribe in
     * the io thread and observe on the Android's UI thread.
     *
     * @return {@link Observable.Transformer}
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyIOtoMainThreadSchedulers() {
        return (Observable.Transformer<T, T>) IO_TO_MAIN_THREAD_SCHEDULER_TRANSFORMER;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applySkipFirstTextChangeDebounceTransformer() {
        return (Observable.Transformer<T, T>) SKIP_FIRST_TEXT_CHANGE_DEBOUNCE_TRANSFORMER;
    }

}
