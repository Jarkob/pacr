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
    MatFormFieldModule,
    MatInputModule,
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
        MatTableModule,
        MatFormFieldModule,
        MatInputModule
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
        MatTableModule,
        MatFormFieldModule,
        MatInputModule
    ]
})
export class MaterialModule { }
