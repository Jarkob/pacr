import { Injectable, isDevMode } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  url: string;

  constructor() {
    this.url = isDevMode() ? 'http://localhost:8080' : 'https://pacr.aaaaaaah.de';
  }

  /**
   * Get the url of the backend.
   */
  public getBackendURL(): string {
    return this.url;
  }
}
