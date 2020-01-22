import { Injectable, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DiagramService {

  selectedCommit = new BehaviorSubject<string>('');

  selectCommit(sha: string) {
    this.selectedCommit.next(sha);
  }
}
