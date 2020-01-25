import {
    MatToolbarModule,
    MatSidenavModule,
    MatGridListModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatTooltipModule,
    MatTabsModule,
    MatExpansionModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatListModule,
    MatCheckboxModule,
    MatTreeModule,
} from '@angular/material';
import { NgModule } from '@angular/core';

@NgModule({
    imports: [
        MatToolbarModule,
        MatSidenavModule,
        MatGridListModule,
        MatTabsModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatTooltipModule,
        MatExpansionModule,
        MatTableModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatListModule,
        MatCheckboxModule,
        MatTreeModule
    ],
    exports: [
        MatToolbarModule,
        MatSidenavModule,
        MatGridListModule,
        MatTabsModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatTooltipModule,
        MatExpansionModule,
        MatTableModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatListModule,
        MatCheckboxModule,
        MatTreeModule
    ]
})
export class MaterialModule { }
