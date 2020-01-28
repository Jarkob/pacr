import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBenchmarksComponent } from './admin-benchmarks.component';

describe('AdminBenchmarksComponent', () => {
  let component: AdminBenchmarksComponent;
  let fixture: ComponentFixture<AdminBenchmarksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminBenchmarksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminBenchmarksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
