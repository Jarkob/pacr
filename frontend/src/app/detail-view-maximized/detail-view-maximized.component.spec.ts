import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailViewMaximizedComponent } from './detail-view-maximized.component';

describe('DetailViewMaximizedComponent', () => {
  let component: DetailViewMaximizedComponent;
  let fixture: ComponentFixture<DetailViewMaximizedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetailViewMaximizedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailViewMaximizedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
