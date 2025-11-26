import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureFlagsService } from '../feature-flags.service';
import { FeatureFlag } from '../feature-flag.model';

@Component({
  selector: 'app-flag-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './flag-list.component.html',
  styleUrls: ['./flag-list.component.scss']
})
export class FlagListComponent implements OnInit {
  flags: FeatureFlag[] = [];
  loading = false;
  error?: string;

  constructor(private flagsService: FeatureFlagsService) {}

  ngOnInit(): void {
    this.loadFlags();
  }

  loadFlags(): void {
    this.loading = true;
    this.error = undefined;
    this.flagsService.getAll().subscribe({
      next: (flags) => {
        this.flags = flags;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load feature flags.';
        this.loading = false;
      }
    });
  }

  onToggle(flag: FeatureFlag): void {
    if (!flag.id) { return; }
    const newValue = !flag.enabled;
    this.flagsService.toggle(flag.id, newValue).subscribe({
      next: updated => {
        flag.enabled = updated.enabled;
      },
      error: err => {
        console.error(err);
      }
    });
}
}