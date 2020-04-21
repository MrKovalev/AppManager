package com.titanium.app_manager.base;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<T extends BaseView> {

    protected T view;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void attachView(BaseView view) {
        this.view = (T) view;

        onViewAttached();
    }

    protected void onViewAttached() { }

    public void detachView() {
        this.view = null;

        compositeDisposable.clear();
    }

    protected void disposeOnDestroy(Disposable disposable) {
        this.compositeDisposable.add(disposable);
    }
}
