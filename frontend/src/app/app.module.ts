import { AdminImportExportComponent } from './admin-import-export/admin-import-export.component';
import { DetailViewComparisonWrapperComponent } from './detail-view-comparison-wrapper/detail-view-comparison-wrapper.component';
import { TimeAgoPipe } from 'time-ago-pipe';
import { EnumPipe } from './pipes/enum-pipe';
import { DetailViewService } from './services/detail-view.service';
import { CommitHistoryJobQueueWrapperComponent } from './commit-history-job-queue-wrapper/commit-history-job-queue-wrapper.component';
import { FooterComponent } from './footer/footer.component';
import { CommitComparisonTableComponent } from './commit-comparison-table/commit-comparison-table.component';
import { BenchmarkingResultTableComponent } from './benchmarking-result-table/benchmarking-result-table.component';
import { SystemEnvironmentDisplayComponent } from './system-environment-display/system-environment-display.component';
import { CommitDetailsComponent } from './commit-details/commit-details.component';
import { CommitComparisonService } from './comparison/commit-comparison.service';
import { CommitComparisonComponent } from './commit-comparison/commit-comparison.component';
import { AuthenticationInterceptor } from './services/authentication.interceptor';
import { SshConfigComponent } from './ssh-config/ssh-config.component';
import { AdminBenchmarksComponent } from './admin-benchmarks/admin-benchmarks.component';
import { AdminRepositoriesComponent } from './admin-repositories/admin-repositories.component';
import { CommitHistoryMaximizedComponent } from './commit-history-maximized/commit-history-maximized.component';
import { CommitHistoryMaximizerService } from './commit-history/commit-history-maximizer.service';
import { ShortenStringPipe } from './pipes/shorten-string-pipe';
import { BrachesPipe } from './pipes/braces-pipe';
import { DiagramMaximizerService } from './diagram/diagram-maximizer.service';
import { DetailViewMaximizerService } from './detail-view/detail-view-maximizer.service';
import { GlobalService } from './services/global.service';
import { SchedulerService } from './services/scheduler.service';
import { RepositoryService } from './services/repository.service';
import { ImportExportService } from './services/import-export.service';
import { EventService } from './services/event.service';
import { BenchmarkService } from './services/benchmark.service';
import { BenchmarkingResultService } from './services/benchmarking-result.service';

import { AppComponent } from './app.component';
import { BenchmarkerListComponent } from './benchmarker-list/benchmarker-list.component';
import { JobQueueComponent } from './job-queue/job-queue.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { EventsComponent } from './events/events.component';
import { DetailViewComponent } from './detail-view/detail-view.component';
import { DetailViewMaximizedComponent } from './detail-view-maximized/detail-view-maximized.component';
import { ComparisonComponent } from './comparison/comparison.component';
import { DiagramComponent } from './diagram/diagram.component';
import { DiagramMaximizedComponent } from './diagram-maximized/diagram-maximized.component';
import { CommitHistoryComponent } from './commit-history/commit-history.component';
import { AdminComponent } from './admin/admin.component';
import { LoginComponent } from './login/login.component';
import { ToolbarComponent } from './toolbar/toolbar.component';

import { MaterialModule } from './material.module';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ChartsModule } from 'ng2-charts';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule, PercentPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

import { DragDropModule} from '@angular/cdk/drag-drop';
import { DateAdapter } from '@angular/material';

@NgModule({
  declarations: [
    AppComponent,
    ComparisonComponent,
    DiagramComponent,
    DiagramMaximizedComponent,
    CommitHistoryComponent,
    CommitHistoryMaximizedComponent,
    AdminComponent,
    AdminRepositoriesComponent,
    AdminBenchmarksComponent,
    SshConfigComponent,
    LoginComponent,
    ToolbarComponent,
    DetailViewComponent,
    DetailViewMaximizedComponent,
    EventsComponent,
    DashboardComponent,
    JobQueueComponent,
    BenchmarkerListComponent,
    BrachesPipe,
    ShortenStringPipe,
    FooterComponent,
    CommitComparisonComponent,
    CommitDetailsComponent,
    SystemEnvironmentDisplayComponent,
    BenchmarkingResultTableComponent,
    CommitComparisonTableComponent,
    TimeAgoPipe,
    EnumPipe,
    CommitHistoryJobQueueWrapperComponent,
    DetailViewComparisonWrapperComponent,
    AdminImportExportComponent
  ],
  entryComponents: [
    DetailViewMaximizedComponent,
    DiagramMaximizedComponent,
    CommitHistoryMaximizedComponent,
    CommitComparisonComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    ChartsModule,
    CommonModule,
    HttpClientModule,
    FlexLayoutModule,
    FormsModule,
    ReactiveFormsModule,
    DragDropModule
  ],
  providers: [
    DetailViewMaximizerService,
    CommitHistoryMaximizerService,
    DiagramMaximizerService,
    CommitComparisonService,
    BenchmarkingResultService,
    BenchmarkService,
    EventService,
    ImportExportService,
    RepositoryService,
    SchedulerService,
    DetailViewService,
    GlobalService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthenticationInterceptor,
      multi: true
    }
  ],
  exports: [
    BrachesPipe,
    ShortenStringPipe,
    PercentPipe,
    EnumPipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
