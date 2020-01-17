import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaderboardEventsComponent } from './leaderboard-events.component';

describe('LeaderboardEventsComponent', () => {
  let component: LeaderboardEventsComponent;
  let fixture: ComponentFixture<LeaderboardEventsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LeaderboardEventsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LeaderboardEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
