import { MatToolbarModule, MatSidenavModule } from '@angular/material';
import { NgModule } from '@angular/core';

@NgModule({
    imports: [
        MatToolbarModule,
        MatSidenavModule
    ],
    exports: [
        MatToolbarModule,
        MatSidenavModule
    ]
})
export class MaterialModule { }
