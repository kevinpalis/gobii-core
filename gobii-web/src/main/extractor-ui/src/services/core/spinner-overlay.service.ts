import { Injectable } from "@angular/core";
import { OverlayRef, Overlay, OverlayPositionBuilder } from "@angular/cdk/overlay";
import { ComponentPortal } from '@angular/cdk/portal';
import { SpinnerOverlayComponent } from 'src/views/spinner-overlay.component';
import { defer, NEVER } from 'rxjs';
import { finalize, share } from 'rxjs/operators';
import { GalleriaThumbnails } from 'primeng';

@Injectable({
    providedIn: 'root',
})
export class SpinnerOverlayService {
    private overlayRef: OverlayRef = undefined;

    constructor(
        private overlayPositionBuilder: OverlayPositionBuilder,
        private overlay: Overlay) {}

    private show(): void {
        Promise.resolve(null).then(() => {
            if (!this.overlayRef) {
                this.overlayRef = this.overlay.create({
                    positionStrategy: this.overlayPositionBuilder
                    .global()
                    .centerHorizontally()
                    .centerVertically(),
                    hasBackdrop: true,

                });
                this.overlayRef.attach(new ComponentPortal(SpinnerOverlayComponent));

            }
        });
    }

    private hide(): void {
        
        if (this.overlayRef) {
            this.overlayRef.detach();
            this.overlayRef = undefined;
        }
    }

    public readonly spinner$ = defer(() => {
        this.show();
        return NEVER.pipe(
            finalize( () => {
                this.hide();
            })
        );
    }).pipe(share());

}