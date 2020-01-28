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
    MatChipsModule,
    MatCheckbox,
    MatRadioModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule
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
        MatTreeModule,
        MatChipsModule,
        MatRadioModule,
        MatDatepickerModule,
        MatSnackBarModule
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
        MatChipsModule,
        MatCheckbox,
        MatCheckboxModule,
        MatTreeModule,
        MatRadioModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatSnackBarModule
    ]
})
export class MaterialModule { }
