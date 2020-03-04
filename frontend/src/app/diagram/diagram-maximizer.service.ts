import { Dataset } from './../classes/dataset';
import { BenchmarkProperty } from './../classes/benchmark-property';
import { Benchmark } from './../classes/benchmark';
import { SELECTED_BENCHMARK, SELECTED_PROPERTY, SELECTED_DATASETS } from './diagram-maximized.tokens';
import { DiagramMaximizedComponent } from './../diagram-maximized/diagram-maximized.component';
import { Injectable, Injector, ComponentRef } from '@angular/core';
import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal, PortalInjector } from '@angular/cdk/portal';

import { DiagramMaximizedRef } from './diagram-maximized-ref';

interface DiagramMaximizedConfig {
  panelClass?: string;
  hasBackdrop?: boolean;
  selectedBenchmark?: Benchmark;
  selectedProperty?: BenchmarkProperty;
  selectedDatasets?: Dataset[];
}

const DEFAULT_CONFIG: DiagramMaximizedConfig = {
  hasBackdrop: true,
  panelClass: 'tm-file-preview-dialog-panel',
  selectedBenchmark: null,
  selectedProperty: null,
  selectedDatasets: null
};

@Injectable()
export class DiagramMaximizerService {

  constructor(
    private injector: Injector,
    private overlay: Overlay) { }

  open(config: DiagramMaximizedConfig = {}) {
    // Override default configuration
    const dialogConfig = { ...DEFAULT_CONFIG, ...config };

    // Returns an OverlayRef which is a PortalHost
    const overlayRef = this.createOverlay(dialogConfig);

    // Instantiate remote control
    const dialogRef = new DiagramMaximizedRef(overlayRef);

    const overlayComponent = this.attachDialogContainer(overlayRef, dialogConfig, dialogRef);

    overlayRef.backdropClick().subscribe(_ => dialogRef.close());

    return dialogRef;
  }

  private createOverlay(config: DiagramMaximizedConfig) {
    const overlayConfig = this.getOverlayConfig(config);
    return this.overlay.create(overlayConfig);
  }

  private attachDialogContainer(overlayRef: OverlayRef, config: DiagramMaximizedConfig, dialogRef: DiagramMaximizedRef) {
    const injector = this.createInjector(config, dialogRef);

    const containerPortal = new ComponentPortal(DiagramMaximizedComponent, null, injector);
    const containerRef: ComponentRef<DiagramMaximizedComponent> = overlayRef.attach(containerPortal);

    return containerRef.instance;
  }

  private createInjector(config: DiagramMaximizedConfig, dialogRef: DiagramMaximizedRef): PortalInjector {
    const injectionTokens = new WeakMap();

    injectionTokens.set(DiagramMaximizedRef, dialogRef);
    injectionTokens.set(SELECTED_BENCHMARK, config.selectedBenchmark);
    injectionTokens.set(SELECTED_PROPERTY, config.selectedProperty);
    injectionTokens.set(SELECTED_DATASETS, config.selectedDatasets);

    return new PortalInjector(this.injector, injectionTokens);
  }

  private getOverlayConfig(config: DiagramMaximizedConfig): OverlayConfig {
    const positionStrategy = this.overlay.position()
      .global()
      .centerHorizontally()
      .centerVertically();

    const overlayConfig = new OverlayConfig({
      hasBackdrop: config.hasBackdrop,
      panelClass: config.panelClass,
      scrollStrategy: this.overlay.scrollStrategies.block(),
      positionStrategy
    });

    return overlayConfig;
  }
}
