import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailViewComparisonWrapperComponent } from './detail-view-comparison-wrapper.component';

describe('DetailViewComparisonWrapperComponent', () => {
  let component: DetailViewComparisonWrapperComponent;
  let fixture: ComponentFixture<DetailViewComparisonWrapperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetailViewComparisonWrapperComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailViewComparisonWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
