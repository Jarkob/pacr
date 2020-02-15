import { LoginComponent } from './login/login.component';
import { AuthenticationGuardService } from './services/authentication-guard.service';
import { CompetitiveDashboardComponent } from './competitive-dashboard/competitive-dashboard.component';
import { AdminComponent } from './admin/admin.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  { path: '', component: CompetitiveDashboardComponent },
  { path: 'admin', component: AdminComponent, canActivate: [AuthenticationGuardService] },
  { path: 'login', component: LoginComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
