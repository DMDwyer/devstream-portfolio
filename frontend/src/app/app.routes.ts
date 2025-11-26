import { Routes } from '@angular/router';
import { FlagListComponent } from './features/flag-list/flag-list.component';

export const routes: Routes = [
	{ path: '', redirectTo: 'flags', pathMatch: 'full' },
	{ path: 'flags', component: FlagListComponent }
];
