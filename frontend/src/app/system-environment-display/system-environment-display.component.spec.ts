import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemEnvironmentDisplayComponent } from './system-environment-display.component';

describe('SystemEnvironmentDisplayComponent', () => {
  let component: SystemEnvironmentDisplayComponent;
  let fixture: ComponentFixture<SystemEnvironmentDisplayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SystemEnvironmentDisplayComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SystemEnvironmentDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
