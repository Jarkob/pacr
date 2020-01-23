import { COMMIT_HASH_DATA } from './detail-view-maximized.tokens';
import { Injectable, Injector, ComponentRef } from '@angular/core';
import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal, PortalInjector } from '@angular/cdk/portal';

import { DetailViewMaximizedComponent } from './../detail-view-maximized/detail-view-maximized.component';

import { DetailViewMaximizedRef } from './detail-view-maximized-ref';

interface DetailViewMaximizedConfig {
  panelClass?: string;
  hasBackdrop?: boolean;
  backdropClass?: string;
  commitHash?: string;
}

const DEFAULT_CONFIG: DetailViewMaximizedConfig = {
  hasBackdrop: true,
  backdropClass: 'dark-backdrop',
  panelClass: 'tm-file-preview-dialog-panel',
  commitHash: null
};

@Injectable()
export class DetailViewMaximizerService {

  constructor(
    private injector: Injector,
    private overlay: Overlay) { }

  open(config: DetailViewMaximizedConfig = {}) {
    // Override default configuration
    const dialogConfig = { ...DEFAULT_CONFIG, ...config };

    // Returns an OverlayRef which is a PortalHost
    const overlayRef = this.createOverlay(dialogConfig);

    // Instantiate remote control
    const dialogRef = new DetailViewMaximizedRef(overlayRef);

    const overlayComponent = this.attachDialogContainer(overlayRef, dialogConfig, dialogRef);

    overlayRef.backdropClick().subscribe(_ => dialogRef.close());

    return dialogRef;
  }

  private createOverlay(config: DetailViewMaximizedConfig) {
    const overlayConfig = this.getOverlayConfig(config);
    return this.overlay.create(overlayConfig);
  }

  private attachDialogContainer(overlayRef: OverlayRef, config: DetailViewMaximizedConfig, dialogRef: DetailViewMaximizedRef) {
    const injector = this.createInjector(config, dialogRef);

    const containerPortal = new ComponentPortal(DetailViewMaximizedComponent, null, injector);
    const containerRef: ComponentRef<DetailViewMaximizedComponent> = overlayRef.attach(containerPortal);

    return containerRef.instance;
  }

  private createInjector(config: DetailViewMaximizedConfig, dialogRef: DetailViewMaximizedRef): PortalInjector {
    const injectionTokens = new WeakMap();

    injectionTokens.set(DetailViewMaximizedRef, dialogRef);
    injectionTokens.set(COMMIT_HASH_DATA, config.commitHash);

    return new PortalInjector(this.injector, injectionTokens);
  }

  private getOverlayConfig(config: DetailViewMaximizedConfig): OverlayConfig {
    const positionStrategy = this.overlay.position()
      .global()
      .centerHorizontally()
      .centerVertically();

    const overlayConfig = new OverlayConfig({
      hasBackdrop: config.hasBackdrop,
      backdropClass: config.backdropClass,
      panelClass: config.panelClass,
      scrollStrategy: this.overlay.scrollStrategies.block(),
      positionStrategy
    });

    return overlayConfig;
  }
}