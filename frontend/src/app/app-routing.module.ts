import { AcademicDashboardComponent } from './academic-dashboard/academic-dashboard.component';
import { CompetitiveDashboardComponent } from './competitive-dashboard/competitive-dashboard.component';
import { AdminComponent } from './admin/admin.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'competitive', component: CompetitiveDashboardComponent },
  { path: 'academic', component: AcademicDashboardComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
