package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.dilimanlabs.pitstop.Pitstop;
import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.events.GetBusinessEvent;
import com.dilimanlabs.pitstop.events.GetEstablishmentEvent;
import com.dilimanlabs.pitstop.okhttp.Headers;
import com.dilimanlabs.pitstop.persistence.Business;
import com.dilimanlabs.pitstop.persistence.Contact;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.persistence.Location;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.dilimanlabs.pitstop.ui.common.BaseFragment;
import com.dilimanlabs.pitstop.ui.widgets.PageFragmentPagerAdapter;
import com.google.gson.Gson;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.android.lifecycle.LifecycleEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class DetailsFragment extends BaseFragment {

    @InjectView(R.id.tabstrip)
    public PagerSlidingTabStrip mTabStrip;

    @InjectView(R.id.viewpager)
    public ViewPager mViewPager;

    Observable<PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse>> mEstObservable;
    Observable<PitstopService.ResponseWrapper<PitstopService.BusinessResponse>> mBizObservable;
    Observable<PitstopService.ResponseWrapper<PitstopService.PagesResponse>> mPagesObservable;

    public static DetailsFragment newInstance(final String estUrl) {
        DetailsFragment detailsFragment = new DetailsFragment();

        final Bundle args = new Bundle();

        args.putString("EST_URL", estUrl);

        detailsFragment.setArguments(args);

        return detailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        final String estUrl = getArguments().getString("EST_URL");
        final String bizUrl = estUrl.substring(0, "/businesses/12345678910".length());

        mEstObservable = getEstablishment(estUrl.substring(1)).subscribeOn(Schedulers.io()).cache();
        mBizObservable = getBusiness(bizUrl.substring(1)).subscribeOn(Schedulers.io()).cache();
        mPagesObservable = getPages(bizUrl.substring(1)).subscribeOn(Schedulers.io()).cache();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.inject(this, rootView);

        mTabStrip.setTextColorResource(R.color.white_text);

        bindLifecycle(mEstObservable, LifecycleEvent.DESTROY)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseWrapper -> {
                    if (responseWrapper.headers.contains(new Header(Headers.IS_CACHED, "False"))
                            || !responseWrapper.headers.contains(new Header(Headers.IS_CACHED, "False"))) {
                        Establishment establishment = responseWrapper.response.establishment;
                        Location location = establishment.location;
                        location.save();
                        Contact contact = establishment.contact;
                        contact.save();

                        new Establishment(establishment.categories, contact, location, establishment.name, establishment.primaryImage, establishment.url).save();
                    }
                }, error -> {

                    // TODO
                    //getActivity().finish();
                });

        bindLifecycle(mBizObservable, LifecycleEvent.DESTROY)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseWrapper -> {
                    if (responseWrapper.headers.contains(new Header(Headers.IS_CACHED, "False"))
                            || !responseWrapper.headers.contains(new Header(Headers.IS_CACHED, "False"))) {
                        Business business = responseWrapper.response.business;

                        business.save();
                    }
                }, error -> {
                    // TODO
                    //getActivity().finish();
                });

        bindLifecycle(mPagesObservable, LifecycleEvent.DESTROY)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseWrapper -> {
                    if (responseWrapper.response.pages != null) {
                        mViewPager.setAdapter(new PageFragmentPagerAdapter(getChildFragmentManager(), responseWrapper.response.pages));
                        //mViewPager.setOffscreenPageLimit(5);
                        mTabStrip.setViewPager(mViewPager);
                    }
                }, error -> {
                    // TODO
                   //getActivity().finish();
                });

        return rootView;
    }

    private Observable<PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse>> getEstablishment(String url) {
        final BehaviorSubject<PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse>> retrofitSubject =
                BehaviorSubject.<PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse>>create();

        ((Pitstop) getActivity().getApplication()).getPitstopService().getEstablishment(url, new Callback<PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse>>() {

            @Override
            public void success(PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse> responseWrapper, Response response) {
                responseWrapper.headers = response.getHeaders();
                retrofitSubject.onNext(responseWrapper);
                retrofitSubject.onCompleted();
            }

            @Override
            public void failure(RetrofitError error) {
                retrofitSubject.onError(error);
            }
        });

        return retrofitSubject;
    }

    private Observable<PitstopService.ResponseWrapper<PitstopService.BusinessResponse>> getBusiness(String url) {
        final BehaviorSubject<PitstopService.ResponseWrapper<PitstopService.BusinessResponse>> retrofitSubject =
                BehaviorSubject.<PitstopService.ResponseWrapper<PitstopService.BusinessResponse>>create();

        ((Pitstop) getActivity().getApplication()).getPitstopService().getBusiness(url, new Callback<PitstopService.ResponseWrapper<PitstopService.BusinessResponse>>() {

            @Override
            public void success(PitstopService.ResponseWrapper<PitstopService.BusinessResponse> responseWrapper, Response response) {
                responseWrapper.headers = response.getHeaders();
                retrofitSubject.onNext(responseWrapper);
                retrofitSubject.onCompleted();
            }

            @Override
            public void failure(RetrofitError error) {
                retrofitSubject.onError(error);
            }
        });

        return retrofitSubject;
    }

    private Observable<PitstopService.ResponseWrapper<PitstopService.PagesResponse>> getPages(String url) {
        final BehaviorSubject<PitstopService.ResponseWrapper<PitstopService.PagesResponse>> retrofitSubject =
                BehaviorSubject.<PitstopService.ResponseWrapper<PitstopService.PagesResponse>>create();

        ((Pitstop) getActivity().getApplication()).getPitstopService().getPages(url, new Callback<PitstopService.ResponseWrapper<PitstopService.PagesResponse>>() {

            @Override
            public void success(PitstopService.ResponseWrapper<PitstopService.PagesResponse> responseWrapper, Response response) {
                responseWrapper.headers = response.getHeaders();
                retrofitSubject.onNext(responseWrapper);
                retrofitSubject.onCompleted();
            }

            @Override
            public void failure(RetrofitError error) {
                retrofitSubject.onError(error);
            }
        });

        return retrofitSubject;
    }

    @Override
    public List<Object> getModules() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
