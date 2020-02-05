import { Injectable, isDevMode } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  url: string;

  constructor() {
    this.url = isDevMode() ? 'http://localhost:8080' : 'http://localhost:8080';
  }
}
