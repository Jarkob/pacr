import { Injectable, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DiagramService {

  selectedCommit = new BehaviorSubject<string>('');

  /**
   * select a commit
   * @param sha the hash of the commit to be selected
   */
  public selectCommit(sha: string) {
    this.selectedCommit.next(sha);
  }
}
