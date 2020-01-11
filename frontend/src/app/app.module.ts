import { GlobalService } from './services/global.service';
import { SchedulerService } from './services/scheduler.service';
import { RepositoryService } from './services/repository.service';
import { ImportExportService } from './services/import-export.service';
import { EventService } from './services/event.service';
import { BenchmarkService } from './services/benchmark.service';
import { BenchmarkingResultService } from './services/benchmarking-result.service';
import { MaterialModule } from './material.module';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ChartsModule } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ComparisonComponent } from './comparison/comparison.component';
import { DiagramComponent } from './diagram/diagram.component';
import { CommitHistoryComponent } from './commit-history/commit-history.component';
import { AdminComponent } from './admin/admin.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { ToolbarComponent } from './toolbar/toolbar.component';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    ComparisonComponent,
    DiagramComponent,
    CommitHistoryComponent,
    AdminComponent,
    DashboardComponent,
    LoginComponent,
    ToolbarComponent,
    BenchmarkingEventsComponent,
    LeaderboardEventsComponent,
    DetailViewComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    ChartsModule,
    HttpClientModule
  ],
  providers: [
    BenchmarkingResultService,
    BenchmarkService,
    EventService,
    ImportExportService,
    RepositoryService,
    SchedulerService,
    GlobalService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
