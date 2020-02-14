import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DetailViewService {

  selectedCommit = new BehaviorSubject<string>('');

  /**
   * select a commit
   * @param commitHash the hash of the commit to be selected
   */
  public selectCommit(commitHash: string) {
    this.selectedCommit.next(commitHash);
  }
}
