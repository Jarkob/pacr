import { ConfigService } from './config.service';
import { Injectable, isDevMode } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  url: string;

  constructor(private configService: ConfigService) {
    this.url = isDevMode() ? configService.getConfig().devUrl : configService.getConfig().prodUrl;
  }

  /**
   * Get the url of the backend.
   */
  public getBackendURL(): string {
    return this.url;
  }
}
