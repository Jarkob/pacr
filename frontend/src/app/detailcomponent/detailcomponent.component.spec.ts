import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailcomponentComponent } from './detailcomponent.component';

describe('DetailcomponentComponent', () => {
  let component: DetailcomponentComponent;
  let fixture: ComponentFixture<DetailcomponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetailcomponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailcomponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
