package io.bega.kduino.datamodel;



import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by usuario on 30/07/15.
 */
public class KdUINOErrorHandler implements ErrorHandler {
    @Override public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
           // return new UnauthorizedException(cause);
        }
        return cause;
    }
}



