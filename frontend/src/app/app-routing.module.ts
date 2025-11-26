import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { FlagListComponent } from "./features/flag-list/flag-list.component";

const routes: Routes = [
    { path: '', redirectTo: 'flags', pathMatch: 'full' },
    { path: 'flags', component: FlagListComponent },
    { path: '**', redirectTo: 'flags' }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {}
