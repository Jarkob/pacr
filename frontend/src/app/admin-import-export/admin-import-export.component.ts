import { ImportExportService } from './../services/import-export.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-import-export',
  templateUrl: './admin-import-export.component.html',
  styleUrls: ['./admin-import-export.component.css']
})
export class AdminImportExportComponent implements OnInit {

  constructor(
    private importExportService: ImportExportService
  ) { }

  file: any;
  loading = false;

  ngOnInit() {

  }

  fileChanged(e) {
    this.file = e.target.files[0];
    console.log(this.file);
  }

  import() {
    this.loading = true;
    const fileReader = new FileReader();

    fileReader.onload = (e) => {
      this.importExportService.import(fileReader.result).subscribe(
        data => {
          this.loading = false;
        },
        err => {
          this.loading = false;
        }
      );
    };

    fileReader.readAsText(this.file);
  }

}
