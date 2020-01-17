import { Injectable, isDevMode } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  url: string;

  constructor() {
    this.url = isDevMode() ? 'http://localhost:3100' : 'http://todo.insert.remote.url';
  }
}
