package com.dilimanlabs.pitstop.retrofit;

import com.dilimanlabs.pitstop.persistence.Business;
import com.dilimanlabs.pitstop.persistence.Category;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.persistence.Meta;
import com.dilimanlabs.pitstop.persistence.Page;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

public interface PitstopService {
    public static final String API_URL = "http://pitstop.dilimanlabs.com/api";

    @POST("/login/")
    ResponseWrapper<AccountSigninResponse> accountSignin(@Body AccountWrapper accountWrapper);

    @POST("/account/")
    ResponseWrapper<AccountSignupResponse> accountSignup(@Body AccountWrapper accountWrapper);

    @GET("/categories")
    void getCategories(Callback<ResponseWrapper<CategoriesResponse>> cb);

    @GET("/markers/{tile}/")
    void listEstablishmentsFromTile(@Path(value = "tile", encode = false) String tile, Callback<ResponseWrapper<MarkersResponse>> cb);

    @GET("/{businessUrl}")
    void getBusiness(@Path(value="businessUrl", encode=false) String businessUrl, Callback<ResponseWrapper<BusinessResponse>> cb);

    @GET("/{establishmentUrl}")
    void getEstablishment(@Path(value="establishmentUrl", encode=false) String establishmentUrl, Callback<ResponseWrapper<EstablishmentResponse>> cb);

    @GET("/{businessUrl}/pages")
    void getPages(@Path(value="businessUrl", encode=false) String businessUrl, Callback<ResponseWrapper<PagesResponse>> cb);

    @Multipart
    @POST("/account/{accountId}/images/")
    Response uploadImage(@Header("authToken") String authToken, @Path("accountId") String accountId, @Part("image") TypedFile image, @Part("title") String title);

    @GET("/establishments/search")
    Observable<ResponseWrapper<EstablishmentsResponse>> searchEstablishments(@Query("query") String query);

    public static class ResponseWrapper<E> {
        public Meta meta;
        public E response;
    }

    public static class AccountWrapper {
        private Account account;

        public AccountWrapper(String id, String password, String name) {
            this.account = new Account(id, password, name);
        }

        public static class Account {
            private String id, name, password;

            public Account(String id, String password, String name) {
                this.id = id;
                this.password = password;
                this.name = name;
            }
        }
    }

    public static class AccountSignupResponse {
        public String authToken;
    }

    public static class AccountSigninResponse {
        public String authToken;
    }

    public static class BusinessResponse {
        public Business business;
    }

    public static class CategoriesResponse {
        public List<Category> categories;
    }

    public static class EstablishmentResponse {
        public Establishment establishment;
    }

    public static class EstablishmentsResponse {
        public List<Establishment> establishments;
    }

    public static class PagesResponse {
        public List<Page> pages;
    }

    public static class MarkersResponse {
        public String tile;
        public List<Establishment> establishments;
    }
}
