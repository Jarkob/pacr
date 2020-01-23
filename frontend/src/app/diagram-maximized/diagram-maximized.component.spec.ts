import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DiagramMaximizedComponent } from './diagram-maximized.component';

describe('DiagramMaximizedComponent', () => {
  let component: DiagramMaximizedComponent;
  let fixture: ComponentFixture<DiagramMaximizedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DiagramMaximizedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DiagramMaximizedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
