import { LoginComponent } from './login/login.component';
import { AuthenticationGuardService } from './services/authentication-guard.service';
import { AcademicDashboardComponent } from './academic-dashboard/academic-dashboard.component';
import { CompetitiveDashboardComponent } from './competitive-dashboard/competitive-dashboard.component';
import { AdminComponent } from './admin/admin.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  { path: '', redirectTo: 'competitive', pathMatch: 'full' },
  { path: 'admin', component: AdminComponent, canActivate: [AuthenticationGuardService] },
  { path: 'dashboard1', component: CompetitiveDashboardComponent },
  { path: 'dashboard2', component: AcademicDashboardComponent },
  { path: 'login', component: LoginComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
