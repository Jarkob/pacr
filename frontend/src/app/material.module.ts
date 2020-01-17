import {
    MatToolbarModule,
    MatSidenavModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatTooltipModule,
    MatTabsModule,
    MatExpansionModule,
    MatTableModule,
} from '@angular/material';
import { NgModule } from '@angular/core';

@NgModule({
    imports: [
        MatToolbarModule,
        MatSidenavModule,
        MatTabsModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatTooltipModule,
        MatExpansionModule,
        MatTableModule
    ],
    exports: [
        MatToolbarModule,
        MatSidenavModule,
        MatTabsModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatTooltipModule,
        MatExpansionModule,
        MatTableModule
    ]
})
export class MaterialModule { }
