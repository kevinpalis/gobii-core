import { Injectable } from "@angular/core";
import { HttpInterceptor, HttpHandler, HttpRequest, HttpEvent } from '@angular/common/http';
import { SpinnerOverlayService } from './spinner-overlay.service';
import { Observable, Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';


@Injectable()
export class SpinnerInterceptor implements HttpInterceptor {

    constructor(private readonly spinnerOverlayService: SpinnerOverlayService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const spinnerSubscription: Subscription = this.spinnerOverlayService.spinner$.subscribe();
        return next.handle(req).pipe(finalize( () => spinnerSubscription.unsubscribe()));
    }
    
}