import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlagList } from './flag-list';

describe('FlagList', () => {
  let component: FlagList;
  let fixture: ComponentFixture<FlagList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlagList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlagList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
