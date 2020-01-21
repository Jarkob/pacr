import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetitiveDashboardComponent } from './competitive-dashboard.component';

describe('DashboardComponent', () => {
  let component: CompetitiveDashboardComponent;
  let fixture: ComponentFixture<CompetitiveDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CompetitiveDashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompetitiveDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
