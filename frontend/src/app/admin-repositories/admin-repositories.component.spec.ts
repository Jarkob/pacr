import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRepositoriesComponent } from './admin-repositories.component';

describe('AdminRepositoriesComponent', () => {
  let component: AdminRepositoriesComponent;
  let fixture: ComponentFixture<AdminRepositoriesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminRepositoriesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminRepositoriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
