import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AcademicDashboardComponent } from './academic-dashboard.component';

describe('DashboardComponent', () => {
  let component: AcademicDashboardComponent;
  let fixture: ComponentFixture<AcademicDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AcademicDashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AcademicDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
