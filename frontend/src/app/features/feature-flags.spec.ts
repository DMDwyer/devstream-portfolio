import { TestBed } from '@angular/core/testing';

import { FeatureFlags } from './feature-flags';

describe('FeatureFlags', () => {
  let service: FeatureFlags;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeatureFlags);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
