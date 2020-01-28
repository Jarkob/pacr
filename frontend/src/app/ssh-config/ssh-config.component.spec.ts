import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SshConfigComponent } from './ssh-config.component';

describe('SshConfigComponent', () => {
  let component: SshConfigComponent;
  let fixture: ComponentFixture<SshConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SshConfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SshConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
