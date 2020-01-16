import { LeaderboardEventsComponent } from './leaderboard-events/leaderboard-events.component';
import { BenchmarkingEventsComponent } from './benchmarking-events/benchmarking-events.component';
import { DiagramComponent } from './diagram/diagram.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'diagram', component: DiagramComponent },
  { path: 'events/benchmarking', component: BenchmarkingEventsComponent},
  { path: 'events/leaderboard', component: LeaderboardEventsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
