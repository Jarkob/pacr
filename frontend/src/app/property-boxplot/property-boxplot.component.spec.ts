import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PropertyBoxplotComponent } from './property-boxplot.component';

describe('PropertyBoxplotComponent', () => {
  let component: PropertyBoxplotComponent;
  let fixture: ComponentFixture<PropertyBoxplotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PropertyBoxplotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PropertyBoxplotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
