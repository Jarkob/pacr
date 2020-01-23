import { COMMIT_HASH_DATA } from './../detail-view/detail-view-maximized.tokens';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { Component, Inject, HostListener } from '@angular/core';

const ESCAPE_KEY = 27;

@Component({
  selector: 'app-detail-view-maximized',
  templateUrl: './detail-view-maximized.component.html',
  styleUrls: ['./detail-view-maximized.component.css']
})
export class DetailViewMaximizedComponent {

  constructor(
    public dialogRef: DetailViewMaximizedRef,
    @Inject(COMMIT_HASH_DATA) public commitHash: string
  ) { }

  @HostListener('document:keydown', ['$event']) private handleKeydown(event: KeyboardEvent) {
    if (event.keyCode === ESCAPE_KEY) {
      this.dialogRef.close();
    }
  }

}
