import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * adds jwt token to http requests for authentication
 */
@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {

    /**
     * add the jwt to the request
     */
    public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = sessionStorage.getItem('token');

        if (token) {
            const clonedRequest = request.clone({
                headers: request.headers.set('jwt', token)
            });

            return next.handle(clonedRequest);
        } else {
            return next.handle(request);
        }
    }
}
