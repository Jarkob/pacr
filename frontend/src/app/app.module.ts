import { MaterialModule } from './material.module';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ComparisonComponent } from './comparison/comparison.component';
import { DetailcomponentComponent } from './detailcomponent/detailcomponent.component';
import { DiagramComponent } from './diagram/diagram.component';
import { CommitHistoryComponent } from './commit-history/commit-history.component';
import { EventComponent } from './event/event.component';
import { AdminComponent } from './admin/admin.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { ToolbarComponent } from './toolbar/toolbar.component';

@NgModule({
  declarations: [
    AppComponent,
    ComparisonComponent,
    DetailcomponentComponent,
    DiagramComponent,
    CommitHistoryComponent,
    EventComponent,
    AdminComponent,
    DashboardComponent,
    LoginComponent,
    ToolbarComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
