import { Injectable, isDevMode } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  url: string;

  constructor() {
    this.url = isDevMode() ? 'http://localhost:8080' : 'http://todo.insert.remote.url'; // 3100 for stoplight
  }
}
